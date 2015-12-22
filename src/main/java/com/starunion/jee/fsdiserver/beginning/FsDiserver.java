package com.starunion.jee.fsdiserver.beginning;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.starunion.jee.fsdiserver.service.ConfigManager;
import com.starunion.jee.fsdiserver.service.InitTerStatus;
import com.starunion.jee.fsdiserver.thread.ClientTcpSocket;
import com.starunion.jee.fsdiserver.thread.FsTcpSocket;

/**
 * This project used as an important middler server who connects both the 
 * clients and the FreeSWITCH server.
 * The clients can be choose normally and traditional technology like Java(AWT),
 * MFC(VC++) etc.But more excitedly, i wanted choose the WEB systems, for its 
 * distribution and convenient.
 * However, web technology is still not mature enough at video (de)encode at 
 * the moment,but i am sure web ones have a brightly and great future.
 * 
 * @date 2015-12-18 
 * @version V0.0.1
 * @author LingSong
 * 
 */

public class FsDiserver {
	private static final Logger logger = LoggerFactory.getLogger(FsDiserver.class);

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
		File conFile = new File("conf/log4j2.xml");
		logContext.setConfigLocation(conFile.toURI());
		logContext.reconfigure();

		AbstractApplicationContext applicationContext = new FileSystemXmlApplicationContext(
				"conf/appContext.xml");

		/** print version information. */
		logger.debug("FsDiserver Version :V0.0.1@20150915");
		
		/** initial Local configuration. */
		ConfigManager configManager = applicationContext.getBean(
				"configManager", ConfigManager.class);
		configManager.getInstance().setConfigurationPath("conf/appParams.conf");

		/** initial server static data structure. */
		InitTerStatus initTerStatus = applicationContext.getBean(
				"initTerStatus", InitTerStatus.class);
		initTerStatus.initSipUserInfo(ConfigManager.getInstance().getFsAddr());

		/** start TCP socket(server) for clients. */
		Thread clientThread = new Thread(applicationContext.getBean(
				"clientTcpSocket", ClientTcpSocket.class));
		clientThread.start();

		/** start TCP socket(client) for FreeSWITCH. */
		Thread fsThread = new Thread(applicationContext.getBean("fsTcpSocket",
				FsTcpSocket.class));
		fsThread.start();
	}
}
