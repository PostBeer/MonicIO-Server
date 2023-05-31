package com.example.monicio.Config;

import com.example.monicio.Services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Task status configuration
 * Includes cron methods for handling task statuses
 *
 * @author HukoJlauII
 */
@Configuration
@EnableScheduling
public class TaskStatusConfig {

    @Autowired
    private TaskService taskService;

    @Scheduled(cron = "* 0 * * * *")
    public void setExpiredTasks() {
        taskService.findExpiredTasks().forEach(task ->
        {
            task.setStatus("Просрочена");
            taskService.save(task);
        });
    }

}
