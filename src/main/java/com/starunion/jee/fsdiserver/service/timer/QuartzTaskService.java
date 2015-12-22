package com.starunion.jee.fsdiserver.service.timer;

import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
//import static org.quartz.JobBuilder.*;
//import static org.quartz.SimpleScheduleBuilder.*;
//import static org.quartz.CronScheduleBuilder.*;
//import static org.quartz.CalendarIntervalScheduleBuilder.*;
//import static org.quartz.JobKey.*;
//import static org.quartz.TriggerKey.*;
//import static org.quartz.DateBuilder.*;
//import static org.quartz.impl.matchers.KeyMatcher.*;
//import static org.quartz.impl.matchers.GroupMatcher.*;
//import static org.quartz.impl.matchers.AndMatcher.*;
//import static org.quartz.impl.matchers.OrMatcher.*;
//import static org.quartz.impl.matchers.EverythingMatcher.*;
import org.springframework.stereotype.Service;

import com.starunion.jee.fsdiserver.thread.FsTcpSocket;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobKey.jobKey;

/**
 * @author LingSong
 * @date 2015-09-21
 * @describe self quartz factory and scheduler initial.
 * 
 */

@Service
public class QuartzTaskService {

	public QuartzTaskService() {

	}

	public void stopMeetPlay(FsTcpSocket fs, String meetNum, int delay) {

		@SuppressWarnings("static-access")
		SchedulerFactory sf = QuartzScheduler.getInstance().getSchedulerFactory();
		try {
			Scheduler sched = sf.getScheduler();
			Date date = new Date();
			long nDate = date.getTime() + delay;
			date.setTime(nDate);

			JobDataMap map = new JobDataMap();
			map.put("fsSock", fs);
			map.put("meetNum", meetNum);
			JobDetail job = newJob(JobStopMeetPlayMusic.class).withIdentity("job1", "group1").setJobData(map).build();
			Trigger trigger = newTrigger().withIdentity("trigger1", "group1").startAt(date)
					.withSchedule(simpleSchedule().withIntervalInSeconds(30).withRepeatCount(0)).build();
			sched.scheduleJob(job, trigger);
			sched.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	}

	public void delayMeetPlayAtTime(StringBuffer cmd, long delayTime) {

		@SuppressWarnings("static-access")
		SchedulerFactory sf = QuartzScheduler.getInstance().getSchedulerFactory();
		try {
			Scheduler sched = sf.getScheduler();
			Date date = new Date();
			date.setTime(delayTime);

			JobDataMap dataMap = new JobDataMap();
			dataMap.put("cmd", cmd);
			JobDetail job = newJob(JobInvokeMeetPlayMusic.class).withIdentity("job2", "group1").setJobData(dataMap)
					.build();
			Trigger trigger = newTrigger().withIdentity("trigger2", "group1").startAt(date)
					.withSchedule(simpleSchedule().withIntervalInSeconds(30).withRepeatCount(0)).build();
			sched.scheduleJob(job, trigger);
			sched.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	}

	public void delayMeetPlayAtCron(String jobName,StringBuffer cmd, String cronExpr) {

		@SuppressWarnings("static-access")
		SchedulerFactory sf = QuartzScheduler.getInstance().getSchedulerFactory();
		try {
			Scheduler sched = sf.getScheduler();
			JobDataMap dataMap = new JobDataMap();
			dataMap.put("cmd", cmd);
			JobDetail job = newJob(JobInvokeMeetPlayMusic.class).withIdentity(jobName, "group1").setJobData(dataMap)
					.build();
			Trigger trigger = newTrigger().withIdentity("trigger3", "group1").startNow()
					.withSchedule(cronSchedule(cronExpr)).build();
			sched.scheduleJob(job, trigger);
			sched.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	}

	public void delayMeetPlayAtTimeWeekly(String jobName,StringBuffer cmd, long delayTime) {

		@SuppressWarnings("static-access")
		SchedulerFactory sf = QuartzScheduler.getInstance().getSchedulerFactory();
		try {
			Scheduler sched = sf.getScheduler();
			Date date = new Date();
			date.setTime(delayTime);

			JobDataMap dataMap = new JobDataMap();
			dataMap.put("cmd", cmd);
			JobDetail job = newJob(JobInvokeMeetPlayMusic.class).withIdentity(jobName, "group1").setJobData(dataMap)
					.build();
			Trigger trigger = newTrigger().withIdentity("trigger2", "group1").startAt(date)
					.withSchedule(simpleSchedule().withIntervalInHours(7 * 24).repeatForever()).build();
			sched.scheduleJob(job, trigger);
			sched.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	}
	
	public void deleteCycleTask(String jobInd, String jobGroup) {

		@SuppressWarnings("static-access")
		SchedulerFactory sf = QuartzScheduler.getInstance().getSchedulerFactory();
		try {
			Scheduler sched = sf.getScheduler();
			sched.deleteJob(jobKey(jobInd, jobGroup));
			/** 
			 * sched.unscheduleJob(triggerKey("trigger3", "group1"));
			 * sched.shutdown();
			 */
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	} 
}
