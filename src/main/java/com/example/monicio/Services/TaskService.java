package com.example.monicio.Services;

import com.example.monicio.DTO.TaskCreateDto;
import com.example.monicio.Models.Project;
import com.example.monicio.Models.Task;
import com.example.monicio.Models.TasksLog;
import com.example.monicio.Repositories.TaskRepository;
import com.example.monicio.Repositories.TasksLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Description of the class or method
 *
 * @author HukoJlauII
 */
@Service
@Transactional
public class TaskService {
    /**
     * The Task repository.
     */
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TasksLogRepository tasksLogRepository;

    @Autowired
    private ProjectService projectService;


    /**
     * Save task.
     *
     * @param task the task
     * @return the task
     */
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    /**
     * Create task task.
     *
     * @param taskCreateDto the task create dto
     * @return the task
     */
    public Task createTask(TaskCreateDto taskCreateDto) {
        Task task = Task.builder()
                .name(taskCreateDto.getName())
                .description(taskCreateDto.getDescription())
                .creationDate(LocalDateTime.now())
                .completeDate(taskCreateDto.getCompleteDate())
                .status("Назначение исполнителя")
                .build();
        return save(task);
    }

    public void linkTaskToProject(Task newTask, Long id) {
        Project project = projectService.findProjectById(id);
        project.getTasks().add(newTask);
        projectService.save(project);
        mapTaskToLog(newTask, project);

    }

    private void mapTaskToLog(Task task, Project project) {
        tasksLogRepository.save(TasksLog.builder()
                .projectId(project.getId())
                .projectName(project.getTitle())
                .taskId(task.getId())
                .taskName(task.getName())
                .status(task.getStatus())
                .changedOn(task.getCreationDate()).build());
    }
}
