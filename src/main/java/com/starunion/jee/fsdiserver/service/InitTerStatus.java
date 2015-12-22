package com.starunion.jee.fsdiserver.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.starunion.jee.fsdiserver.po.SipActiveInfo;


@Service
public class InitTerStatus {
	private static final Logger logger = LoggerFactory
			.getLogger(InitTerStatus.class);

	// public static List<SipActiveInfo> activeUserList = new
	// ArrayList<SipActiveInfo>();
	public static Map<String, SipActiveInfo> activeUserMap = new HashMap<String, SipActiveInfo>();
	public static Map<String, String> specialCallMap = new HashMap<String, String>();
	@Autowired
	private ProcLinuxCommand proc;

	public InitTerStatus() {

	}

	public void initSipUserInfo(String addr) {
		StringBuffer buff = new StringBuffer();
		StringBuffer line = new StringBuffer();
		StringBuffer cmd = new StringBuffer();
		cmd.append("fs_cli -H ");
		cmd.append(addr);
		cmd.append(" -x 'list_users'");
		buff = proc.execCmd(cmd.toString());

		for (int i = 0; i < buff.length(); i++) {
			char c;
			if ((c = buff.charAt(i)) != '\n') {
				line.append(c);
			} else {
				String[] parts;
				/** 10 used to skip \r\n & +OK */
				if (line.length() > 10) {
					parts = line.toString().split("\\|");
					if (!parts[0].equals("userid")) {
						SipActiveInfo info = new SipActiveInfo();
						info.setSipNumber(parts[0]);
						if (parts[4].startsWith("error")) {
							info.setStatus("0");
						} else {
							info.setStatus("1");
						}
						// activeUserList.add(info);
						activeUserMap.put(parts[0], info);
					}
					line.delete(0, line.length());
				}
			}
		}
	}

}
