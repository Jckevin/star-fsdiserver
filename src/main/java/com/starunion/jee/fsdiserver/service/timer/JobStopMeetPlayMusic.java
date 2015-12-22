package com.starunion.jee.fsdiserver.service.timer;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.starunion.jee.fsdiserver.thread.FsTcpSocket;

public class JobStopMeetPlayMusic implements Job {
	private static final Logger logger = LoggerFactory.getLogger(JobStopMeetPlayMusic.class);
	public JobStopMeetPlayMusic(){
		
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobMap = context.getJobDetail().getJobDataMap();
		FsTcpSocket fsSocket = (FsTcpSocket) jobMap.get("fsSock");
		String meetNumber = jobMap.getString("meetNum");
		fsSocket.fsSendCommand("bgapi conference "+meetNumber+" stop\n\n");
		
	}

}
