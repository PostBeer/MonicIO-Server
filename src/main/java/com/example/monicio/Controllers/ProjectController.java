package com.example.monicio.Controllers;

import com.example.monicio.DTO.ProjectUpdateDTO;
import com.example.monicio.DTO.TaskCreateDto;
import com.example.monicio.Models.Project;
import com.example.monicio.Models.Task;
import com.example.monicio.Models.User;
import com.example.monicio.Services.ProjectService;
import com.example.monicio.Services.TaskService;
import com.example.monicio.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Description of the class or method
 *
 * @author HukoJlauII
 */
@RestController
@RequestMapping("/api/projects")
@Secured({"ADMIN", "USER", "PROJECT_MANAGER"})
@CrossOrigin(origins = "http://localhost:3000")
public class ProjectController {
    /**
     * The Task service.
     */
    @Autowired
    private TaskService taskService;

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
     * Create new task response entity.
     *
     * @param taskCreateDto the task create dto
     * @param bindingResult the binding result
     * @param id            the id
     * @return the response entity
     */
    @Secured("PROJECT_MANAGER")
    @PostMapping("/{id}/tasks/create")
    public ResponseEntity<?> createNewTask(@Valid @RequestBody TaskCreateDto taskCreateDto, BindingResult bindingResult, @PathVariable Long id) {
        if (taskCreateDto.getCompleteDate() != null) {
            if (ChronoUnit.DAYS.between(LocalDateTime.now(), taskCreateDto.getCompleteDate()) < 3) {
                bindingResult.addError(new FieldError("task", "completeDate", "Минимальное время на выполнение задания составляет 3 дня"));
            }
        }
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.CONFLICT);
        }
        Task newTask = taskService.createTask(taskCreateDto);
        taskService.linkTaskToProject(newTask, id);
        return new ResponseEntity<>(newTask, HttpStatus.CREATED);
    }

    /**
     * Take existing task response entity.
     *
     * @param authentication the authentication
     * @param id             the id
     * @param taskId         the task id
     * @return the response entity
     */
    @PostMapping("/{id}/tasks/take")
    public ResponseEntity<?> takeExistingTask(Authentication authentication, @PathVariable Long id, @RequestParam("task") Long taskId) {
        return new ResponseEntity<>(taskService.takeTask(authentication, taskId), HttpStatus.OK);
    }

    /**
     * Send task for check response entity.
     *
     * @param id     the id
     * @param taskId the task id
     * @return the response entity
     */
    @PostMapping("/{id}/tasks/forCheck")
    public ResponseEntity<?> sendTaskForCheck(@PathVariable Long id, @RequestParam("task") Long taskId) {
        return new ResponseEntity<>(taskService.sendTaskForCheck(taskId), HttpStatus.OK);
    }

    /**
     * Cancel task response entity.
     *
     * @param id     the id
     * @param taskId the task id
     * @return the response entity
     */
    @PostMapping("/{id}/tasks/forCheck/cancel")
    public ResponseEntity<?> cancelTask(@PathVariable Long id, @RequestParam("task") Long taskId) {
        return new ResponseEntity<>(taskService.cancelTask(taskId), HttpStatus.OK);
    }

    /**
     * Check task response entity.
     *
     * @param id      the id
     * @param taskId  the task id
     * @param success the success
     * @return the response entity
     */
    @PostMapping("/{id}/tasks/check")
    public ResponseEntity<?> checkTask(@PathVariable Long id, @RequestParam("task") Long taskId, @RequestParam("success") boolean success) {

        return new ResponseEntity<>(taskService.checkTask(taskId, success), HttpStatus.OK);
    }

    /**
     * Update project response entity.
     *
     * @param projectUpdateDTO the project update dto
     * @param bindingResult    the binding result
     * @param id               the id
     * @return the response entity
     */
    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateProject(@RequestBody @Valid ProjectUpdateDTO projectUpdateDTO, BindingResult bindingResult, @PathVariable Long id) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(projectService.updateProject(projectUpdateDTO, id), HttpStatus.OK);
    }

    /**
     * Leave project response entity.
     *
     * @param id the id
     * @return the response entity
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> leaveProject(@PathVariable Long id) {
        return new ResponseEntity<>(projectService.completeProject(id), HttpStatus.OK);
    }

    /**
     * Send request response entity.
     *
     * @param authentication the authentication
     * @param id             the id
     * @return the response entity
     */
    @PostMapping("/{id}/request")
    public ResponseEntity<?> sendRequest(Authentication authentication, @PathVariable Long id) {
        User user = userService.getUserAuthentication(authentication);
        Project project = projectService.findProjectById(id);
        if (project.getRequests().contains(user)) {
            return new ResponseEntity<>("Request has already sent", HttpStatus.BAD_REQUEST);
        }
        if (project.getUsers().contains(user)) {
            return new ResponseEntity<>("The user is already a member of the project", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(projectService.sendRequest(user, project), HttpStatus.OK);
    }

    /**
     * Cancel request response entity.
     *
     * @param authentication the authentication
     * @param id             the id
     * @return the response entity
     */
    @Secured("USER")
    @PostMapping("/{id}/request/cancel")
    public ResponseEntity<?> cancelRequest(Authentication authentication, @PathVariable Long id) {
        return new ResponseEntity<>(projectService.cancelRequest(authentication, id), HttpStatus.OK);
    }

    /**
     * Complete request response entity.
     *
     * @param id       the id
     * @param username the username
     * @param success  the success
     * @return the response entity
     */
    @Secured("PROJECT_MANAGER")
    @PostMapping("/{id}/request/{username}")
    public ResponseEntity<?> completeRequest(@PathVariable Long id, @PathVariable String username, @RequestParam boolean success) {
        return new ResponseEntity<>(projectService.completeRequest(id, username, success), HttpStatus.OK);
    }

    /**
     * Leave project response entity.
     *
     * @param authentication the authentication
     * @param id             the id
     * @return the response entity
     */
    @Secured("USER")
    @PostMapping("/{id}/leave")
    public ResponseEntity<?> leaveProject(Authentication authentication, @PathVariable Long id) {
        return new ResponseEntity<>(projectService.leaveProject(authentication, id), HttpStatus.OK);
    }

    /**
     * Gets projects statuses.
     *
     * @param authentication the authentication
     * @return the projects statuses
     */
    @GetMapping("/status")
    public ResponseEntity<?> getProjectsStatuses(Authentication authentication) {
        return new ResponseEntity<>(projectService.getProjectsStatuses(authentication), HttpStatus.OK);
    }

    /**
     * Gets tasks statuses.
     *
     * @param authentication the authentication
     * @return the tasks statuses
     */
    @GetMapping("/status/tasks")
    public ResponseEntity<?> getTasksStatuses(Authentication authentication) {
        return new ResponseEntity<>(projectService.getTasksStatuses(authentication), HttpStatus.OK);
    }
}
