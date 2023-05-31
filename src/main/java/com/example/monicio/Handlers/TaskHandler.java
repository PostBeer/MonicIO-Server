package com.example.monicio.Handlers;

import com.example.monicio.Models.Project;
import com.example.monicio.Models.Task;
import com.example.monicio.Services.ProjectService;
import com.example.monicio.Services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

/**
 * Description of the class or method
 *
 * @author HukoJlauII
 */
@RepositoryEventHandler(Task.class)
public class TaskHandler {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @HandleBeforeDelete
    public void deleteTaskFromProject(Task task) {
        Project project = projectService.findByTasks_Id(task.getId());
        project.getTasks().remove(task);
        taskService.deleteAllTaskLogsByTaskId(task.getId());
        taskService.save(task);
    }
}
