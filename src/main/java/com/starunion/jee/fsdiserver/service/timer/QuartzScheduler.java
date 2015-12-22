package com.starunion.jee.fsdiserver.service.timer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzScheduler {
	private static QuartzScheduler quartzScheduler;
	private static SchedulerFactory schedulerFactory;

	private QuartzScheduler() {
	}

	public static synchronized QuartzScheduler getInstance() {
		if (quartzScheduler == null) {
			quartzScheduler = new QuartzScheduler();
			return quartzScheduler;
		} else
			return quartzScheduler;
	}
	
	public static SchedulerFactory getSchedulerFactory() {

		if (schedulerFactory == null) {
			schedulerFactory = new StdSchedulerFactory();
		}
		return schedulerFactory;
	}

	public static void setSchedulerFactory(SchedulerFactory schedulerFactory) {
		QuartzScheduler.schedulerFactory = schedulerFactory;
	}

	//} SchedulerFactory sf = new StdSchedulerFactory();      
	//Scheduler sched = sf.getScheduler();  
	//
	//Date runTime = evenMinuteDate(new Date());  
	//
	// 通过过JobDetail封装HelloJob，同时指定Job在Scheduler中所属组及名称，这里，组名为group1，而名称为job1。  
	//JobDetail job = newJob(HelloJob.class).withIdentity("job1", "group1").build();  
	//
	// 创建一个SimpleTrigger实例，指定该Trigger在Scheduler中所属组及名称。  
	// 接着设置调度的时间规则.当前时间运行  
	//Trigger trigger = newTrigger().withIdentity("trigger1", "group1").startAt(runTime).build();  
	//
	// 注册并进行调度  
	//sched.scheduleJob(job, trigger);  
	//
	// 启动调度器  
	//sched.start();  
	//
	//try {  
	//  //当前线程等待65秒  
	//  Thread.sleep(65L * 1000L);  
	//} catch (Exception e) {  
	//    
	//}  
	//
	//调度器停止运行  
	//sched.shutdown(true);  
	//
	//log.error("结束运行。。。。");  
	//
	//}  
}
