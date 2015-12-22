package com.starunion.jee.fsdiserver.service.timer;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.starunion.jee.fsdiserver.service.ProcClientRequest;


@Service
public class JobInvokeMeetPlayMusic implements Job {

	@Autowired
	private ProcClientRequest proc;
	public JobInvokeMeetPlayMusic(){
		
	}
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobMap = context.getJobDetail().getJobDataMap();
		StringBuffer cmd = (StringBuffer) jobMap.get("cmd");
		proc.procClientRequest(cmd);
		
	}

}
