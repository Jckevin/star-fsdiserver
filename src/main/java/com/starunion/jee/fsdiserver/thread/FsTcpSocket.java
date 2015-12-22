package com.starunion.jee.fsdiserver.thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.BindException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.starunion.jee.fsdiserver.service.ConfigManager;
import com.starunion.jee.fsdiserver.service.ProcFsResponse;

/*
 * @Author LingSong
 * @date   2015-08-19 
 * @modify 2015-09-11
 */

@Service
public class FsTcpSocket implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(FsTcpSocket.class);

	private String ipAddr;
	private int ipPort;
	private Socket fsClient;
	private BufferedWriter out;
	@Autowired
	private ProcFsResponse msgProc;

	public FsTcpSocket() {

	}

	@Override
	public void run() {
		try {
			ipAddr = ConfigManager.getInstance().getFsAddr();
			ipPort = Integer.parseInt(ConfigManager.getInstance().getFsPort());
			fsClient = new Socket(ipAddr, ipPort);

			BufferedReader in = new BufferedReader(new InputStreamReader(fsClient.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(fsClient.getOutputStream()));
			StringBuffer respBuffer = new StringBuffer();
			String line = null;
			try {
				/** this logic is beautiful for me, hold on! keep on!*/
				while ((line = in.readLine()) != null) {
					respBuffer.append(line);
					respBuffer.append("\n");
					if (line.equals("")) {
						/**
						 * it seems this condition is enough for message end.
						 */
						msgProc.procFsResponse(respBuffer);
						/**
						 * this step is important ,or memory leak and useless
						 * message .
						 */
						respBuffer.delete(0, respBuffer.length());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (BindException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void fsSendCommand(String command) {
		try {
			out.write(command);
			out.flush();
			logger.info("command sendto FreeSWITCH : {}", command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
