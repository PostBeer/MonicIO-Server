package com.example.monicio.Services;


import com.example.monicio.Config.JWTUtil;
import com.example.monicio.Controllers.AuthController;
import com.example.monicio.DTO.UserDTO;
import com.example.monicio.DTO.userInfo;
import com.example.monicio.Models.Role;
import com.example.monicio.Models.User;
import com.example.monicio.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
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

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private  UserRepository userRepo;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    @Autowired
    private  JWTUtil jwtUtil;

    public void save(User user){
        userRepo.save(user);
    }
    public boolean existsByUserName(String userName){
        return userRepo.findByUsername(userName).isPresent();
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("No user with username = " + username));
    }

    public void registerUser(UserDTO userDto){
        User user = new User(
                userDto.getUserName(),
                passwordEncoder.encode(userDto.getPassword()),
                Set.of(Role.ROLE_ADMIN)
        );
        save(user);
    }

    public ResponseEntity<?> loginUser(UserDTO userDto){
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getUserName(), userDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(user.getUsername());

        List<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return  ResponseEntity.ok(new AuthController.JwtResponse(jwt, user.getId(), user.getUsername(), authorities));
    }

    public ResponseEntity<?> collectUserData(Principal user){
        User userObj=(User) loadUserByUsername(user.getName());

        userInfo userInfo=new userInfo();
        userInfo.setUserName(userObj.getUsername());
        userInfo.setRoles(userObj.getAuthorities().toArray());


        return ResponseEntity.ok(userInfo);
    }
}