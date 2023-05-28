package com.example.monicio.Config;

import com.example.monicio.Models.ActivationToken;
import com.example.monicio.Models.PasswordToken;
import com.example.monicio.Repositories.ActivationTokenRepository;
import com.example.monicio.Repositories.PasswordTokenRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
/**
 * Expiring configuration
 * Includes cron methods for handling own tokens
 *
 * @author Maxim Milko
 * @see ActivationToken
 * @see PasswordToken
 */
@Configuration
@EnableScheduling
public class ExpiringConfig {
    /**
     * The Activation token repository.
     */
    private ActivationTokenRepository activationTokenRepository;
    /**
     * The Password token repository.
     */
    private PasswordTokenRepository passwordTokenRepository;

    /**
     * Delete expired tokens at 00:00 every day
     */
    @Scheduled(cron = "* 0 0 * * *")
    public void deleteExpiredTokens(){
        List<ActivationToken> aTokens = activationTokenRepository.findAll();
        for (ActivationToken token: aTokens) {
            if (!token.compareDate())
                activationTokenRepository.delete(token);
        }
        List<PasswordToken> pTokens = passwordTokenRepository.findAll();
        for (PasswordToken token: pTokens){
            if (!token.compareDate()){
                passwordTokenRepository.delete(token);
            }
        }
    }
}
