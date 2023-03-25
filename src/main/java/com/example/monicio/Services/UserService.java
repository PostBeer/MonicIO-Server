package com.example.monicio.Services;


import com.example.monicio.Config.JWT.JWTUtil;
import com.example.monicio.Controllers.AuthController;
import com.example.monicio.DTO.UserDTO;
import com.example.monicio.DTO.ValidateDTO.RegisterRequestDTO;
import com.example.monicio.Models.ActivationToken;
import com.example.monicio.Models.Role;
import com.example.monicio.Models.User;
import com.example.monicio.Repositories.ActivationTokenRepository;
import com.example.monicio.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.mail.MessagingException;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class UserService implements UserDetailsService {
    @Autowired
    private  UserRepository userRepo;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    @Autowired
    private  JWTUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ActivationTokenRepository activationTokenRepository;

    public void save(User user){
        userRepo.save(user);
//        try {
//            createActivationCode(user.getEmail());
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
    }
    public boolean existsByUserEmail(String email){
        return userRepo.findUserByEmail(email).isPresent();
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("No user with username = " + username));
    }

    public void registerUser(RegisterRequestDTO registerRequestDTO){
        User user = new User(
                registerRequestDTO.getEmail(),
                registerRequestDTO.getUserName(),
                passwordEncoder.encode(registerRequestDTO.getPassword()),
                Set.of(Role.ROLE_ADMIN),
                false
        );
        save(user);
    }

    public ResponseEntity<?> loginUser(UserDTO userDto){
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getUserName(), userDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        user.setEmail(userDto.getEmail());
        String jwt = jwtUtil.generateToken(user.getUsername());

        List<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return  ResponseEntity.ok(new AuthController.JwtResponse(jwt, user.getId(),user.getEmail(), user.getUsername(), authorities));
    }

    public ResponseEntity<?> collectUserData(Principal user){
        User userObj=(User) loadUserByUsername(user.getName());

        UserDTO userDto=new UserDTO();
        userDto.setUserName(userObj.getUsername());
        userDto.setEmail(userObj.getEmail());
        userDto.setRoles(userObj.getAuthorities().toArray());


        return ResponseEntity.ok(userDto);
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

    public User findUserByEmail(String email) {
        return userRepo.findUserByEmail(email).get();
    }

    public void createActivationCode(String userEmail) throws MessagingException {
        User user = findUserByEmail(userEmail);
        String token = UUID.randomUUID().toString();
        ActivationToken myToken = new ActivationToken(token, user, new Date());
        activationTokenRepository.save(myToken);

        if (!ObjectUtils.isEmpty(user.getEmail())) {
            String message = "Привет, " + user.getUsername() + "!" +
                    " для активации аккаунта перейдите <a href='http://localhost:8080/activate/" + token + "'>по ссылке для подтверждения почты</a>"
                    +"а затем продолжите логин <a href='http://localhost:3000/login/'>по ссылке</a>";
            emailService.sendSimpleMessage(user.getEmail(), message);
        }
    }
}