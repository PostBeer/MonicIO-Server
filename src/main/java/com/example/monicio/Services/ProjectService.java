package com.example.monicio.Services;

import com.example.monicio.Models.Project;
import com.example.monicio.Models.TasksLog;
import com.example.monicio.Models.User;
import com.example.monicio.Repositories.ProjectRepository;
import com.example.monicio.Repositories.TasksLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description of the class or method
 *
 * @author HukoJlauII
 */
@Service
@Transactional
public class ProjectService {
    /**
     * The Project repository.
     */
    @Autowired
    private ProjectRepository projectRepository;

    /**
     * The User service.
     */
    @Autowired
    private UserService userService;

    @Autowired
    private TasksLogRepository tasksLogRepository;

    /**
     * Find project by id project.
     *
     * @param id the id
     * @return the project
     */
    public Project findProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    /**
     * Save project.
     *
     * @param project the project
     * @return the project
     */
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    /**
     * Send request project.
     *
     * @param user    the user
     * @param project the project
     * @return the project
     */
    public Project sendRequest(User user, Project project) {
        project.getRequests().add(user);
        return save(project);
    }

    /**
     * Cancel request project.
     *
     * @param authentication the authentication
     * @param id             the id
     * @return the project
     */
    public Project cancelRequest(Authentication authentication, Long id) {
        User user = userService.getUserAuthentication(authentication);
        Project project = findProjectById(id);
        project.getRequests().remove(user);
        return save(project);
    }

    /**
     * Complete request project.
     *
     * @param id       the id
     * @param username the username
     * @param success  the success
     * @return the project
     */
    public Project completeRequest(Long id, String username, boolean success) {
        User user = userService.findUserByUsername(username);
        Project project = findProjectById(id);
        project.getRequests().remove(user);
        if (success) {
            project.getUsers().add(user);
            user.getProjects().add(project);
            userService.save(user);
        }
        return save(project);

    }

    /**
     * Leave project project.
     *
     * @param authentication the authentication
     * @param id             the id
     * @return the project
     */
    public Project leaveProject(Authentication authentication, Long id) {
        User user = userService.getUserAuthentication(authentication);
        Project project = findProjectById(id);
        project.getUsers().remove(user);
        user.getProjects().remove(project);
        userService.save(user);
        return save(project);

    }

    public Page<TasksLog> tasksLog(Long id) {
        return tasksLogRepository.findAllByProjectIdOrderByChangedOnDesc(id, PageRequest.of(0, 6));
    }
}
