package com.example.monicio.Services;

import com.example.monicio.DTO.ProjectUpdateDTO;
import com.example.monicio.Models.Project;
import com.example.monicio.Models.Task;
import com.example.monicio.Models.User;
import com.example.monicio.Repositories.ProjectRepository;
import com.example.monicio.Repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * The Task repository.
     */
    @Autowired
    private TaskRepository taskRepository;

    /**
     * Find project by id project.
     *
     * @param id the id
     * @return the project
     */
    public Project findProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    public Project findByTasks_Id(Long id) {
        return projectRepository.findByTasks_Id(id);
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
     * Leave project method.
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

    /**
     * Update project.
     *
     * @param projectUpdateDTO the project update dto
     * @param id               the id
     * @return the project
     */
    public Project updateProject(ProjectUpdateDTO projectUpdateDTO, Long id) {
        Project project = findProjectById(id);
        project.setTitle(projectUpdateDTO.getTitle());
        project.setDescription(projectUpdateDTO.getDescription());
        return save(project);
    }

    /**
     * Complete project.
     *
     * @param id the id
     * @return the project
     */
    public Project completeProject(Long id) {
        Project project = findProjectById(id);
        project.setStatus("Завершён");
        return save(project);
    }

    /**
     * Gets projects statuses.
     *
     * @param authentication the authentication
     * @return the projects statuses
     */
    public Page<Project> getProjectsStatuses(Authentication authentication) {
        User user = userService.getUserAuthentication(authentication);
        List<Long> ids = new ArrayList<>();
        user.getProjects().forEach(project -> ids.add(project.getId()));
        return projectRepository.findByIdIn(ids, PageRequest.of(0, 3));
    }

    /**
     * Gets tasks statuses.
     *
     * @param authentication the authentication
     * @return the tasks statuses
     */
    public Page<Task> getTasksStatuses(Authentication authentication) {
        User user = userService.getUserAuthentication(authentication);
        return taskRepository.findAllByImplementerOrderByCompleteDate(user, PageRequest.of(0, 5));
    }
}
