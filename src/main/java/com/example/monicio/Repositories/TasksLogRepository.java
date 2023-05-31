package com.example.monicio.Repositories;

import com.example.monicio.Models.TasksLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

/**
 * The interface Tasks log repository.
 */
@Repository
@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "logs", path = "logs")
public interface TasksLogRepository extends JpaRepository<TasksLog, Long> {
    /**
     * Find all by project id order by changed on desc page.
     *
     * @param id       the id
     * @param pageable the pageable
     * @return the page
     */
    Page<TasksLog> findAllByProjectIdOrderByChangedOnDesc(Long id, Pageable pageable);

    /**
     * Find all by project id is in order by changed on desc page.
     *
     * @param ids      the ids
     * @param pageable the pageable
     * @return the page
     */
    Page<TasksLog> findAllByProjectIdIsInOrderByChangedOnDesc(List<Long> ids, Pageable pageable);
}
