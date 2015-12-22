package com.starunion.jee.fsdiserver.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author LingSong
 * @date 2015-08-13
 * 
 */

@Service
public class ConfigManager {
	private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

	private String disAddr;
	private String disPort;
	private String fsAddr;
	private String fsPort;
	private String disRecordPath;
	private String disMusicPath;

	private static ConfigManager configurationManager;
	private static String configurationPath;
	private static Properties properties;

	private ConfigManager() {
	}

	public static synchronized ConfigManager getInstance() {
		if (configurationManager == null) {
			configurationManager = new ConfigManager();
			return configurationManager;
		} else
			return configurationManager;
	}
	
	private static Properties getProperties() {

		if (properties == null) {
			properties = new Properties();
			try {
				properties.load(new FileInputStream(configurationPath));
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage(), e);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return properties;
	}

	public void setConfigurationPath(String path) {
		configurationPath = path;
		String s = "------";
		logger.debug("{} < fsdiserver configuration > {}",s,s);

		disAddr = getProperties().getProperty("disAddr").trim();
		logger.debug("{} disAddr {} {}",s,s,disAddr);
		disPort = getProperties().getProperty("disPort").trim();
		logger.debug("{} disPort {} {}",s,s,disPort);
		fsAddr = getProperties().getProperty("fsAddr").trim();
		logger.debug("{} fsAddr {} {}",s,s,fsAddr);
		fsPort = getProperties().getProperty("fsPort").trim();
		logger.debug("{} fsPort {} {}",s,s,fsPort);
		disRecordPath = getProperties().getProperty("disRecordPath").trim();
		logger.debug("{} disRecordPath {} {}",s,s,disRecordPath);
		disMusicPath = getProperties().getProperty("disMusicPath").trim();
		logger.debug("{} disMusicPath {} {}",s,s,disMusicPath);

	}

	public String getDisAddr() {
		return disAddr;
	}

	public void setDisAddr(String disAddr) {
		this.disAddr = disAddr;
	}

	public String getDisPort() {
		return disPort;
	}

	public void setDisPort(String disPort) {
		this.disPort = disPort;
	}

	public String getFsAddr() {
		return fsAddr;
	}

	public void setFsAddr(String fsAddr) {
		this.fsAddr = fsAddr;
	}

	public String getFsPort() {
		return fsPort;
	}

	public void setFsPort(String fsPort) {
		this.fsPort = fsPort;
	}

	public String getDisRecordPath() {
		return disRecordPath;
	}

	public void setDisRecordPath(String disRecordPath) {
		this.disRecordPath = disRecordPath;
	}

	public String getDisMusicPath() {
		return disMusicPath;
	}

	public void setDisMusicPath(String disMusicPath) {
		this.disMusicPath = disMusicPath;
	}

}
