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


/**
 * Service for interaction with {@link User} entity.
 *
 * @author HukoJlauII, Nikita Zhiznevskiy
 * @see org.springframework.security.core.userdetails.UserDetailsService
 * @see UserRepository
 */
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

    /**
     * Save user to database.
     *
     * @param user user to save
     * @return saved user
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Check user existence by email.
     *
     * @param email the email
     * @return true if user exists
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Check user existence by username.
     *
     * @param username the username
     * @return true if user exists
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Find user by username.
     *
     * @param username the username
     * @return the user
     */
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElse(null);
    }

    /**
     * Find user by email.
     *
     * @param email the email
     * @return the user
     */
    public User findUserByEmail(String email) {
        return userRepository.findUserByUsername(email).orElse(null);
    }

    /**
     * Load user details by username .
     *
     * @param username users' username
     * @return the user details
     * @throws UsernameNotFoundException the username not found exception
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("No user with username = " + username));
    }

    /**
     * Convert user to info dto.
     *
     * @param user the user to convert
     * @return the user info dto
     */
    public UserInfoDTO mapUserToInfoDTO(User user) {
        return UserInfoDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .surname(user.getSurname())
                .roles(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();
    }

    /**
     * Validate register response entity.
     *
     * @param registerRequestDTO the register request dto
     * @param bindingResult      the binding result
     * @return response with status 200 if user was registered <br>
     * response with status 409 if register request is not valid
     * @throws MessagingException the messaging exception
     */
    public ResponseEntity<?> validateRegister(RegisterRequestDTO registerRequestDTO, BindingResult bindingResult) throws MessagingException {
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
        createActivationCode(registerUser(registerRequestDTO));
        return ResponseEntity.ok(new RegisterResponseDTO("Пользователь зарегистрирован!"));
    }

    /**
     * Register user.
     *
     * @param registerRequestDTO the register request data
     */
    public User registerUser(RegisterRequestDTO registerRequestDTO) {
        return save(User.builder()
                .username(registerRequestDTO.getUsername())
                .email(registerRequestDTO.getEmail())
                .name(registerRequestDTO.getName())
                .surname(registerRequestDTO.getSurname())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .authorities(Collections.singleton(registerRequestDTO.getRole()))
                .active(false)
                .build());
    }

    /**
     * Login user response entity.
     *
     * @param loginRequestDTO the login request data
     * @return response with status 200 if user was logged in <br>
     * * response with status 401 if credentials is wrong
     */
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

    /**
     * Collect user data response entity.
     *
     * @param authentication the users's authentication
     * @return the response entity with information about user
     */
    public ResponseEntity<?> collectUserData(Authentication authentication) {
        User user = (User) loadUserByUsername(authentication.getName());
        return ResponseEntity.ok(mapUserToInfoDTO(user));
    }


    /**
     * Activate user account.
     *
     * @param code the code for activation
     */
    public void activateUser(String code) {
        User user = activationTokenRepository.findByToken(code).getUser();
        if (user == null) {
            return;
        }
        user.setActive(true);
        activationTokenRepository.deleteByToken(code);
        save(user);
    }


    /**
     * Create activation code for new user and send email with it.
     *
     * @param user user's entity
     * @throws MessagingException the messaging exception
     */
    public void createActivationCode(User user) throws MessagingException {
        String token = UUID.randomUUID().toString();
        ActivationToken myToken = new ActivationToken(token, user, new Date());
        activationTokenRepository.save(myToken);

        if (!ObjectUtils.isEmpty(user.getEmail())) {
            String message = "Привет, " + user.getUsername() + "!" +
                    " Для активации аккаунта перейдите <a href='http://localhost:3000/activate/" + token + "'>по ссылке для подтверждения почты</a>";
            emailService.sendSimpleMessage(user.getEmail(), message);
        }
    }
}