package com.example.monicio.Repositories;

import com.example.monicio.Models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
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
@RepositoryRestResource(collectionResourceRel = "messages", path = "messages")
public interface MessageRepository extends JpaRepository<Message, Long> {
    /**
     * Find messages by project id.
     *
     * @param project_id the project id
     * @return the list of messages
     */
    List<Message> findMessagesByProject_Id(Long project_id);
}