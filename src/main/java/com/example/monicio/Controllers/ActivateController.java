package com.example.monicio.Controllers;

import com.example.monicio.Models.ActivationToken;
import com.example.monicio.Repositories.ActivationTokenRepository;
import com.example.monicio.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller for user activation.
 *
 * @author Nikita Zhiznevskiy
 * @see ActivationTokenRepository
 * @see UserService
 */
@Controller
@RequiredArgsConstructor
public class ActivateController {

    /**
     * The Activation token repository.
     */
    private final ActivationTokenRepository activationTokenRepository;
    /**
     * The User service.
     */
    private final UserService userService;

    /**
     * Activate user after click on activation link.
     *
     * @param code the activation code
     * @return response with status 302 if code is valid <br>
     * response with status 409 if code is not valid
     */
    @GetMapping("/activate/{code}")
    public ResponseEntity<?> activate(@PathVariable String code) {
        ActivationToken activationToken = activationTokenRepository.findByToken(code);
        if (activationToken != null && activationToken.compareDate()) {
            userService.activateUser(code);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "localhost:3000/login");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } else
            return new ResponseEntity<>("Токен не действителен", HttpStatus.CONFLICT);
    }
}
