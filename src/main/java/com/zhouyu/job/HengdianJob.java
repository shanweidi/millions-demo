package com.zhouyu.job;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 *
 * @author shanweidi
 * @date 2026-03-13 14:36
 **/
public abstract class HengdianJob implements SchedulingConfigurer {

    protected static final String SUCCESS = "SUC0000";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    protected abstract void scheduleCronTask();

    protected abstract String springDynamicCron();

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(3, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("Hg-job-pool");
            return t;
        });
        taskRegistrar.setScheduler(executorService);
        taskRegistrar.addTriggerTask(new Runnable() {
            @Override
            public void run() {
                scheduleCronTask();
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                String cron = springDynamicCron();
                CronTrigger trigger = new CronTrigger(cron);

                Date nextExecDate = trigger.nextExecutionTime(triggerContext);
                return nextExecDate;
            }
        });

    }
}
