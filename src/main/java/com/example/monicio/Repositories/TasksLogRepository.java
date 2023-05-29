package com.example.monicio.Repositories;

import com.example.monicio.Models.TasksLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Repository
@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "logs", path = "logs")
public interface TasksLogRepository extends JpaRepository<TasksLog, Long> {
    Page<TasksLog> findAllByProjectIdOrderByChangedOnDesc(Long id, Pageable pageable);
    Page<TasksLog> findAllByProjectIdIsInOrderByChangedOnDesc(List<Long> ids, Pageable pageable);
}
