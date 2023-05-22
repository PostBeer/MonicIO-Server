package com.example.monicio.Services;

import com.example.monicio.Models.Project;
import com.example.monicio.Repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ProjectRepository projectRepository;

    public Project findProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    public Project save(Project project) {
        return projectRepository.save(project);
    }
}
