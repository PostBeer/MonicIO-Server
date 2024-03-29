package com.example.monicio.Services;


import com.example.monicio.Config.JWT.JWTUtil;
import com.example.monicio.DTO.*;
import com.example.monicio.Models.ActivationToken;
import com.example.monicio.Models.Media;
import com.example.monicio.Models.PasswordToken;
import com.example.monicio.Models.User;
import com.example.monicio.Repositories.ActivationTokenRepository;
import com.example.monicio.Repositories.PasswordTokenRepository;
import com.example.monicio.Repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
    private Validator validator;

    @Autowired
    private ActivationTokenRepository activationTokenRepository;

    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    @Value("${CLIENT_URL}")
    private String CLIENT_URL;

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
     * Find by projects id list.
     *
     * @param id the id
     * @return the list
     */
    public List<User> findByProjects_Id(Long id) {
        return userRepository.findByProjects_Id(id);
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
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("No user with username" + username));
    }

    /**
     * Convert user to info dto.
     *
     * @param user the user to convert
     * @return the user info dto
     */
    public UserInfoDTO mapUserToInfoDTO(User user) {
        return UserInfoDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .surname(user.getSurname())
                .avatar(user.getAvatar())
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
     * Get user authentication user.
     *
     * @param authentication the authentication
     * @return the user
     */
    public User getUserAuthentication(Authentication authentication) {
        return findUserByUsername(authentication.getName());
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
     * Change user password response entity.
     *
     * @param changePasswordDto the change password dto
     * @param bindingResult     the binding result
     * @param authentication    the authentication
     * @return the response entity
     */
    public ResponseEntity<?> changeUserPassword(ChangePasswordDto changePasswordDto, BindingResult bindingResult, Authentication authentication) {
        User user = getUserAuthentication(authentication);

        if (!passwordEncoder.matches(changePasswordDto.getPassword(), user.getPassword())) {
            bindingResult.addError(new FieldError("user", "password", "Старый пароль неверный"));
            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.CONFLICT);
        }
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getNewPasswordConfirm())) {
            bindingResult.addError(new FieldError("user", "newPasswordConfirm", "Пароли не совпадают"));
            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.CONFLICT);
        }
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok(mapUserToInfoDTO(user));
    }

    /**
     * Change user info response entity.
     *
     * @param authentication the authentication
     * @param multipartFile  the multipart file
     * @param changeUserInfo the change user info
     * @return the response entity
     * @throws IOException the io exception
     */
    public ResponseEntity<?> changeUserInfo(Authentication authentication, MultipartFile multipartFile, String changeUserInfo) throws IOException {
        User user = getUserAuthentication(authentication);
        ChangeUserDto changeUserDto = new ObjectMapper().readValue(changeUserInfo, ChangeUserDto.class);
        SpringValidatorAdapter springValidator = new SpringValidatorAdapter(validator);
        BindingResult bindingResult = new BeanPropertyBindingResult(changeUserDto, "changeUserDtoResult");
        springValidator.validate(changeUserDto, bindingResult);
        if (!changeUserDto.getEmail().equals(user.getEmail()) && existsByEmail(changeUserDto.getEmail())) {
            bindingResult.addError(new FieldError("user", "email", "Пользователь с такой почтой уже существует"));
        }
        if (!changeUserDto.getUsername().equals(user.getUsername()) && existsByUsername(changeUserDto.getUsername())) {
            bindingResult.addError(new FieldError("user", "username", "Пользователь с таким именем уже существует"));
        }
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.CONFLICT);
        }
        if (!changeUserDto.getUsername().equals(user.getUsername())) {
            user.setUsername(changeUserDto.getUsername());
            authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        user.setName(changeUserDto.getName());
        user.setSurname(changeUserDto.getSurname());
        user.setEmail(changeUserDto.getEmail());
        if (multipartFile != null) {
            Media media = Media.builder()
                    .originalFileName(multipartFile.getOriginalFilename())
                    .mediaType(multipartFile.getContentType())
                    .size(multipartFile.getSize())
                    .bytes(multipartFile.getBytes()).build();
            user.setAvatar(media);
        }
        save(user);
        return new ResponseEntity<>(mapUserToInfoDTO(user), HttpStatus.OK);
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
                    " Для активации аккаунта перейдите <a href='" + CLIENT_URL + "/activate/" + token + "'>по ссылке для подтверждения почты</a>";
            emailService.sendSimpleMessage(user.getEmail(), message);
        }
    }

    /**
     * Create activation code for new user and send email with it.
     *
     * @param passwordTokenDTO passwordDTO validated
     * @param token            PasswordToken by user
     */
    public void changePasswordByToken(PasswordTokenDTO passwordTokenDTO, PasswordToken token) {
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(passwordTokenDTO.getPassword()));
        passwordTokenRepository.delete(token);
        userRepository.save(user);
    }


    /**
     * Create password code for user has forgotten his password
     *
     * @param passwordForgetDTO Email DTO
     * @throws MessagingException the messaging exception
     */
    public boolean createPasswordToken(PasswordForgetDTO passwordForgetDTO) throws MessagingException {
        User user = userRepository.findUserByEmail(passwordForgetDTO.getEmail()).orElse(null);
        if (user == null) {
            return false;
        }
        String token = UUID.randomUUID().toString();
        PasswordToken passwordToken = new PasswordToken(token, user, new Date());
        passwordTokenRepository.save(passwordToken);

        if (!ObjectUtils.isEmpty(user.getEmail())) {
            String message = "Привет, " + user.getUsername() + "!" +
                    " Для восстановления аккаунта перейдите <a href='" + CLIENT_URL + "/forget/" + token + "'>по ссылке для замены пароля</a>";
            emailService.sendSimpleMessage(user.getEmail(), message);
            return true;
        }
        return false;
    }

    /**
     * Sending callback to developer team
     *
     * @param callbackRequestDTO Information about callback
     * @throws MessagingException the messaging exception
     */
    public void sendCallback(CallbackRequestDTO callbackRequestDTO) throws MessagingException {
        String message = "Сообщение в студию от " + callbackRequestDTO.getName() +
                "<br> Email для ответа: " + callbackRequestDTO.getEmail() +
                "<br> Тема сообщения: " + callbackRequestDTO.getTheme() +
                "<br> Сообщение: " + callbackRequestDTO.getMessage();
        emailService.sendSimpleMessage("postbeer322@gmail.com", message);
    }
}