package com.starunion.jee.fsdiserver.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.starunion.jee.fsdiserver.dao.DaoIntercomGroup;
import com.starunion.jee.fsdiserver.dao.DaoIntercomMember;
import com.starunion.jee.fsdiserver.dao.DaoPrePlayInfo;
import com.starunion.jee.fsdiserver.dao.DaoUserGpsInfo;
import com.starunion.jee.fsdiserver.dao.DaoUserGpsTrail;
import com.starunion.jee.fsdiserver.dao.DaoUserSip;
import com.starunion.jee.fsdiserver.dao.DaoUserSipBlack;
import com.starunion.jee.fsdiserver.dao.DaoUserSipCamer;
import com.starunion.jee.fsdiserver.po.ClientBindInfo;
import com.starunion.jee.fsdiserver.po.IntercomGroup;
import com.starunion.jee.fsdiserver.po.IntercomMember;
import com.starunion.jee.fsdiserver.po.PrePlayInfo;
import com.starunion.jee.fsdiserver.po.SipActiveInfo;
import com.starunion.jee.fsdiserver.po.UserGpsInfo;
import com.starunion.jee.fsdiserver.po.UserGpsTrail;
import com.starunion.jee.fsdiserver.po.UserSip;
import com.starunion.jee.fsdiserver.po.UserSipBlack;
import com.starunion.jee.fsdiserver.po.UserSipCamer;
import com.starunion.jee.fsdiserver.service.timer.QuartzTaskService;
import com.starunion.jee.fsdiserver.thread.ClientTcpSocket;
import com.starunion.jee.fsdiserver.thread.FsTcpSocket;


@Service
public class ProcClientRequest implements IMsgTypeDef {
	private static final Logger logger = LoggerFactory.getLogger(ProcClientRequest.class);

	@Autowired
	private FsTcpSocket fsSocket;
	@Autowired
	private DaoUserSip daoUserSip;
	@Autowired
	private DaoUserSipBlack daoUserSipBlack;
	@Autowired
	private DaoUserSipCamer daoUserSipCamer;
	@Autowired
	private DaoPrePlayInfo daoPrePlayInfo;
	@Autowired
	private DaoIntercomGroup daoIntercomGroup;
	@Autowired
	private DaoIntercomMember daoIntercomMember;
	@Autowired
	private DaoUserGpsInfo daoUserGpsInfo;
	@Autowired
	private DaoUserGpsTrail daoUserGpsTrail;
	@Autowired
	private InitTerStatus initTerStatus;
	@Autowired
	private QuartzTaskService timerTask;
	@Autowired
	private ProcLinuxCommand procLinuxCmd;
	@Autowired
	ProcFsResponse procFsResponse;

	public ProcClientRequest() {

	}

	public StringBuffer procClientRequest(StringBuffer reqBuff) {
		StringBuffer rspBuff = new StringBuffer();
		String[] parts = reqBuff.toString().split(":");
		int partsLen = parts.length;

		if (parts[0].startsWith(DISP_DLOGIN)||parts[0].startsWith(DISP_LOGIN)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 3) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqLogin(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			} else if (procReqLogin(parts) == FAILURE) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_SHOW_DUSERS)||parts[0].startsWith(DISP_SHOW_USERS)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (parts[0].startsWith(DISP_SHOW_DUSERS) && partsLen < 2) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			/** TODO:maybe here need a more strict judge ? */
			rspBuff = procReqDisUsersList(parts);
			/** add a list over indicate */
			rspBuff.append(reqBuff);
			rspBuff = makeLastResponse(rspBuff, SUCCESS);
			return rspBuff;
		} else if (parts[0].startsWith(DISP_SHOW_PEERS)) {
			logger.debug("receive client request type [{}]", parts[0]);
			rspBuff = procReqSipUserStatus();
			rspBuff.append(reqBuff);
			rspBuff = makeLastResponse(rspBuff, SUCCESS);
			return rspBuff;
		} else if (parts[0].startsWith(DISP_KEEP_ALIVE)) {
			logger.debug("receive client request type [{}]", parts[0]);
			/** TODO:should add real logic here,connect with HERATBEAT */
			rspBuff = makeLastResponse(rspBuff, SUCCESS);
			return rspBuff;
		} else if (parts[0].startsWith(DISP_SHOW_NLIST)) {
			logger.debug("receive client request type [{}]", parts[0]);
			rspBuff = procReqNightUserList();
			rspBuff.append(reqBuff);
			rspBuff = makeLastResponse(rspBuff, SUCCESS);
			return rspBuff;
		} else if (parts[0].startsWith(DISP_SHOW_FLIST)) {
			logger.debug("receive client request type [{}]", parts[0]);
			rspBuff = procReqForbidUserList();
			rspBuff.append(reqBuff);
			rspBuff = makeLastResponse(rspBuff, SUCCESS);
			return rspBuff;
		} else if (parts[0].startsWith(DISP_SHOW_CAMERS)) {
			logger.debug("receive client request type [{}]", parts[0]);
			rspBuff = procReqCamerUserList();
			rspBuff.append(reqBuff);
			rspBuff = makeLastResponse(rspBuff, SUCCESS);
			return rspBuff;
		} else if (parts[0].startsWith(DISP_SHOW_BROAD_PLAY)) {
			logger.debug("receive client request type [{}]", parts[0]);
			rspBuff = procReqPrePlayList();
			rspBuff.append(reqBuff);
			rspBuff = makeLastResponse(rspBuff, SUCCESS);
			return rspBuff;
		} else if (parts[0].startsWith(DISP_THIRD_PCALL)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 3) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqThirdPartyCall(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_MONITOR_CALL)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 3) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqMonitorCall(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_INSERT_CALL)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 3) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqInsertCall(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_BRIDGE_CALL) || parts[0].startsWith(DISP_DEMOLION_CALL)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 3) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqBridgeCall(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_PICKUP_CALL)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 3) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqPickupCall(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_TRANS_CALL)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 3) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqTransCall(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_HANGUP_CALL)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 2) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqHangupCall(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_HANGUPMEET_CALL)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 2) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqHangupMeetCall(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_NIGHT_CALL) || (parts[0].startsWith(DISP_UNIGHT_CALL))) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (parts[0].startsWith(DISP_NIGHT_CALL) && partsLen < 3) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (parts[0].startsWith(DISP_UNIGHT_CALL) && partsLen < 2) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqNightCall(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_FORBID_CALL)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 3) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqForbidCall(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_RECORD_CALL)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 2) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqRecordCall(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_MEET_JOIN)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 3) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqMeetCall(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_MEET_KICK) || parts[0].startsWith(DISP_MEET_MUTE)
				|| parts[0].startsWith(DISP_MEET_UNMUTE)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 3) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqMeetProcCall(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_MEET_KICKALL)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 2) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqMeetProcCall(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_QUEUE_ANS)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 4) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqQueueProcCall(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_PAGE_CALL)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 3) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqPageCall(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_BROAD_PLAY)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 3) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqMeetPlayCall(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_BROAD_PLAY_DEL)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 2) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			if (procReqMeetPlayDel(parts) == SUCCESS) {
				rspBuff = makeLastResponse(reqBuff, SUCCESS);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_TALKIE_SHOW)) {
			logger.debug("receive client request type [{}]", parts[0]);
			rspBuff = procReqTalkieShow(parts);
			rspBuff.append(reqBuff);
			rspBuff = makeLastResponse(rspBuff, SUCCESS);
			return rspBuff;
		} else if (parts[0].startsWith(DISP_TALKIE_MEM_SHOW)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 2) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			rspBuff = procReqTalkieMemShow(parts);
			rspBuff.append(reqBuff);
			rspBuff = makeLastResponse(rspBuff, SUCCESS);
			return rspBuff;
		} else if (parts[0].startsWith(DISP_GPS_EXTEN_SHOW)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 2) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			rspBuff = procExtenGpsShow(parts);
			return rspBuff;
		} else if (parts[0].startsWith(DISP_GPS_EXTEN_SHOW_ALL)) {
			logger.debug("receive client request type [{}]", parts[0]);
			rspBuff = procExtenGpsShowAll();
			rspBuff.append(reqBuff);
			rspBuff = makeLastResponse(rspBuff, SUCCESS);
			return rspBuff;
		} else if (parts[0].startsWith(DISP_GPS_EXTEN_ADD)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 2) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			int res = procExtenGpsAdd(parts);
			rspBuff.append(reqBuff);
			if(res == 1){
				rspBuff = makeLastResponse(rspBuff, SUCCESS);
				//simply and directly
				StringBuffer strBuff = new StringBuffer();
				strBuff.append(DISP_GPS_EXTEN_NOTIFY);
				strBuff.append(":");
				strBuff.append(parts[1]).append(":");
				strBuff.append(parts[2]).append(":");
				strBuff.append(parts[3]).append("\r\n");
				procFsResponse.SendClientNotify(strBuff.toString());
			}else{
				rspBuff = makeLastResponse(rspBuff, FAILURE);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_GPS_EXTEN_DEL)) {
			logger.debug("receive client request type [{}]", parts[0]);
			if (partsLen < 2) {
				rspBuff = makeLastResponse(reqBuff, FAILURE);
				return rspBuff;
			}
			int res = procExtenGpsDel(parts);
			rspBuff.append(reqBuff);
			if(res == 1){
				rspBuff = makeLastResponse(rspBuff, SUCCESS);	
			}else{
				rspBuff = makeLastResponse(rspBuff, FAILURE);
			}
			return rspBuff;
		} else if (parts[0].startsWith(DISP_GPS_EXTEN_TRAIL)) {
			logger.debug("receive client request type [{}]", parts[0]);
		if (partsLen < 2) {
			rspBuff = makeLastResponse(reqBuff, FAILURE);
			return rspBuff;
		}
		rspBuff = procExtenGpsTrailShow(parts);
		rspBuff.append(reqBuff);
		rspBuff = makeLastResponse(rspBuff, SUCCESS);
		return rspBuff;
	} else {
			logger.info("receive unkown client request message type = {}", parts[0]);
			rspBuff = makeLastResponse(reqBuff, FAILURE);
			return rspBuff;
		}

	}
	
	private StringBuffer procExtenGpsTrailShow(String[] data) {
		List<UserGpsTrail> gpsList = new ArrayList<UserGpsTrail>();
		gpsList = daoUserGpsTrail.findByNumber(data[1]);
		StringBuffer buff = new StringBuffer();
		for (UserGpsTrail trail : gpsList) {
			buff.append("extenmapdata:");
			buff.append(trail.getExten()).append(":");
			buff.append(trail.getLng()).append(":");
			buff.append(trail.getLat()).append(":");
			buff.append(trail.getTime());
			buff.append("\r\n");
		}
		return buff;
	}
	
	private int procExtenGpsDel(String[] data) {
		String exten = data[1];
		UserGpsInfo extenInfo = daoUserGpsInfo.findByNumber(exten);
		if(extenInfo == null){
			return -1;
		}
		int res = daoUserGpsInfo.delExtenGps(exten);
		return res;
	}
	
	private int procExtenGpsAdd(String[] data) {
		String exten = data[1];
		String lng = data[2];
		String lat = data[3];
		UserGpsInfo extenInfo = daoUserGpsInfo.findByNumber(exten);
		if(extenInfo != null){
			daoUserGpsInfo.updateGpsByNumber(exten, lng, lat);
			daoUserGpsTrail.addExtenGpsTrail(exten, lng, lat);
			return -1;
		}
		int res = daoUserGpsInfo.addExtenGps(exten, lng, lat);
		return res;
	}
	
	private StringBuffer procExtenGpsShowAll() {
		List<UserGpsInfo> gpsList = new ArrayList<UserGpsInfo>();
		gpsList = daoUserGpsInfo.findAll();
		StringBuffer buff = new StringBuffer();
		int len = gpsList.size();
		for (int i = 0; i < len; i++) {
			buff.append("extenmapdata:");
			buff.append(gpsList.get(i).getExten()).append(":");
			buff.append(gpsList.get(i).getLng()).append(":");
			buff.append(gpsList.get(i).getLat());
			buff.append("\r\n");
		}
		return buff;
	}
	
	private StringBuffer procExtenGpsShow(String[] data) {
		String name = data[1];
		UserGpsInfo gpsInfo = new UserGpsInfo();
		gpsInfo = daoUserGpsInfo.findByNumber(name);
		StringBuffer buff = new StringBuffer();
		buff.append(data[0]);
		buff.append(":");
		buff.append(gpsInfo.getExten()).append(":");
		buff.append(gpsInfo.getLng()).append(":");
		buff.append(gpsInfo.getLat());
		buff.append("\r\n");
		
		return buff;
	}

	private StringBuffer procReqSendMsg(String[] data) {
		String to = data[1];
		String domain = data[3];
		String text = data[2];
		String len = Integer.toString(text.length());
		StringBuffer buff = new StringBuffer();
		buff.append("sendevent SEND_MESSAGE\n");
		buff.append("profile: internal\n");
		buff.append("user: sip:");
		buff.append(to);
		buff.append("\n");
		buff.append("host: ");
		buff.append(domain);
		buff.append(":5060\n");
		buff.append("content-type: text/plain\n");
		buff.append("content-length: ");
		buff.append(len);
		buff.append("\n\n");
		buff.append(text);
		buff.append(DISP_FSCMD_TAIL);
		fsSocket.fsSendCommand(buff.toString());
		return buff;
	}

	private StringBuffer procReqTalkieMemShow(String[] data) {
		String name = data[1];
		List<IntercomMember> group = new ArrayList<IntercomMember>();
		group = daoIntercomMember.findAllByGrpNumber(name);
		StringBuffer buff = new StringBuffer();
		int len = group.size();
		for (int i = 0; i < len; i++) {
			buff.append("intercomlist:");
			buff.append(group.get(i).getName()).append(":");
			buff.append(group.get(i).getExten());
			buff.append("\r\n");
		}
		return buff;
	}

	private StringBuffer procReqTalkieShow(String[] data) {
		List<IntercomGroup> group = new ArrayList<IntercomGroup>();
		group = daoIntercomGroup.findAll();
		StringBuffer buff = new StringBuffer();
		int len = group.size();
		for (int i = 0; i < len; i++) {
			buff.append("intercom:");
			buff.append(group.get(i).getName()).append(":");
			buff.append(group.get(i).getNumber());
			buff.append("\r\n");
		}
		return buff;
	}

	private int procReqMeetPlayDel(String[] data) {
		String id = data[1];

		PrePlayInfo info = daoPrePlayInfo.findById(id);
		String jobKey = info.getTkey();
		timerTask.deleteCycleTask(jobKey, "group1");

		daoPrePlayInfo.delPrePlayInfo(id);

		return SUCCESS;
	}

	@SuppressWarnings("static-access")
	private int procReqMeetPlayCall(String[] data) {
		/**
		 * message instance:
		 * playfile:20140624145300:0:1:10:a.mp3:611,612,613:655\r\n 预约模式：0 立即
		 * 1定时2每天3每周 播放模式：0次数播放1时长播放 时长或次数：时长指分钟，次数指文件播放次数 播放文件：多个以,分割 分机列表：以,分割
		 * 添加分机：添加用户的分机
		 */

		String planMode = data[2];
		String playMode = data[3];
		String timeInd = data[4];
		String playFile = data[5];
		String callees = data[6];
		String caller = data[7];

		Date time = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
		String timeStr = formatter.format(time);
		String tempMeetNum = FS_MEET_PLAY_TYPE + timeStr;
		Map<String, SipActiveInfo> map = initTerStatus.activeUserMap;

		if (planMode.equals(DISP_BROAD_PLANMOD_NOW)) {
			String[] callee = callees.split(",");
			int calleeLen = callee.length;
			for (int i = 0; i < calleeLen; i++) {
				String status = map.get(callee[i]).getCallStatus();
				if (status.equals(TER_STATUS_REG)) {
					StringBuffer buff = new StringBuffer();
					buff.append("bgapi originate {origination_caller_id_number=000}/user");
					buff.append(callee[i]);
					buff.append(" &transfer(");
					buff.append(tempMeetNum);
					buff.append(")");
					buff.append(DISP_FSCMD_TAIL);
					fsSocket.fsSendCommand(buff.toString());
					logger.debug("make command : {}", buff.toString());
				}
			}
			/** give soft broadcast terminal 1s delay time */
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			StringBuffer cmdBuff = new StringBuffer();
			if (playMode.equals(DISP_BROAD_PLAYMOD_TIMES)) {
				int playTimes = Integer.parseInt(timeInd);
				String musicStr = getBroadPlayMusicName(playFile);
				cmdBuff.append("bgapi conference ");
				cmdBuff.append(tempMeetNum);
				cmdBuff.append(" play file_string://");
				for (int i = 0; i < playTimes; i++) {
					cmdBuff.append(musicStr);
				}
			} else if (playMode.equals(DISP_BROAD_PLAYMOD_TIMELEN)) {
				int fileMSecond = getBroadPlayMusicLen(playFile);
				int timeMinute = Integer.parseInt(timeInd);
				int timeMSecond = timeMinute * 60 * 1000;
				int playTimes = 0;
				if (fileMSecond != 0) {
					playTimes = timeMSecond / fileMSecond;
				}
				cmdBuff.append("bgapi conference ");
				cmdBuff.append(tempMeetNum);
				cmdBuff.append(" play file_string://");

				String musicStr = getBroadPlayMusicName(playFile);
				for (int i = 0; i < playTimes; i++) {
					cmdBuff.append(musicStr);
				}
				/** start timer to stop at the indicate time point */
				timerTask.stopMeetPlay(fsSocket, tempMeetNum, timeMSecond);
			}
		} else {
			// "20140624145300"1定时2每天3每周
			String dateInd = data[1];
			StringBuffer cmdBuff = new StringBuffer();
			cmdBuff.append(data[0]).append(":");
			cmdBuff.append(data[1]).append(":");
			cmdBuff.append(DISP_BROAD_PLANMOD_NOW).append(":");
			cmdBuff.append(data[3]).append(":");
			cmdBuff.append(data[4]).append(":");
			cmdBuff.append(data[5]).append(":");
			cmdBuff.append(data[6]).append(":");
			cmdBuff.append(data[7]).append("\n\n");
			if (planMode.equals(DISP_BROAD_PLANMOD_FUTURE)) {
				formatter = new SimpleDateFormat("yyyyMMddhhmmss");
				try {
					long delayTime = formatter.parse(dateInd).getTime();
					timerTask.delayMeetPlayAtTime(cmdBuff, delayTime);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (planMode.equals(DISP_BROAD_PLANMOD_DAY)) {
				// 59 23 03 1-31 * ? *
				PrePlayInfo info = new PrePlayInfo();
				info.setTimestr(data[1]);
				info.setPmode(DISP_BROAD_PLANMOD_DAY);
				info.setTmode(data[3]);
				info.setTimes(data[4]);
				info.setFile(data[5]);
				info.setList(data[6]);
				info.setPlayer(data[7]);
				info.setTkey(tempMeetNum);
				daoPrePlayInfo.addPrePlayInfo(info);

				String hour = dateInd.substring(8, 9);
				String min = dateInd.substring(10, 11);
				String sec = dateInd.substring(12, 13);
				StringBuffer cronBuff = new StringBuffer();
				cronBuff.append(sec).append(" ");
				cronBuff.append(min).append(" ");
				cronBuff.append(hour).append(" ");
				cronBuff.append("1-31").append(" ");
				cronBuff.append("*").append(" ");
				cronBuff.append("?").append(" ");
				cronBuff.append("*");
				timerTask.delayMeetPlayAtCron(tempMeetNum, cmdBuff, cronBuff.toString());

			} else if (planMode.equals(DISP_BROAD_PLANMOD_WEEK)) {
				formatter = new SimpleDateFormat("yyyyMMddhhmmss");
				PrePlayInfo info = new PrePlayInfo();
				info.setTimestr(data[1]);
				info.setPmode(DISP_BROAD_PLANMOD_WEEK);
				info.setTmode(data[3]);
				info.setTimes(data[4]);
				info.setFile(data[5]);
				info.setList(data[6]);
				info.setPlayer(data[7]);
				info.setTkey(tempMeetNum);
				daoPrePlayInfo.addPrePlayInfo(info);
				try {
					long delayTime = formatter.parse(dateInd).getTime();
					timerTask.delayMeetPlayAtTimeWeekly(tempMeetNum, cmdBuff, delayTime);
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		}

		return SUCCESS;
	}

	@SuppressWarnings("static-access")
	/**
	 * TODO: here command define need to be optimized, now command style =
	 * page:1:800:801:802:803\r\n how about style = page:1:800:801,802,803\r\n
	 * the using style is not clear enough.
	 */
	private int procReqPageCall(String[] data) {
		String type = data[1];
		String caller = data[2];
		Date time = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
		String timeStr = formatter.format(time);
		// String time = (procLinuxCmd.execCmd("date \"+%H%M%S\"")).toString();
		String tempMeetNum = null;
		StringBuffer buff = new StringBuffer();
		if (type.equals(DISP_PAGE_CALLTYPE_MEET)) {
			tempMeetNum = FS_MEET_NORMAL_TYPE + timeStr;
			buff.append("bgapi originate {origination_caller_id_number=000}user/");
			buff.append(caller);
			buff.append(" &transfer(");
			buff.append(tempMeetNum);
			buff.append(")");
			buff.append(DISP_FSCMD_TAIL);
			fsSocket.fsSendCommand(buff.toString());
			logger.debug("make command : {}", buff.toString());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			while (true) {
				Map<String, SipActiveInfo> actMap = initTerStatus.activeUserMap;
				String status = actMap.get(caller).getCallStatus();
				if (status.equals(TER_STATUS_ANSWER)) {
					int len = data.length;
					StringBuffer calleesBuff = new StringBuffer();
					for (int i = 3; i < len; i++) {
						if (i < len) {
							calleesBuff.append(data[i]).append(":");
						} else {
							calleesBuff.append(data[i]);
						}
					}

					Map<String, String> map = initTerStatus.specialCallMap;
					map.put(tempMeetNum, type + ":" + caller);

					loopDisCallMeet(tempMeetNum, calleesBuff.toString());

					break;
				} else if (status.equals(TER_STATUS_REG) || status.equals(TER_STATUS_UNREG)) {
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else if (type.equals(DISP_PAGE_CALLTYPE_BROAD)) {
			tempMeetNum = FS_MEET_MUTE_TYPE + timeStr;
			buff.append("bgapi originate {origination_caller_id_number=000}user/");
			buff.append(caller);
			buff.append(" &transfer(");
			buff.append(tempMeetNum);
			buff.append(")");
			buff.append(DISP_FSCMD_TAIL);
			fsSocket.fsSendCommand(buff.toString());
			logger.debug("make command : {}", buff.toString());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			while (true) {
				Map<String, SipActiveInfo> actMap = initTerStatus.activeUserMap;
				String status = actMap.get(caller).getCallStatus();
				if (status.equals(TER_STATUS_ANSWER)) {
					StringBuffer cmdBuff = new StringBuffer();
					cmdBuff.append("bgapi conference ");
					cmdBuff.append(tempMeetNum);
					cmdBuff.append(" unmute ");
					cmdBuff.append(getMeetMemberId(tempMeetNum, caller));
					cmdBuff.append(DISP_FSCMD_TAIL);
					fsSocket.fsSendCommand(buff.toString());
					logger.debug("make command : {}", buff.toString());
					int len = data.length;
					StringBuffer calleesBuff = new StringBuffer();
					for (int i = 3; i < len; i++) {
						if (i < len) {
							calleesBuff.append(data[i]).append(":");
						} else {
							calleesBuff.append(data[i]);
						}
					}

					Map<String, String> map = initTerStatus.specialCallMap;
					map.put(tempMeetNum, type + ":" + caller);

					loopDisCallMeet(tempMeetNum, calleesBuff.toString());

					break;
				} else if (status.equals(TER_STATUS_REG) || status.equals(TER_STATUS_UNREG)) {
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		return SUCCESS;
	}

	@SuppressWarnings("static-access")
	private int procReqQueueProcCall(String[] data) {
		String type = data[0];
		/** queue don't use ? */
		String queue = data[1];
		String caller = data[2];
		String callee = data[3];

		Map<String, String> map = initTerStatus.specialCallMap;
		map.put(caller, type + ":" + callee);

		StringBuffer buff = new StringBuffer();
		buff.append("bgapi originate {origination_caller_id_number=000}user/");
		buff.append(caller);
		buff.append(" &park");
		buff.append(DISP_FSCMD_TAIL);
		fsSocket.fsSendCommand(buff.toString());
		logger.debug("make command : {}", buff.toString());

		return SUCCESS;
	}

	private int procReqMeetProcCall(String[] data) {
		String type = data[0];
		String meetNum = data[1];
		StringBuffer buff = new StringBuffer();
		buff.append("bgapi conference ");
		buff.append(meetNum);
		if (type.equals(DISP_MEET_KICK)) {
			buff.append(" kick ");
		} else if (type.equals(DISP_MEET_MUTE)) {
			buff.append(" mute ");
		} else if (type.equals(DISP_MEET_UNMUTE)) {
			buff.append(" unmute ");
		}
		if (type.equals(DISP_MEET_KICKALL)) {
			buff.append("all");
		} else {
			String number = data[2];
			String id = getMeetMemberId(meetNum, number);
			buff.append(id);
		}
		buff.append(DISP_FSCMD_TAIL);
		fsSocket.fsSendCommand(buff.toString());
		logger.debug("make command : {}", buff.toString());

		return SUCCESS;
	}

	private int procReqMeetCall(String[] data) {
		String number = data[1];
		String meetNum = data[2];
		StringBuffer buff = new StringBuffer();
		buff.append("bgapi conference ");
		buff.append(meetNum);
		buff.append(" dial {originate_timeout=15}user/");
		buff.append(number);
		buff.append(" ");
		buff.append(meetNum);
		buff.append(DISP_FSCMD_TAIL);
		fsSocket.fsSendCommand(buff.toString());
		logger.debug("make command : {}", buff.toString());

		return SUCCESS;
	}

	@SuppressWarnings("static-access")
	private int procReqRecordCall(String[] data) {
		String number = data[1];
		Map<String, SipActiveInfo> map = initTerStatus.activeUserMap;
		String uuid = map.get(number).getOwnUuid();
		Date time = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
		String timeStr = formatter.format(time);
		// String time = (procLinuxCmd.execCmd("date
		// \"+%Y%m%d-%H:%M:%S\"")).toString();
		String[] timePart = timeStr.split("-");
		StringBuffer buff = new StringBuffer();
		buff.append("bgapi uuid_record ");
		buff.append(uuid);
		buff.append(" start ");
		buff.append(ConfigManager.getInstance().getDisRecordPath());
		buff.append(timePart[0]);
		buff.append("/");
		buff.append(number);
		buff.append("_");
		buff.append(timePart[1]);
		buff.append(".wav");
		buff.append(DISP_FSCMD_TAIL);
		fsSocket.fsSendCommand(buff.toString());
		logger.debug("make command : {}", buff.toString());

		return SUCCESS;
	}

	private int procReqForbidCall(String[] data) {
		String ind = data[1];
		String number = data[2];
		if (ind.equals(FORBID_ON)) {
			daoUserSipBlack.addUserSip(number);
		} else if (ind.equals(FORBID_OFF)) {
			daoUserSipBlack.delUserSip(number);
		}
		return SUCCESS;
	}

	private int procReqNightCall(String[] data) {
		String type = data[0];
		String name = data[1];
		String transfer = null;
		if (type.equals(DISP_NIGHT_CALL)) {
			transfer = data[2];
		} else if (type.equals(DISP_UNIGHT_CALL)) {
			transfer = "";
		}
		daoUserSip.updateTransByNumber(name, transfer);

		procLinuxCmd.execCmd("/usr/bin/fs_manage -ft /usr/local/freeswitch/conf/fsmanage.conf");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		procLinuxCmd.execCmd("fs_cli -x 'reloadxml'");

		return SUCCESS;
	}

	private int procReqHangupMeetCall(String[] data) {
		String conferenceId = data[1];
		StringBuffer buff = new StringBuffer();
		buff.append("bgapi conference ");
		buff.append(conferenceId);
		buff.append(" kick all");
		buff.append(DISP_FSCMD_TAIL);
		fsSocket.fsSendCommand(buff.toString());
		logger.debug("make command : {}", buff.toString());

		return SUCCESS;
	}

	@SuppressWarnings("static-access")
	private int procReqHangupCall(String[] data) {
		String caller = data[1];
		StringBuffer buff = new StringBuffer();
		Map<String, SipActiveInfo> map = initTerStatus.activeUserMap;
		String uuid = map.get(caller).getOwnUuid();
		buff.append("bgapi uuid_kill ");
		buff.append(uuid);
		buff.append(DISP_FSCMD_TAIL);
		fsSocket.fsSendCommand(buff.toString());
		logger.debug("make command : {}", buff.toString());

		return SUCCESS;
	}

	@SuppressWarnings("static-access")
	private int procReqTransCall(String[] data) {
		String type = data[0];
		String caller = data[1];
		String callee = data[2];

		Map<String, String> map = initTerStatus.specialCallMap;
		map.put(callee, type + ":" + caller);

		StringBuffer buff = new StringBuffer();
		buff.append("bgapi originate {origination_caller_id_number=000}user/");
		buff.append(callee);
		buff.append(" &park");
		buff.append(DISP_FSCMD_TAIL);
		fsSocket.fsSendCommand(buff.toString());
		logger.debug("make command : {}", buff.toString());

		return SUCCESS;
	}

	private int procReqPickupCall(String[] data) {
		String caller = data[1];
		StringBuffer buff = new StringBuffer();
		buff.append("bgapi originate {origination_caller_id_number=000}user/");
		buff.append(caller);
		buff.append(" &transfer(");
		buff.append(FUNCODE_PICKUP);
		buff.append(")");
		buff.append(DISP_FSCMD_TAIL);
		fsSocket.fsSendCommand(buff.toString());
		logger.debug("make command : {}", buff.toString());

		return SUCCESS;
	}

	@SuppressWarnings("static-access")
	private int procReqBridgeCall(String[] data) {
		String type = data[0];
		String caller = data[1];
		String callee = data[2];

		Map<String, String> map = initTerStatus.specialCallMap;
		map.put(caller, type + ":" + callee);

		StringBuffer buff = new StringBuffer();
		buff.append("bgapi originate {origination_caller_id_number=000}user/");
		buff.append(caller);
		buff.append(" &park");
		buff.append(DISP_FSCMD_TAIL);
		fsSocket.fsSendCommand(buff.toString());
		logger.debug("make command : {}", buff.toString());

		return SUCCESS;
	}

	@SuppressWarnings("static-access")
	private int procReqInsertCall(String[] data) {
		// w1:current one. w2:opposite one. w3:three way.
		String caller = data[1];
		String callee = data[2];
		StringBuffer buff = new StringBuffer();
		Map<String, SipActiveInfo> map = initTerStatus.activeUserMap;
		String uuid = map.get(callee).getOwnUuid();
		buff.append("bgapi originate {origination_caller_id_number=000}user/");
		buff.append(caller);
		buff.append(" \'queue_dtmf:w2@500,eavesdrop:");
		buff.append(uuid);
		buff.append("\' inline");
		buff.append(DISP_FSCMD_TAIL);
		fsSocket.fsSendCommand(buff.toString());
		logger.debug("make command : {}", buff.toString());

		return SUCCESS;
	}

	@SuppressWarnings("static-access")
	private int procReqMonitorCall(String[] data) {
		String caller = data[1];
		String callee = data[2];
		StringBuffer buff = new StringBuffer();
		Map<String, SipActiveInfo> map = initTerStatus.activeUserMap;
		String uuid = map.get(callee).getOwnUuid();
		buff.append("bgapi originate {origination_caller_id_number=000}user/");
		buff.append(caller);
		buff.append(" &eavesdrop(");
		buff.append(uuid);
		buff.append(")");
		buff.append(DISP_FSCMD_TAIL);
		fsSocket.fsSendCommand(buff.toString());
		logger.debug("make command : {}", buff.toString());

		return SUCCESS;
	}

	private int procReqThirdPartyCall(String[] data) {
		String caller = data[1];
		String callee = data[2];
		StringBuffer buff = new StringBuffer();
		buff.append("bgapi originate {origination_caller_id_number=000}user/");
		buff.append(caller);
		buff.append(" &bridge(user/");
		buff.append(callee);
		buff.append(")");
		buff.append(DISP_FSCMD_TAIL);
		fsSocket.fsSendCommand(buff.toString());
		logger.debug("make command : {}", buff.toString());

		return SUCCESS;
	}

	private int procReqLogin(String[] data) {
		String name = data[1];
		String passwd = data[2];
		UserSip user = daoUserSip.findByNumber(name);
		if(user == null){
			return FAILURE;	
		}
		logger.debug("get user with name = {},passwd = {}", user.getName(), user.getPassword());
		if (passwd.equals(user.getPassword())) {
			return SUCCESS;
		}
		return FAILURE;
	}

	private StringBuffer procReqDisUsersList(String[] data) {
		StringBuffer buff = new StringBuffer();
		/** temporary no use ,need dispatcher and user relation table */
		// String name = data[1];
		List<UserSip> list = new ArrayList<UserSip>();
		list = daoUserSip.findAll();
		for (int i = 0; i < list.size(); i++) {
			buff.append("userlist:");
			buff.append(list.get(i).getNumber() + ":");
			buff.append(list.get(i).getName() + ":");
			try {
				buff.append(new String(list.get(i).getDepartment().getBytes("iso-8859-1"), "gb2312") + ":");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			buff.append(list.get(i).getType());
			buff.append("\r\n");
		}
		return buff;
	}

	private StringBuffer procReqSipUserStatus() {
		StringBuffer buff = new StringBuffer();
		@SuppressWarnings("static-access")
		Map<String, SipActiveInfo> map = initTerStatus.activeUserMap;
		Iterator<Map.Entry<String, SipActiveInfo>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, SipActiveInfo> entry = iter.next();
			buff.append("peer:");
			buff.append(entry.getKey() + ":");
//			logger.debug("get key from activeUserMap : {}",entry.getKey());
			buff.append(entry.getValue().getStatus());
			buff.append("\r\n");
		}
		return buff;
	}

	private StringBuffer procReqNightUserList() {
		StringBuffer buff = new StringBuffer();
		List<UserSip> list = new ArrayList<UserSip>();
		list = daoUserSip.findAllTransUsers();
		for (int i = 0; i < list.size(); i++) {
			buff.append(DISP_SHOW_NLIST);
			buff.append(":");
			buff.append(list.get(i).getNumber() + ":");
			buff.append(list.get(i).getAlltrans());
			buff.append("\r\n");
		}
		return buff;
	}

	private StringBuffer procReqForbidUserList() {
		StringBuffer buff = new StringBuffer();
		List<UserSipBlack> list = new ArrayList<UserSipBlack>();
		list = daoUserSipBlack.findAll();
		for (int i = 0; i < list.size(); i++) {
			buff.append(DISP_SHOW_FLIST_PRE);
			buff.append(":");
			buff.append(list.get(i).getNum());
			buff.append("\r\n");
		}
		return buff;
	}

	private StringBuffer procReqCamerUserList() {
		StringBuffer buff = new StringBuffer();
		List<UserSipCamer> list = new ArrayList<UserSipCamer>();
		list = daoUserSipCamer.findAll();
		for (int i = 0; i < list.size(); i++) {
			buff.append(DISP_SHOW_CAMERS_PRE);
			buff.append(":");
			buff.append(list.get(i).getName() + ":");
			buff.append(list.get(i).getIp() + ":");
			buff.append(list.get(i).getPort() + ":");
			buff.append(list.get(i).getType());
			buff.append(":");
			buff.append(list.get(i).getUsername() + ":");
			buff.append(list.get(i).getPassword());
			buff.append("\r\n");
		}
		return buff;
	}

	private StringBuffer procReqPrePlayList() {
		StringBuffer buff = new StringBuffer();
		List<PrePlayInfo> list = new ArrayList<PrePlayInfo>();
		list = daoPrePlayInfo.findAll();
		for (int i = 0; i < list.size(); i++) {
			buff.append(DISP_SHOW_BROAD_PLAY_PRE);
			buff.append(":");
			buff.append(list.get(i).getId() + ":");
			buff.append(list.get(i).getTimestr() + ":");
			buff.append(list.get(i).getPmode() + ":");
			buff.append(list.get(i).getTmode() + ":");
			buff.append(list.get(i).getTimes() + ":");
			buff.append(list.get(i).getFile() + ":");
			buff.append(list.get(i).getList() + ":");
			buff.append(list.get(i).getPlayer());
			buff.append("\r\n");
		}
		return buff;
	}

	private String getMeetMemberId(String meetNum, String number) {
		String id = "0";
		StringBuffer cmdBuff = new StringBuffer();
		cmdBuff.append("fs_cli -H 192.168.8.12 -x 'conference ").append(meetNum).append(" list '");
		StringBuffer resBuff = procLinuxCmd.execCmd(cmdBuff.toString());
		int start = 0;
		int pos = 0;
		while ((pos = resBuff.indexOf("\n")) != -1) {
			String line = resBuff.substring(start, pos);
			logger.debug("conference member line = {}", line);
			String[] part = line.split(";");
			if (part[4].equals(number)) {
				id = part[0];
				break;
			}
			start = pos + 1;
		}
		return id;
	}

	private void loopDisCallMeet(String meetNum, String callee) {
		String[] callees = callee.split(":");
		int len = callees.length;
		for (int i = 0; i < len; i++) {
			StringBuffer tempBuff = new StringBuffer();
			if (callees[i] != null && !callees[i].equals("")) {
				tempBuff.append("bgapi originate {origination_caller_id_number=000}user/");
				tempBuff.append(callees[i]);
				tempBuff.append(" &transfer(");
				tempBuff.append(meetNum);
				tempBuff.append(")");
				tempBuff.append(DISP_FSCMD_TAIL);
				fsSocket.fsSendCommand(tempBuff.toString());
				logger.debug("make command : {}", tempBuff.toString());
			}
		}
	}

	private String getBroadPlayMusicName(String musics) {
		StringBuffer buff = new StringBuffer();
		String[] music = musics.split(",");
		int musicLen = music.length;
		int j = 0;
		for (j = 0; j < musicLen; j++) {
			if (j < musicLen) {
				buff.append(ConfigManager.getInstance().getDisMusicPath());
				buff.append(music[j]).append("!");
			} else {
				buff.append(ConfigManager.getInstance().getDisMusicPath()).append(music[j]);
			}
		}
		return buff.toString();
	}

	private int getBroadPlayMusicLen(String musics) {
		int filePlaySecondEstimate = 0;
		String[] music = musics.split(",");
		int musicLen = music.length;

		int j = 0;
		for (j = 0; j < musicLen; j++) {
			String fileName = ConfigManager.getInstance().getDisMusicPath() + music[j];
			File file = new File(fileName);
			logger.debug("get music file length {}", file.length());
			filePlaySecondEstimate += file.length() * 8 / 160;
		}

		return filePlaySecondEstimate;
	}

	private StringBuffer makeLastResponse(StringBuffer buff, int result) {
		StringBuffer nBuff = new StringBuffer();
		nBuff.append(buff);
		if (result == SUCCESS) {
			nBuff.append(DISP_TAIL_SUCC);
		} else if (result == FAILURE) {
			nBuff.append(DISP_TAIL_FAIL);
		}
		return nBuff;
	}
	/**
	 * 
	 * 字母 日期或时间元素 表示 示例 G Era 标志符 Text AD y 年 Year 1996; 96 M 年中的月份 Month July;
	 * Jul; 07 w 年中的周数 Number 27 W 月份中的周数 Number 2 D 年中的天数 Number 189 d 月份中的天数
	 * Number 10 F 月份中的星期 Number 2 E 星期中的天数 Text Tuesday; Tue a Am/pm 标记 Text PM
	 * H 一天中的小时数（0-23） Number 0 k 一天中的小时数（1-24） Number 24 K am/pm 中的小时数（0-11）
	 * Number 0 h am/pm 中的小时数（1-12） Number 12 m 小时中的分钟数 Number 30 s 分钟中的秒数
	 * Number 55 S 毫秒数 Number 978 z 时区 General time zone Pacific Standard Time;
	 * PST; GMT-08:00 Z 时区 RFC 822 time zone -0800
	 * 
	 */

}
