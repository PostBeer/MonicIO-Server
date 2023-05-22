package com.example.monicio.Repositories;


import com.example.monicio.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;


/**
 * Repository for {@link User} entity.
 *
 * @author Nikita Zhiznevskiy,HukoJlauII
 * @see com.example.monicio.Services.UserService
 */
@Repository
@CrossOrigin(origins = "http://localhost:3000")
@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Check user existence by email.
     *
     * @param email the email
     * @return true if user exists
     */
    boolean existsByEmail(String email);

    /**
     * Check user existence by username .
     *
     * @param username the  username
     * @return true if user exists
     */
    boolean existsByUsername(String username);

    /**
     * Find user by username.
     *
     * @param username the username
     * @return the user if exists
     */
    Optional<User> findUserByUsername(String username);

    /**
     * Find user by email.
     *
     * @param email the email
     * @return the user if exists
     */
    Optional<User> findUserByEmail(String email);

    @Query("select u from User u inner join u.projects projects where projects.id = ?1")
    List<User> findByProjects_Id(Long id);


}

