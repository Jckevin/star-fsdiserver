package com.starunion.jee.fsdiserver.thread;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.starunion.jee.fsdiserver.po.ClientBindInfo;
import com.starunion.jee.fsdiserver.service.ConfigManager;
import com.starunion.jee.fsdiserver.service.ProcClientRequest;

/*
 * @Author LingSong 
 * @Date   2015-08-18
 * 
 */
@Service
public class ClientTcpSocket implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ClientTcpSocket.class);
	
	public static Map<String, ClientBindInfo> clientInfoMap = new ConcurrentHashMap<String, ClientBindInfo>();
	private String ipAddr;
	private int ipPort;
	private ServerSocket server;
	@Autowired
	ProcClientRequest clientProc;
	
	public ClientTcpSocket() {

	}

	@Override
	public void run() {
		try {
			ipAddr = ConfigManager.getInstance().getDisAddr();
			ipPort = Integer.parseInt(ConfigManager.getInstance().getDisPort());
			server = new ServerSocket(this.ipPort);
			while (true) {
				try {
					Socket clientSocket = server.accept();
					
					String clientIp = clientSocket.getInetAddress().getHostAddress();
					String clientPort = String.valueOf(clientSocket.getPort());
					logger.info("get client bind from {}:{}", clientIp, clientPort);
					
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(),"gb2312"));
					
					/** put client bind info to a static Map */
					ClientBindInfo cInfo = new ClientBindInfo();
					cInfo.setClientIp(clientIp);
					cInfo.setClientPort(clientPort);
					cInfo.setWriter(out);
					clientInfoMap.put(clientPort, cInfo);
					
					/** 
					 * use java self thread pool,but it seems no need here.
					 * ExecutorService executorService = Executors.newCachedThreadPool();
					 * executorService.execute(new ClientTcpThread(clientSocket));
					 * executorService.shutdown();
					 */
					
					/** 
					 * new thread with runnable maybe better.
					 * Thread clientThread = new TcpClientThread(clientSocket);
					 * clientThread.start();
					 */
					
					Thread clientThread = new Thread(new ClientTcpThread(clientSocket,clientProc,out,clientPort));
					/** 
					 * why this method can't inject clientProc successful?
					 * Thread clientThread = new Thread(new ClientTcpThread(clientSocket,out,clientPort));
					 * 
					 */
					clientThread.start();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (BindException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public int getIpPort() {
		return ipPort;
	}

	public void setIpPort(int ipPort) {
		this.ipPort = ipPort;
	}

}
