package com.example.monicio.Controllers;

import com.example.monicio.DTO.UserDTO;
import com.example.monicio.Models.ActivationToken;
import com.example.monicio.Repositories.ActivationTokenRepository;
import com.example.monicio.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDto) {
        return userService.loginUser(userDto);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDto) {
        if (userService.existsByUserEmail(userDto.getEmail())) {
            return ResponseEntity.badRequest().body("Ошибка: такой пользователь уже существует!");
        }
        userService.registerUser(userDto);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован!");
    }

    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserInfo(Principal user){
        return userService.collectUserData(user);
    }


    public record JwtResponse(String jwt, Long id,String email, String username, List<String> authorities) {}
}
