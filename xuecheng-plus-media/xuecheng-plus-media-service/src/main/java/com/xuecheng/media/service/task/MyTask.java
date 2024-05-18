package com.xuecheng.media.service.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
public class MyTask {
    @Scheduled(cron = "0/10 * * * * ?")
    public void task1(){
        System.out.println("spring task....");
    }
}
