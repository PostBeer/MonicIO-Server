package com.example.monicio.Repositories;

import com.example.monicio.Models.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;


/**
 * Repository for {@link Project} entity.
 *
 * @author HukoJlauII
 */
@Repository
@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "projects", path = "projects")
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findByIdIn(List<Long> ids, Pageable pageable);

    Project findByTasks_Id(Long id);


}