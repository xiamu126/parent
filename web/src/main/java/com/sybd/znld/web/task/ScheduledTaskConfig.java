package com.sybd.znld.web.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableScheduling
@EnableAsync
public class ScheduledTaskConfig {

    private final TaskSchedulingProperties taskSchedulingProperties;

    @Autowired
    public ScheduledTaskConfig(TaskSchedulingProperties taskSchedulingProperties) {
        this.taskSchedulingProperties = taskSchedulingProperties;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(taskSchedulingProperties.getPool().getSize(), new ThreadPoolExecutor.CallerRunsPolicy());
        //new ConcurrentTaskScheduler(Executors.newScheduledThreadPool(5))
        return new ConcurrentTaskScheduler(executor);
    }
}