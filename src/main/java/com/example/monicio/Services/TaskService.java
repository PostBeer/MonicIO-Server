package com.example.monicio.Services;

import com.example.monicio.DTO.TaskCreateDto;
import com.example.monicio.Models.Task;
import com.example.monicio.Repositories.TaskRepository;
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
    @Autowired
    private TaskRepository taskRepository;


    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public Task createTask(TaskCreateDto taskCreateDto, Long id) {
        Task task = Task.builder()
                .name(taskCreateDto.getName())
                .description(taskCreateDto.getDescription())
                .creationDate(LocalDateTime.now())
                .completeDate(taskCreateDto.getCompleteDate())
                .status("Назначение исполнителя")
                .build();
        return save(task);
    }
}
