package com.example.monicio.Controllers;

import com.example.monicio.Config.JWTUtil;
import com.example.monicio.DTO.UserDTO;
import com.example.monicio.DTO.userInfo;
import com.example.monicio.Models.Role;
import com.example.monicio.Models.User;
import com.example.monicio.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (userService.existsByUserName(userDto.getUserName())) {
            return ResponseEntity.badRequest().body("Ошибка: такой пользователь уже существует!");
        }
        userService.registerUser(userDto);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован!");
    }

    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserInfo(Principal user){
        return userService.collectUserData(user);
    }

    public record JwtResponse(String jwt, Long id, String username, List<String> authorities) {}
}
