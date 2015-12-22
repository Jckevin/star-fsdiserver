package com.starunion.jee.fsdiserver.service.timer;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
public class SpringTimerTaskTest {

	public SpringTimerTaskTest() {

	}

//	@Scheduled(cron = "0/5 * *  * * ? ") // 每5秒执行一次
	public void taks1() {
		System.out.println("task1 ............. 5 s 进入测试");
	}
	
//	@Scheduled(cron = "0/10 * *  * * ? ") // 每5秒执行一次
	public void taks2() {
		System.out.println("task2 ............. 10 s 进入测试");
	}

}
