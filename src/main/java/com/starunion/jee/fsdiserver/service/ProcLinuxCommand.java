package com.starunion.jee.fsdiserver.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

@Service
public class ProcLinuxCommand {

	public ProcLinuxCommand() {

	}
	
	public StringBuffer execCmd(String cmd) {
		try {
			String[] command = { "/bin/sh", "-c", cmd };
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuffer buff = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null) {
				buff.append(line).append("\n");
			}
			return buff;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}