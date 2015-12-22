package com.starunion.jee.fsdiserver.thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.starunion.jee.fsdiserver.service.ProcClientRequest;


/*
 * @Author LingSong 
 * @Date   2015-08-18
 * @describe this io need change to nio,2015-09-11,by LingSong.
 * 
 */
@Service
public class ClientTcpThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ClientTcpThread.class);

	private Socket clientSocket;
	private ProcClientRequest reqProc;
	private BufferedWriter out;
	private String clientPort;
	@Autowired
	ClientTcpSocket socket;

	/**
	 * TODO:need spend time here?
	 * 
	 * @Service this class throw errors, may because don't have empty class
	 *          constructor or don't put proper parameters.
	 * @Autowired ProcClientRequest reqProc; why reqProc.procClientRequest()
	 *            throw null? it seems not initial proper? as guessed,
	 */

	public ClientTcpThread() {

	}

	public ClientTcpThread(Socket s, BufferedWriter out, String port) {
		this.clientSocket = s;
		this.out = out;
		this.clientPort = port;
	}

	public ClientTcpThread(Socket s, ProcClientRequest proc, BufferedWriter out, String port) {
		this.clientSocket = s;
		this.reqProc = proc;
		this.out = out;
		this.clientPort = port;
	}

	@SuppressWarnings("static-access")
	public void run() {

		String line = null;
		StringBuffer recBuff = new StringBuffer();
		StringBuffer sendBuff = new StringBuffer();

		try {
			/**
			 * BufferedReader is buffered character flow, InputStream is byte
			 * flow.
			 */
			BufferedReader is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			while ((line = is.readLine()) != null) {
				logger.info("receive client request message : {}", line);
				recBuff.append(line);
				int reqLen = recBuff.toString().length();

				if (reqLen > 0) {
					sendBuff = reqProc.procClientRequest(recBuff);
					out.write(sendBuff.toString());
					out.flush();
					logger.info("send client response message : {}", sendBuff.toString());
					/** after message process,clear the recbuff & sendBuff */
					recBuff.delete(0, reqLen);
					sendBuff.delete(0, sendBuff.length());
				} else {
					logger.debug("get multiple end indicate (like \\r\\n etc.)characters,drop them.");
				}
			}

			socket.clientInfoMap.remove(clientPort);
			/** close the clientSocket. */
			clientSocket.close();
			logger.debug("normal closed, alive client count = {}", socket.clientInfoMap.size());
			
		} catch (IOException e) {
			if(e.getMessage().equals("Connection reset")){
				socket.clientInfoMap.remove(clientPort);
				logger.info("special RST closed, alive client count = {}", socket.clientInfoMap.size());
				try {
					clientSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}	
			}else{
				e.printStackTrace();
			}
		}

	}

	public Socket getSocket() {
		return clientSocket;
	}

	public void setSocket(Socket socket) {
		this.clientSocket = socket;
	}

}
