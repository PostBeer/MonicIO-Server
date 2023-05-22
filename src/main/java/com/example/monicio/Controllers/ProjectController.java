package com.example.monicio.Controllers;

import com.example.monicio.DTO.TaskCreateDto;
import com.example.monicio.Models.Project;
import com.example.monicio.Models.Task;
import com.example.monicio.Services.ProjectService;
import com.example.monicio.Services.TaskService;
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
    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectService projectService;

    @PostMapping("/{id}/tasks/create")
    public ResponseEntity<?> createNewTask(Authentication authentication, @Valid @RequestBody TaskCreateDto taskCreateDto, BindingResult bindingResult, @PathVariable Long id) {
        if (taskCreateDto.getCompleteDate() != null) {
            if (ChronoUnit.DAYS.between(LocalDateTime.now(), taskCreateDto.getCompleteDate()) < 3) {
                bindingResult.addError(new FieldError("task", "completeDate", "Минимальное время на выполнение задания составляет 3 дня"));
            }
        }
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.CONFLICT);
        }
        Task newTask = taskService.createTask(taskCreateDto, id);
        Project project = projectService.findProjectById(id);
        project.getTasks().add(newTask);
        projectService.save(project);
        return new ResponseEntity<>(newTask, HttpStatus.CREATED);
    }
}
