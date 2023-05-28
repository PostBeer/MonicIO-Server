package com.example.monicio.Controllers;


import com.example.monicio.DTO.PasswordForgetDTO;
import com.example.monicio.DTO.PasswordTokenDTO;
import com.example.monicio.Models.PasswordToken;
import com.example.monicio.Repositories.ActivationTokenRepository;
import com.example.monicio.Repositories.PasswordTokenRepository;
import com.example.monicio.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;

/**
 * Controller for user activation.
 *
 * @author Maxim Milko
 * @see ActivationTokenRepository
 * @see UserService
 */
@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PasswordController {

    /**
     * The Password token repository.
     */
    private final PasswordTokenRepository passwordTokenRepository;
    /**
     * The User service.
     */
    private final UserService userService;

    /**
     * Checking for usable password token.
     * METHOD: GET
     * @param token the password token
     * @return response with status 200 if code is valid <br>
     * response with status 409 if code is not valid
     */
    @GetMapping("/forget/{token}")
    public ResponseEntity<?> checkToken(@PathVariable String token){
        PasswordToken passwordToken = passwordTokenRepository.findByToken(token);
        if (passwordToken != null && passwordToken.compareDate()){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Токен не действителен", HttpStatus.CONFLICT);
        }
    }

    /**
     * Change password by User and delete password token.
     * METHOD: POST
     * @param token the password token
     * @return response with status 200 if code is valid <br>
     * response with status 409 if code is not valid
     */
    @PostMapping("/forget/{token}")
    public ResponseEntity<?> acceptTokenAndChangePassword(@Valid @RequestBody PasswordTokenDTO passwordTokenDTO, BindingResult bindingResult, @PathVariable String token){
        if (!passwordTokenDTO.getPassword().equals(passwordTokenDTO.getPasswordConfirm())) {
            bindingResult.addError(new FieldError("user", "passwordConfirm", "Пароли не совпадают"));
        }
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.CONFLICT);
        }
        PasswordToken passwordToken = passwordTokenRepository.findByToken(token);
        if (passwordToken != null && passwordToken.compareDate()){
            userService.changePasswordByToken(passwordTokenDTO, passwordToken);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Токен не действителен", HttpStatus.CONFLICT);
        }
    }

    /**
     * Create password token for user
     * METHOD: POST
     * @param passwordForgetDTO account email
     * @return response with status 200 if email is valid <br>
     * response with status 409 if email is not valid
     */
    @PostMapping("/forget/email")
    public ResponseEntity<?> userForgetPassword(@Valid @RequestBody PasswordForgetDTO passwordForgetDTO, BindingResult bindingResult) throws MessagingException {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.CONFLICT);
        }
        boolean success = userService.createPasswordToken(passwordForgetDTO);
        if (success){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            bindingResult.addError(new FieldError("user", "email", "Пользователя с такой почтой не существует"));
            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.CONFLICT);
        }
    }
}
