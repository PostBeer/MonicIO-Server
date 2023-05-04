package com.example.monicio.Repositories;


import com.example.monicio.Models.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link ActivationToken} entity.
 *
 * @author Nikita Zhiznevskiy
 */
@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Integer> {

    /**
     * Find activation token entity by token.
     *
     * @param token the token
     * @return the activation token
     */
    ActivationToken findByToken(String token);

    /**
     * Delete activation token by token.
     *
     * @param token the token
     */
    void deleteByToken(String token);
}