package com.example.monicio.Services;

import com.example.monicio.Models.TasksLog;
import com.example.monicio.Models.User;
import com.example.monicio.Repositories.TasksLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Log service.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class LogService {
    /**
     * The Tasks log repository.
     */
    private final TasksLogRepository tasksLogRepository;
    /**
     * The User service.
     */
    private final UserService userService;

    /**
     * Tasks log for project page.
     *
     * @param id the id
     * @return the page
     */
    public Page<TasksLog> tasksLogForProject(Long id) {
        return tasksLogRepository.findAllByProjectIdOrderByChangedOnDesc(id, PageRequest.of(0, 6));
    }

    /**
     * Tasks log for user page.
     *
     * @param authentication the authentication
     * @return the page
     */
    public Page<TasksLog> tasksLogForUser(Authentication authentication) {
        User user = userService.getUserAuthentication(authentication);
        List<Long> ids = new ArrayList<>();
        user.getProjects().forEach(project -> ids.add(project.getId()));
        return tasksLogRepository.findAllByProjectIdIsInOrderByChangedOnDesc(ids, PageRequest.of(0, 13));
    }
}
