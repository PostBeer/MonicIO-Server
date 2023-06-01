package com.example.monicio.Services;

import com.example.monicio.DTO.TaskCreateDto;
import com.example.monicio.Models.Project;
import com.example.monicio.Models.Task;
import com.example.monicio.Models.TasksLog;
import com.example.monicio.Models.User;
import com.example.monicio.Repositories.TaskRepository;
import com.example.monicio.Repositories.TasksLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * The Tasks log repository.
     */
    @Autowired
    private TasksLogRepository tasksLogRepository;

    /**
     * The Project service.
     */
    @Autowired
    private ProjectService projectService;

    /**
     * The User service.
     */
    @Autowired
    private UserService userService;


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
     * Find task by id task.
     *
     * @param id the id
     * @return the task
     */
    public Task findTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    /**
     * Find log by task id list.
     *
     * @param id the id
     * @return the list
     */
    public List<TasksLog> findLogByTaskId(Long id) {
        return tasksLogRepository.findByTaskId(id);
    }

    /**
     * Delete all task logs by task id.
     *
     * @param id the id
     */
    public void deleteAllTaskLogsByTaskId(Long id) {
        tasksLogRepository.deleteAll(findLogByTaskId(id));
    }

    /**
     * Find expired tasks list.
     *
     * @return the list
     */
    public List<Task> findExpiredTasks() {
        return taskRepository.findAll().stream().filter(task ->
                        LocalDateTime.now().isAfter(task.getCompleteDate())
                                && (task.getStatus().equals("Выполняется")
                                || task.getStatus().equals("Назначение исполнителя")))
                .toList();
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

    /**
     * Take task task.
     *
     * @param authentication the authentication
     * @param taskId         the task id
     * @return the task
     */
    public Task takeTask(Authentication authentication, Long taskId) {
        User user = userService.getUserAuthentication(authentication);
        Task task = findTaskById(taskId);
        task.setImplementer(user);
        task.setStatus("Выполняется");
        return save(task);
    }

    /**
     * Send task for check task.
     *
     * @param taskId the task id
     * @return the task
     */
    public Task sendTaskForCheck(Long taskId) {
        Task task = findTaskById(taskId);
        task.setStatus("Отправлена на проверку");
        return save(task);
    }

    /**
     * Cancel task task.
     *
     * @param taskId the task id
     * @return the task
     */
    public Task cancelTask(Long taskId) {
        Task task = findTaskById(taskId);
        task.setStatus("Выполняется");
        return save(task);
    }

    /**
     * Check task task.
     *
     * @param taskId  the task id
     * @param success the success
     * @return the task
     */
    public Task checkTask(Long taskId, boolean success) {
        Task task = findTaskById(taskId);
        if (success) {
            task.setStatus("Выполнена");
        } else {
            task.setStatus("Выполняется");
        }
        return save(task);
    }


    /**
     * Link task to project.
     *
     * @param newTask the new task
     * @param id      the id
     */
    public void linkTaskToProject(Task newTask, Long id) {
        Project project = projectService.findProjectById(id);
        project.getTasks().add(newTask);
        projectService.save(project);
        mapTaskToLog(newTask, project);

    }

    /**
     * Map task to log.
     *
     * @param task    the task
     * @param project the project
     */
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
