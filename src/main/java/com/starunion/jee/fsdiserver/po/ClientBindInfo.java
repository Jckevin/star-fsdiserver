package com.starunion.jee.fsdiserver.po;

import java.io.BufferedWriter;

import org.springframework.stereotype.Repository;

@Repository
public class ClientBindInfo {
	private String clientIp;
	private String clientPort;
	private BufferedWriter writer;
	
	public ClientBindInfo(){
		
	}
	
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getClientPort() {
		return clientPort;
	}

	public void setClientPort(String clientPort) {
		this.clientPort = clientPort;
	}

	public BufferedWriter getWriter() {
		return writer;
	}

	public void setWriter(BufferedWriter writer) {
		this.writer = writer;
	}


}
