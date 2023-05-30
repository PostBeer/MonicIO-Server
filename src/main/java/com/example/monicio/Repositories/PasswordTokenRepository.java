package com.example.monicio.Repositories;

import com.example.monicio.Models.PasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link PasswordToken} entity.
 *
 * @author Maxim Milko
 */
@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordToken, Integer> {

    /**
     * Find password token entity by token.
     *
     * @param token the token
     * @return the activation token
     */
    PasswordToken findByToken(String token);

    /**
     * Delete password token by token.
     *
     * @param token the token
     */
    void deleteByToken(String token);
}
