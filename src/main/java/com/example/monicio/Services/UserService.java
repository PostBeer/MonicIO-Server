package com.example.monicio.Services;


import com.example.monicio.Config.JWT.JWTUtil;
import com.example.monicio.DTO.*;
import com.example.monicio.Models.ActivationToken;
import com.example.monicio.Models.User;
import com.example.monicio.Repositories.ActivationTokenRepository;
import com.example.monicio.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.mail.MessagingException;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ActivationTokenRepository activationTokenRepository;

    //    public void save(User user){
//        userRepository.save(user);
////        try {
////            createActivationCode(user.getEmail());
////        } catch (MessagingException e) {
////            throw new RuntimeException(e);
////        }
//    }
    public User save(User user) {
        return userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElse(null);
    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByUsername(email).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("No user with username = " + username));
    }

    public UserInfoDTO mapUserToInfoDTO(User user) {
        return UserInfoDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .surname(user.getSurname())
                .roles(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();
    }

    public ResponseEntity<?> validateRegister(RegisterRequestDTO registerRequestDTO, BindingResult bindingResult) {
        if (!registerRequestDTO.getPassword().equals(registerRequestDTO.getPasswordConfirm())) {
            bindingResult.addError(new FieldError("user", "passwordConfirm", "Пароли не совпадают"));
        }
        if (existsByUsername(registerRequestDTO.getUsername())) {
            bindingResult.addError(new FieldError("user", "username", "Пользователь с таким никнеймом уже существует"));
        }
        if (existsByEmail(registerRequestDTO.getEmail())) {
            bindingResult.addError(new FieldError("user", "email", "Пользователь с такой почтой уже существует"));
        }
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.CONFLICT);
        }
        registerUser(registerRequestDTO);
        return ResponseEntity.ok(new RegisterResponseDTO("Пользователь зарегистрирован!"));
    }

    public void registerUser(RegisterRequestDTO registerRequestDTO) {
        save(User.builder()
                .username(registerRequestDTO.getUsername())
                .email(registerRequestDTO.getEmail())
                .name(registerRequestDTO.getName())
                .surname(registerRequestDTO.getSurname())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .authorities(Collections.singleton(registerRequestDTO.getRole()))
                .active(false)
                .build());
    }

    public ResponseEntity<?> loginUser(LoginRequestDTO loginRequestDTO) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = findUserByUsername(loginRequestDTO.getUsername());
        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .jwt(jwtUtil.generateToken(user.getUsername()))
                .userInfoDTO(mapUserToInfoDTO(user))
                .build();
        return ResponseEntity.ok(loginResponseDTO);
    }

    public ResponseEntity<?> collectUserData(Authentication authentication) {
        User user = (User) loadUserByUsername(authentication.getName());
        return ResponseEntity.ok(mapUserToInfoDTO(user));
    }


    public void activateUser(String code) {
        User user = activationTokenRepository.findByToken(code).getUser();
        if (user == null) {
            return;
        }
        user.setActive(true);
        activationTokenRepository.deleteByToken(code);
        save(user);
    }


    public void createActivationCode(String userEmail) throws MessagingException {
        User user = findUserByEmail(userEmail);
        String token = UUID.randomUUID().toString();
        ActivationToken myToken = new ActivationToken(token, user, new Date());
        activationTokenRepository.save(myToken);

        if (!ObjectUtils.isEmpty(user.getEmail())) {
            String message = "Привет, " + user.getUsername() + "!" +
                    " для активации аккаунта перейдите <a href='http://localhost:8080/activate/" + token + "'>по ссылке для подтверждения почты</a>"
                    + "а затем продолжите логин <a href='http://localhost:3000/login/'>по ссылке</a>";
            emailService.sendSimpleMessage(user.getEmail(), message);
        }
    }
}