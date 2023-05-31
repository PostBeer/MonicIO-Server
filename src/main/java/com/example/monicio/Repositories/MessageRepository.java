package com.example.monicio.Repositories;

import com.example.monicio.Models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

/**
 * Repository for {@link Message} entity.
 *
 * @author HukoJlauII
 */
@Repository
@CrossOrigin(origins = "*")
public interface MessageRepository extends JpaRepository<Message, Long> {
    /**
     * Find messages by project id.
     *
     * @param project_id the project id
     * @return the list of messages
     */
    @Query("select m from Message m where m.project.id = :project")
    List<Message> findMessagesByProject_Id(@Param("project") Long project_id);
}