package com.starunion.jee.fsdiserver.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.starunion.jee.fsdiserver.po.ClientBindInfo;
import com.starunion.jee.fsdiserver.thread.ClientTcpSocket;
import com.starunion.jee.fsdiserver.thread.FsTcpSocket;


@Service
public class ProcFsResponse implements IMsgTypeDef {
	private static final Logger logger = LoggerFactory.getLogger(ProcFsResponse.class);
	@Autowired
	InitTerStatus activeMap;
	@Autowired
	FsTcpSocket fsTcpSocket;
	@Autowired
	ClientTcpSocket cliTcpSocket;
	@Autowired
	InitTerStatus initTerStatus;
	
	public ProcFsResponse() {

	}

	public Map<String, String> parseFsResponse(StringBuffer buff) {
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer line = new StringBuffer();
		for (int i = 0; i < buff.length(); i++) {
			char c;
			if ((c = buff.charAt(i)) != '\n') {
				line.append(c);
			} else {
				// logger.debug("Parse line = {}", line);
				String key = "";
				String value = "";
				int pos = line.indexOf(":");
				if (pos != -1) {
					// logger.debug("pos = {}", pos);
					key = line.substring(0, pos);
					// logger.debug("Parse key = {}", key);
					value = line.substring(pos + 1).trim();
					/**
					 * it seems urlDecode can be invoke when need the value
					 * within Map, but for logic design,here seems better.
					 */
					if (value.contains("%")) {
						String newVal;
						try {
							newVal = new String(java.net.URLDecoder.decode(value, "utf-8"));
							map.put(key, newVal);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						// logger.debug("Parse newValue = {}", newVal);
					} else {
						// logger.debug("Parse value = {}", value);
						map.put(key, value);
					}
				}
				line.delete(0, line.length());
			}
		}
		return map;
	}

	@SuppressWarnings("static-access")
	public int procFsResponse(StringBuffer strBuff) {
		Map<String, String> respMap = new HashMap<String, String>();
		// logger.debug("receive response message from FreeSWITCH:======>\n{}",
		// strBuff.toString());
		respMap = parseFsResponse(strBuff);
		String contentType = respMap.get("Content-Type");
		if (contentType != null) {
			if (contentType.equals("auth/request")) {
				fsTcpSocket.fsSendCommand("auth ClueCon\n\n");
			} else if (contentType.equals("command/reply")) {
				String reply = respMap.get("Reply-Text");
				String[] parts = reply.split(" ");
				// if (parts[0].equals("+OK")) {
				// logger.debug("successful reply");
				// } else {
				// logger.debug("unsuccessful reply");
				// }
				if (parts[1].equals("accepted")) {
					fsTcpSocket.fsSendCommand("event plain HEARTBEAT\n\n");
					fsTcpSocket.fsSendCommand("event plain CHANNEL_CALLSTATE\n\n");
					fsTcpSocket.fsSendCommand("event plain CHANNEL_PARK\n\n");
					fsTcpSocket.fsSendCommand("event plain CUSTOM sofia::register\n\n");
				}
			} else {
				logger.debug("get content type [{}] without process.", contentType);
			}
		} else {
			String eventType = respMap.get("Event-Name");
			if (eventType.equals(EVENT_CUSTOM)) {
				String subType = respMap.get("Event-Subclass");
				logger.debug("Receive notify type CUSTOM, subclass {}", subType);
				if (subType.equals("sofia::register")) {
					logger.debug("register!!!");
				}
			} else if (eventType.equals(EVENT_HEARBEAT)) {
				logger.debug("hearbeat!!!");
			} else if (eventType.equals(EVENT_CHANNEL_CALLSTATE)) {
				String status = respMap.get("Answer-State");
				logger.debug("Receive notify type CHANNEL_CALLSTATE, status {}", status);
				StringBuffer notifyBuff = new StringBuffer();
				if (status.equals("ringing")) {
					String callDirec = respMap.get("Caller-Logical-Direction");
					String callerAni = respMap.get("Caller-ANI");
					String callee = respMap.get("Caller-Destination-Number");
					String uuid = respMap.get("Caller-Unique-ID");
					if (callDirec.equals("inbound")) {
						activeMap.activeUserMap.get(callerAni).setCallStatus(TER_STATUS_RING);
						activeMap.activeUserMap.get(callerAni).setOwnUuid(uuid);
						notifyBuff.append("peer:").append(callerAni).append(":").append(TER_STATUS_FRING).append(":").append(callee).append("\r\n");
						logger.debug("terminal {}, uuid = {}", callerAni,
								activeMap.activeUserMap.get(callerAni).getOwnUuid());
					} else {
						activeMap.activeUserMap.get(callee).setCallStatus(TER_STATUS_RING);
						activeMap.activeUserMap.get(callee).setOwnUuid(uuid);
						notifyBuff.append("peer:").append(callee).append(":").append(TER_STATUS_RING).append(":").append(callerAni).append("\r\n");
						logger.debug("terminal {}, uuid = {}", callee,
								activeMap.activeUserMap.get(callee).getOwnUuid());
					}
					SendClientNotify(notifyBuff.toString());
				} else if (status.equals("answered")) {
					/** when Channel-Call-State: HELD; Caller-Logical-Direction="inbound" */
					String callDirec = respMap.get("Caller-Direction");
					String callerAni = respMap.get("Caller-ANI");
					String callee = respMap.get("Caller-Destination-Number");
//					String otherUuid = respMap.get("Other-Leg-Unique-ID");
					//TODO:here need be updated to suit for when caller is "000",for bridge/delimotion call.
					if (callDirec.equals("inbound")) {
						activeMap.activeUserMap.get(callerAni).setCallStatus(TER_STATUS_ANSWER);
						activeMap.activeUserMap.get(callerAni).setCallWith(callee);
						notifyBuff.append("peer:").append(callerAni).append(":").append(TER_STATUS_ANSWER).append(":")
								.append(callee).append("\r\n");
					} else {
						activeMap.activeUserMap.get(callee).setCallStatus(TER_STATUS_ANSWER);
						activeMap.activeUserMap.get(callee).setCallWith(callerAni);
						notifyBuff.append("peer:").append(callee).append(":").append(TER_STATUS_ANSWER).append(":")
								.append(callerAni).append("\r\n");
					}
					SendClientNotify(notifyBuff.toString());
				} else if (status.equals("hangup")) {
//					logger.debug("receive unprocess fs notify : {}", strBuff.toString());
//					String callDirec = respMap.get("Caller-Logical-Direction");
					String callDirec = respMap.get("Caller-Direction");
					String callerAni = respMap.get("Caller-ANI");
					String callee = respMap.get("Caller-Destination-Number");
					//only for pickup ? Caller-Caller-ID-Number
					if(callee.equals(FUNCODE_PICKUP)){
						callee = respMap.get("Caller-RDNIS");
					}else if(callee.contains(FS_MEET_NORMAL_TYPE)||callee.contains(FS_MEET_MUTE_TYPE)){
						String meetNum = callee;
						callee = respMap.get("Caller-RDNIS");
						Map<String, String> callMap = initTerStatus.specialCallMap;
						if(callMap.get(meetNum) != null){
							String caller = callMap.get(meetNum).split(":")[1];
							if(caller.equals(callee)){
								StringBuffer buff = new StringBuffer();
								buff.append("bgapi conference ");
								buff.append(meetNum);
								buff.append(" kick all");
								buff.append("\n\n");
								fsTcpSocket.fsSendCommand(buff.toString());
								callMap.remove(meetNum);
							}	
						}
						
					}
					
					if (callDirec.equals("inbound")) {
						activeMap.activeUserMap.get(callerAni).setCallStatus(TER_STATUS_REG);
						notifyBuff.append("peer:").append(callerAni).append(":").append(TER_STATUS_REG).append("\r\n");
					} else {
//						logger.debug("receive outbound hangup : {}", strBuff.toString());
						activeMap.activeUserMap.get(callee).setCallStatus(TER_STATUS_REG);
						notifyBuff.append("peer:").append(callee).append(":").append(TER_STATUS_REG).append("\r\n");
					}
					SendClientNotify(notifyBuff.toString());
				}
			} else if (eventType.equals(EVENT_CHANNEL_PARK)) {
				String callInd = respMap.get("Caller-Destination-Number");
				String uuid = respMap.get("Caller-Unique-ID");
				
				Map<String,String> map = initTerStatus.specialCallMap;
				String[] parts = map.get(callInd).split(":");
				StringBuffer buff = new StringBuffer();
				buff.append("bgapi uuid_bridge ");
				buff.append(uuid);
				buff.append(" ");
				if(parts[0].equals("bridge")||parts[0].equals("queueanswer")){
					buff.append(activeMap.activeUserMap.get(parts[1]).getOwnUuid());
				}else if(parts[0].equals("demolition")||parts[0].equals("transfer")){
					buff.append(activeMap.activeUserMap.get(activeMap.activeUserMap.get(parts[1]).getCallWith()).getOwnUuid());
				}
				buff.append("\n\n");
				fsTcpSocket.fsSendCommand(buff.toString());
				map.remove(callInd);
				
			} else {
				logger.debug("receive unprocess fs notify : {}", strBuff.toString());
			}
			
		}

		return 0;
	}

	@SuppressWarnings("static-access")
	public void SendClientNotify(String body) {
		Map<String, ClientBindInfo> map = cliTcpSocket.clientInfoMap;
		Iterator<Map.Entry<String, ClientBindInfo>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			logger.debug("send notify to client : {}", body);
			Map.Entry<String, ClientBindInfo> entry = iter.next();
			try {
				entry.getValue().getWriter().write(body);
				entry.getValue().getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
