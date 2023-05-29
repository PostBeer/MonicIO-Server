package com.example.monicio.Repositories;

import com.example.monicio.Models.Task;
import com.example.monicio.Models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Repository for {@link Task} entity.
 *
 * @author HukoJlauII
 */
@Repository
@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "tasks", path = "tasks")
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAllByImplementerOrderByCompleteDate(User implementer, Pageable pageable);
}