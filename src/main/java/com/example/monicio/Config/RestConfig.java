package com.example.monicio.Config;

import com.example.monicio.Handlers.ProjectHandler;
import com.example.monicio.Models.Media;
import com.example.monicio.Models.Project;
import com.example.monicio.Models.Task;
import com.example.monicio.Models.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * Description of the class or method
 *
 * @author HukoJlauII
 */
@Configuration
public class RestConfig implements RepositoryRestConfigurer {

    @Bean
    ProjectHandler projectHandler() {
        return new ProjectHandler();
    }

    @Override
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(Media.class);
        config.exposeIdsFor(User.class);
        config.exposeIdsFor(Project.class);
        config.exposeIdsFor(Task.class);
    }
}


