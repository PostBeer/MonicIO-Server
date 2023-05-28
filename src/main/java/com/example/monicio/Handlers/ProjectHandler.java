package com.example.monicio.Handlers;

import com.example.monicio.Models.Project;
import com.example.monicio.Models.User;
import com.example.monicio.Services.ProjectService;
import com.example.monicio.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Description of the class or method
 *
 * @author HukoJlauII
 */
@RepositoryEventHandler(Project.class)
public class ProjectHandler {
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;

    @HandleAfterCreate
    public void linkProjectToUser(Project project) {
        User user = (User) userService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        user.getProjects().add(project);
        project.getUsers().add(user);
        projectService.save(project);
        userService.save(user);
    }

    @HandleBeforeDelete
    public void deleteProjectFromUsers(Project project) {
        userService.findByProjects_Id(project.getId()).forEach(user -> {
            user.getProjects().remove(project);
            userService.save(user);
        });
    }


}
