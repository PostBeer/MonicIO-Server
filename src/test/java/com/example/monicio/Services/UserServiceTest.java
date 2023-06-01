package com.example.monicio.Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.monicio.Config.JWT.JWTUtil;
import com.example.monicio.DTO.CallbackRequestDTO;
import com.example.monicio.DTO.ChangePasswordDto;
import com.example.monicio.DTO.LoginRequestDTO;
import com.example.monicio.DTO.LoginResponseDTO;
import com.example.monicio.DTO.PasswordForgetDTO;
import com.example.monicio.DTO.PasswordTokenDTO;
import com.example.monicio.DTO.RegisterRequestDTO;
import com.example.monicio.DTO.UserInfoDTO;
import com.example.monicio.Models.ActivationToken;
import com.example.monicio.Models.Media;
import com.example.monicio.Models.PasswordToken;
import com.example.monicio.Models.Project;
import com.example.monicio.Models.Role;
import com.example.monicio.Models.User;
import com.example.monicio.Repositories.ActivationTokenRepository;
import com.example.monicio.Repositories.PasswordTokenRepository;
import com.example.monicio.Repositories.UserRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.mail.MessagingException;
import javax.validation.Validator;

import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;

@ContextConfiguration(classes = {UserService.class})
@ExtendWith(SpringExtension.class)
class UserServiceTest {
    @MockBean
    private ActivationTokenRepository activationTokenRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private EmailService emailService;

    @MockBean
    private JWTUtil jWTUtil;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private PasswordTokenRepository passwordTokenRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @MockBean
    private Validator validator;

    /**
     * Method under test: {@link UserService#save(User)}
     */
    @Test
    void testSave() throws UnsupportedEncodingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        when(userRepository.save(Mockito.<User>any())).thenReturn(user);

        Media avatar2 = new Media();
        avatar2.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar2.setId(1L);
        avatar2.setMediaType("Media Type");
        avatar2.setOriginalFileName("foo.txt");
        avatar2.setSize(3L);

        User user2 = new User();
        user2.setActive(true);
        user2.setAuthorities(new HashSet<>());
        user2.setAvatar(avatar2);
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setName("Name");
        user2.setPassword("iloveyou");
        user2.setPasswordConfirm("Password Confirm");
        user2.setProjects(new HashSet<>());
        user2.setSurname("Doe");
        user2.setUsername("janedoe");
        assertSame(user, userService.save(user2));
        verify(userRepository).save(Mockito.<User>any());
    }

    /**
     * Method under test: {@link UserService#existsByEmail(String)}
     */
    @Test
    void testExistsByEmail() {
        when(userRepository.existsByEmail(Mockito.<String>any())).thenReturn(true);
        assertTrue(userService.existsByEmail("jane.doe@example.org"));
        verify(userRepository).existsByEmail(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#existsByEmail(String)}
     */
    @Test
    void testExistsByEmail2() {
        when(userRepository.existsByEmail(Mockito.<String>any())).thenReturn(false);
        assertFalse(userService.existsByEmail("jane.doe@example.org"));
        verify(userRepository).existsByEmail(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#existsByEmail(String)}
     */
    @Test
    void testExistsByEmail3() {
        when(userRepository.existsByEmail(Mockito.<String>any())).thenThrow(new UsernameNotFoundException("Msg"));
        assertThrows(UsernameNotFoundException.class, () -> userService.existsByEmail("jane.doe@example.org"));
        verify(userRepository).existsByEmail(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#existsByUsername(String)}
     */
    @Test
    void testExistsByUsername() {
        when(userRepository.existsByUsername(Mockito.<String>any())).thenReturn(true);
        assertTrue(userService.existsByUsername("janedoe"));
        verify(userRepository).existsByUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#existsByUsername(String)}
     */
    @Test
    void testExistsByUsername2() {
        when(userRepository.existsByUsername(Mockito.<String>any())).thenReturn(false);
        assertFalse(userService.existsByUsername("janedoe"));
        verify(userRepository).existsByUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#existsByUsername(String)}
     */
    @Test
    void testExistsByUsername3() {
        when(userRepository.existsByUsername(Mockito.<String>any())).thenThrow(new UsernameNotFoundException("Msg"));
        assertThrows(UsernameNotFoundException.class, () -> userService.existsByUsername("janedoe"));
        verify(userRepository).existsByUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#findUserByUsername(String)}
     */
    @Test
    void testFindUserByUsername() throws UnsupportedEncodingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(ofResult);
        assertSame(user, userService.findUserByUsername("janedoe"));
        verify(userRepository).findUserByUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#findUserByUsername(String)}
     */
    @Test
    void testFindUserByUsername2() {
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenThrow(new UsernameNotFoundException("Msg"));
        assertThrows(UsernameNotFoundException.class, () -> userService.findUserByUsername("janedoe"));
        verify(userRepository).findUserByUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#findUserByEmail(String)}
     */
    @Test
    void testFindUserByEmail() throws UnsupportedEncodingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(ofResult);
        assertSame(user, userService.findUserByEmail("jane.doe@example.org"));
        verify(userRepository).findUserByUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#findUserByEmail(String)}
     */
    @Test
    void testFindUserByEmail2() {
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenThrow(new UsernameNotFoundException("Msg"));
        assertThrows(UsernameNotFoundException.class, () -> userService.findUserByEmail("jane.doe@example.org"));
        verify(userRepository).findUserByUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#findByProjects_Id(Long)}
     */
    @Test
    void testFindByProjects_Id() {
        ArrayList<User> userList = new ArrayList<>();
        when(userRepository.findByProjects_Id(Mockito.<Long>any())).thenReturn(userList);
        List<User> actualFindByProjects_IdResult = userService.findByProjects_Id(1L);
        assertSame(userList, actualFindByProjects_IdResult);
        assertTrue(actualFindByProjects_IdResult.isEmpty());
        verify(userRepository).findByProjects_Id(Mockito.<Long>any());
    }

    /**
     * Method under test: {@link UserService#findByProjects_Id(Long)}
     */
    @Test
    void testFindByProjects_Id2() {
        when(userRepository.findByProjects_Id(Mockito.<Long>any())).thenThrow(new UsernameNotFoundException("Msg"));
        assertThrows(UsernameNotFoundException.class, () -> userService.findByProjects_Id(1L));
        verify(userRepository).findByProjects_Id(Mockito.<Long>any());
    }

    /**
     * Method under test: {@link UserService#loadUserByUsername(String)}
     */
    @Test
    void testLoadUserByUsername() throws UnsupportedEncodingException, UsernameNotFoundException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(ofResult);
        assertSame(user, userService.loadUserByUsername("janedoe"));
        verify(userRepository).findUserByUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#loadUserByUsername(String)}
     */
    @Test
    void testLoadUserByUsername2() throws UsernameNotFoundException {
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("janedoe"));
        verify(userRepository).findUserByUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#loadUserByUsername(String)}
     */
    @Test
    void testLoadUserByUsername3() throws UsernameNotFoundException {
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenThrow(new UsernameNotFoundException("Msg"));
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("janedoe"));
        verify(userRepository).findUserByUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#mapUserToInfoDTO(User)}
     */
    @Test
    void testMapUserToInfoDTO() throws UnsupportedEncodingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        UserInfoDTO actualMapUserToInfoDTOResult = userService.mapUserToInfoDTO(user);
        assertSame(avatar, actualMapUserToInfoDTOResult.getAvatar());
        assertEquals("janedoe", actualMapUserToInfoDTOResult.getUsername());
        assertEquals("jane.doe@example.org", actualMapUserToInfoDTOResult.getEmail());
        assertEquals("Name", actualMapUserToInfoDTOResult.getName());
        assertTrue(actualMapUserToInfoDTOResult.getRoles().isEmpty());
        assertEquals(1L, actualMapUserToInfoDTOResult.getId());
        assertEquals("Doe", actualMapUserToInfoDTOResult.getSurname());
    }

    /**
     * Method under test: {@link UserService#mapUserToInfoDTO(User)}
     */
    @Test
    void testMapUserToInfoDTO2() throws UnsupportedEncodingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        Media media = new Media();
        media.setBytes("AXAXAXAX".getBytes("UTF-8"));
        media.setId(1L);
        media.setMediaType("Media Type");
        media.setOriginalFileName("foo.txt");
        media.setSize(3L);
        User user = mock(User.class);
        when(user.getAvatar()).thenReturn(media);
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn("jane.doe@example.org");
        when(user.getName()).thenReturn("Name");
        when(user.getSurname()).thenReturn("Doe");
        when(user.getUsername()).thenReturn("janedoe");
        Mockito.<Collection<? extends GrantedAuthority>>when(user.getAuthorities()).thenReturn(new ArrayList<>());
        doNothing().when(user).setActive(anyBoolean());
        doNothing().when(user).setAuthorities(Mockito.<Set<Role>>any());
        doNothing().when(user).setAvatar(Mockito.<Media>any());
        doNothing().when(user).setEmail(Mockito.<String>any());
        doNothing().when(user).setId(Mockito.<Long>any());
        doNothing().when(user).setName(Mockito.<String>any());
        doNothing().when(user).setPassword(Mockito.<String>any());
        doNothing().when(user).setPasswordConfirm(Mockito.<String>any());
        doNothing().when(user).setProjects(Mockito.<Set<Project>>any());
        doNothing().when(user).setSurname(Mockito.<String>any());
        doNothing().when(user).setUsername(Mockito.<String>any());
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        UserInfoDTO actualMapUserToInfoDTOResult = userService.mapUserToInfoDTO(user);
        assertSame(media, actualMapUserToInfoDTOResult.getAvatar());
        assertEquals("janedoe", actualMapUserToInfoDTOResult.getUsername());
        assertEquals("jane.doe@example.org", actualMapUserToInfoDTOResult.getEmail());
        assertEquals("Name", actualMapUserToInfoDTOResult.getName());
        assertTrue(actualMapUserToInfoDTOResult.getRoles().isEmpty());
        assertEquals(1L, actualMapUserToInfoDTOResult.getId());
        assertEquals("Doe", actualMapUserToInfoDTOResult.getSurname());
        verify(user).getAvatar();
        verify(user).getId();
        verify(user).getEmail();
        verify(user).getName();
        verify(user).getSurname();
        verify(user).getUsername();
        verify(user).getAuthorities();
        verify(user).setActive(anyBoolean());
        verify(user).setAuthorities(Mockito.<Set<Role>>any());
        verify(user).setAvatar(Mockito.<Media>any());
        verify(user).setEmail(Mockito.<String>any());
        verify(user).setId(Mockito.<Long>any());
        verify(user).setName(Mockito.<String>any());
        verify(user).setPassword(Mockito.<String>any());
        verify(user).setPasswordConfirm(Mockito.<String>any());
        verify(user).setProjects(Mockito.<Set<Project>>any());
        verify(user).setSurname(Mockito.<String>any());
        verify(user).setUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#validateRegister(RegisterRequestDTO, BindingResult)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testValidateRegister() throws MessagingException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.example.monicio.DTO.RegisterRequestDTO.getPassword()" because "registerRequestDTO" is null
        //       at com.example.monicio.Services.UserService.validateRegister(UserService.java:179)
        //   See https://diff.blue/R013 to resolve this issue.

        userService.validateRegister(null, new BindException("Target", "Object Name"));
    }

    /**
     * Method under test: {@link UserService#validateRegister(RegisterRequestDTO, BindingResult)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testValidateRegister2() throws MessagingException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.example.monicio.DTO.RegisterRequestDTO.getPassword()" because "registerRequestDTO" is null
        //       at com.example.monicio.Services.UserService.validateRegister(UserService.java:179)
        //   See https://diff.blue/R013 to resolve this issue.

        userService.validateRegister(null, mock(BeanPropertyBindingResult.class));
    }

    /**
     * Method under test: {@link UserService#validateRegister(RegisterRequestDTO, BindingResult)}
     */
    @Test
    void testValidateRegister3() throws MessagingException {
        when(userRepository.existsByEmail(Mockito.<String>any())).thenReturn(true);
        when(userRepository.existsByUsername(Mockito.<String>any())).thenReturn(true);
        RegisterRequestDTO registerRequestDTO = mock(RegisterRequestDTO.class);
        when(registerRequestDTO.getEmail()).thenReturn("jane.doe@example.org");
        when(registerRequestDTO.getUsername()).thenReturn("janedoe");
        when(registerRequestDTO.getPassword()).thenReturn("iloveyou");
        when(registerRequestDTO.getPasswordConfirm()).thenReturn("Password Confirm");
        BindException bindingResult = new BindException("Target", "Object Name");

        ResponseEntity<?> actualValidateRegisterResult = userService.validateRegister(registerRequestDTO, bindingResult);
        assertTrue(actualValidateRegisterResult.hasBody());
        assertEquals(HttpStatus.CONFLICT, actualValidateRegisterResult.getStatusCode());
        assertTrue(actualValidateRegisterResult.getHeaders().isEmpty());
        verify(userRepository).existsByEmail(Mockito.<String>any());
        verify(userRepository).existsByUsername(Mockito.<String>any());
        verify(registerRequestDTO).getEmail();
        verify(registerRequestDTO).getPassword();
        verify(registerRequestDTO).getPasswordConfirm();
        verify(registerRequestDTO).getUsername();
        assertEquals("org.springframework.validation.BeanPropertyBindingResult: 3 errors\n"
                + "Field error in object 'user' on field 'passwordConfirm': rejected value [null]; codes []; arguments"
                + " []; default message [Пароли не совпадают]\n"
                + "Field error in object 'user' on field 'username': rejected value [null]; codes []; arguments []; default"
                + " message [Пользователь с таким никнеймом уже существует]\n"
                + "Field error in object 'user' on field 'email': rejected value [null]; codes []; arguments []; default"
                + " message [Пользователь с такой почтой уже существует]", bindingResult.getLocalizedMessage());
        assertTrue(bindingResult.getBindingResult().hasErrors());
    }

    /**
     * Method under test: {@link UserService#validateRegister(RegisterRequestDTO, BindingResult)}
     */
    @Test
    void testValidateRegister4() throws MessagingException {
        when(userRepository.existsByEmail(Mockito.<String>any())).thenReturn(true);
        when(userRepository.existsByUsername(Mockito.<String>any())).thenReturn(true);
        RegisterRequestDTO registerRequestDTO = mock(RegisterRequestDTO.class);
        when(registerRequestDTO.getEmail()).thenThrow(new UsernameNotFoundException("iloveyou"));
        when(registerRequestDTO.getUsername()).thenThrow(new UsernameNotFoundException("iloveyou"));
        when(registerRequestDTO.getPassword()).thenReturn("iloveyou");
        when(registerRequestDTO.getPasswordConfirm()).thenReturn("Password Confirm");
        BindException bindingResult = new BindException("Target", "Object Name");

        userService.validateRegister(registerRequestDTO, bindingResult);
        verify(registerRequestDTO).getPassword();
        verify(registerRequestDTO).getPasswordConfirm();
        verify(registerRequestDTO).getUsername();
        assertEquals("org.springframework.validation.BeanPropertyBindingResult: 1 errors\n"
                + "Field error in object 'user' on field 'passwordConfirm': rejected value [null]; codes []; arguments"
                + " []; default message [Пароли не совпадают]", bindingResult.getLocalizedMessage());
        assertNull(bindingResult.getBindingResult().getGlobalError());
    }

    /**
     * Method under test: {@link UserService#validateRegister(RegisterRequestDTO, BindingResult)}
     */
    @Test
    void testValidateRegister5() throws MessagingException {
        when(userRepository.existsByEmail(Mockito.<String>any())).thenReturn(false);
        when(userRepository.existsByUsername(Mockito.<String>any())).thenReturn(true);
        RegisterRequestDTO registerRequestDTO = mock(RegisterRequestDTO.class);
        when(registerRequestDTO.getEmail()).thenReturn("jane.doe@example.org");
        when(registerRequestDTO.getUsername()).thenReturn("janedoe");
        when(registerRequestDTO.getPassword()).thenReturn("iloveyou");
        when(registerRequestDTO.getPasswordConfirm()).thenReturn("Password Confirm");
        BindException bindingResult = new BindException("Target", "Object Name");

        ResponseEntity<?> actualValidateRegisterResult = userService.validateRegister(registerRequestDTO, bindingResult);
        assertTrue(actualValidateRegisterResult.hasBody());
        assertEquals(HttpStatus.CONFLICT, actualValidateRegisterResult.getStatusCode());
        assertTrue(actualValidateRegisterResult.getHeaders().isEmpty());
        verify(userRepository).existsByEmail(Mockito.<String>any());
        verify(userRepository).existsByUsername(Mockito.<String>any());
        verify(registerRequestDTO).getEmail();
        verify(registerRequestDTO).getPassword();
        verify(registerRequestDTO).getPasswordConfirm();
        verify(registerRequestDTO).getUsername();
        assertEquals("org.springframework.validation.BeanPropertyBindingResult: 2 errors\n"
                + "Field error in object 'user' on field 'passwordConfirm': rejected value [null]; codes []; arguments"
                + " []; default message [Пароли не совпадают]\n"
                + "Field error in object 'user' on field 'username': rejected value [null]; codes []; arguments []; default"
                + " message [Пользователь с таким никнеймом уже существует]", bindingResult.getLocalizedMessage());
        assertTrue(bindingResult.getBindingResult().hasErrors());
    }

    /**
     * Method under test: {@link UserService#validateRegister(RegisterRequestDTO, BindingResult)}
     */
    @Test
    void testValidateRegister6() throws MessagingException {
        when(userRepository.existsByEmail(Mockito.<String>any())).thenReturn(true);
        when(userRepository.existsByUsername(Mockito.<String>any())).thenReturn(false);
        RegisterRequestDTO registerRequestDTO = mock(RegisterRequestDTO.class);
        when(registerRequestDTO.getEmail()).thenReturn("jane.doe@example.org");
        when(registerRequestDTO.getUsername()).thenReturn("janedoe");
        when(registerRequestDTO.getPassword()).thenReturn("iloveyou");
        when(registerRequestDTO.getPasswordConfirm()).thenReturn("Password Confirm");
        BindException bindingResult = new BindException("Target", "Object Name");

        ResponseEntity<?> actualValidateRegisterResult = userService.validateRegister(registerRequestDTO, bindingResult);
        assertTrue(actualValidateRegisterResult.hasBody());
        assertEquals(HttpStatus.CONFLICT, actualValidateRegisterResult.getStatusCode());
        assertTrue(actualValidateRegisterResult.getHeaders().isEmpty());
        verify(userRepository).existsByEmail(Mockito.<String>any());
        verify(userRepository).existsByUsername(Mockito.<String>any());
        verify(registerRequestDTO).getEmail();
        verify(registerRequestDTO).getPassword();
        verify(registerRequestDTO).getPasswordConfirm();
        verify(registerRequestDTO).getUsername();
        assertEquals("org.springframework.validation.BeanPropertyBindingResult: 2 errors\n"
                + "Field error in object 'user' on field 'passwordConfirm': rejected value [null]; codes []; arguments"
                + " []; default message [Пароли не совпадают]\n"
                + "Field error in object 'user' on field 'email': rejected value [null]; codes []; arguments []; default"
                + " message [Пользователь с такой почтой уже существует]", bindingResult.getLocalizedMessage());
        assertTrue(bindingResult.getBindingResult().hasErrors());
    }

    /**
     * Method under test: {@link UserService#validateRegister(RegisterRequestDTO, BindingResult)}
     */
    @Test
    void testValidateRegister7() throws MessagingException {
        when(userRepository.existsByEmail(Mockito.<String>any())).thenReturn(true);
        when(userRepository.existsByUsername(Mockito.<String>any())).thenReturn(true);
        RegisterRequestDTO registerRequestDTO = mock(RegisterRequestDTO.class);
        when(registerRequestDTO.getEmail()).thenReturn("jane.doe@example.org");
        when(registerRequestDTO.getUsername()).thenReturn("janedoe");
        when(registerRequestDTO.getPassword()).thenReturn("Password Confirm");
        when(registerRequestDTO.getPasswordConfirm()).thenReturn("Password Confirm");
        BindException bindingResult = new BindException("Target", "Object Name");

        ResponseEntity<?> actualValidateRegisterResult = userService.validateRegister(registerRequestDTO, bindingResult);
        assertTrue(actualValidateRegisterResult.hasBody());
        assertEquals(HttpStatus.CONFLICT, actualValidateRegisterResult.getStatusCode());
        assertTrue(actualValidateRegisterResult.getHeaders().isEmpty());
        verify(userRepository).existsByEmail(Mockito.<String>any());
        verify(userRepository).existsByUsername(Mockito.<String>any());
        verify(registerRequestDTO).getEmail();
        verify(registerRequestDTO).getPassword();
        verify(registerRequestDTO).getPasswordConfirm();
        verify(registerRequestDTO).getUsername();
        assertEquals("org.springframework.validation.BeanPropertyBindingResult: 2 errors\n"
                + "Field error in object 'user' on field 'username': rejected value [null]; codes []; arguments []; default"
                + " message [Пользователь с таким никнеймом уже существует]\n"
                + "Field error in object 'user' on field 'email': rejected value [null]; codes []; arguments []; default"
                + " message [Пользователь с такой почтой уже существует]", bindingResult.getLocalizedMessage());
        assertTrue(bindingResult.getBindingResult().hasErrors());
    }

    /**
     * Method under test: {@link UserService#validateRegister(RegisterRequestDTO, BindingResult)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testValidateRegister8() throws MessagingException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "org.springframework.validation.BindingResult.addError(org.springframework.validation.ObjectError)" because "bindingResult" is null
        //       at com.example.monicio.Services.UserService.validateRegister(UserService.java:180)
        //   See https://diff.blue/R013 to resolve this issue.

        when(userRepository.existsByEmail(Mockito.<String>any())).thenReturn(true);
        when(userRepository.existsByUsername(Mockito.<String>any())).thenReturn(true);
        RegisterRequestDTO registerRequestDTO = mock(RegisterRequestDTO.class);
        when(registerRequestDTO.getEmail()).thenReturn("jane.doe@example.org");
        when(registerRequestDTO.getUsername()).thenReturn("janedoe");
        when(registerRequestDTO.getPassword()).thenReturn("iloveyou");
        when(registerRequestDTO.getPasswordConfirm()).thenReturn("Password Confirm");
        userService.validateRegister(registerRequestDTO, null);
    }

    /**
     * Method under test: {@link UserService#validateRegister(RegisterRequestDTO, BindingResult)}
     */
    @Test
    void testValidateRegister9() throws UnsupportedEncodingException, MessagingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");

        ActivationToken activationToken = new ActivationToken();
        activationToken
                .setExpiryDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        activationToken.setId(1L);
        activationToken.setToken("ABC123");
        activationToken.setUser(user);
        when(activationTokenRepository.save(Mockito.<ActivationToken>any())).thenReturn(activationToken);
        doNothing().when(emailService).sendSimpleMessage(Mockito.<String>any(), Mockito.<String>any());

        Media avatar2 = new Media();
        avatar2.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar2.setId(1L);
        avatar2.setMediaType("Media Type");
        avatar2.setOriginalFileName("foo.txt");
        avatar2.setSize(3L);

        User user2 = new User();
        user2.setActive(true);
        user2.setAuthorities(new HashSet<>());
        user2.setAvatar(avatar2);
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setName("Name");
        user2.setPassword("iloveyou");
        user2.setPasswordConfirm("Password Confirm");
        user2.setProjects(new HashSet<>());
        user2.setSurname("Doe");
        user2.setUsername("janedoe");
        when(userRepository.save(Mockito.<User>any())).thenReturn(user2);
        when(userRepository.existsByEmail(Mockito.<String>any())).thenReturn(true);
        when(userRepository.existsByUsername(Mockito.<String>any())).thenReturn(true);
        when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");
        RegisterRequestDTO registerRequestDTO = mock(RegisterRequestDTO.class);
        when(registerRequestDTO.getRole()).thenReturn(Role.USER);
        when(registerRequestDTO.getName()).thenReturn("Name");
        when(registerRequestDTO.getSurname()).thenReturn("Doe");
        when(registerRequestDTO.getEmail()).thenReturn("jane.doe@example.org");
        when(registerRequestDTO.getUsername()).thenReturn("janedoe");
        when(registerRequestDTO.getPassword()).thenReturn("iloveyou");
        when(registerRequestDTO.getPasswordConfirm()).thenReturn("Password Confirm");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenThrow(new UsernameNotFoundException("Msg"));
        when(bindingResult.hasErrors()).thenReturn(true);
        doNothing().when(bindingResult).addError(Mockito.<ObjectError>any());
        assertThrows(UsernameNotFoundException.class,
                () -> userService.validateRegister(registerRequestDTO, bindingResult));
        verify(userRepository).existsByEmail(Mockito.<String>any());
        verify(userRepository).existsByUsername(Mockito.<String>any());
        verify(registerRequestDTO).getEmail();
        verify(registerRequestDTO).getPassword();
        verify(registerRequestDTO).getPasswordConfirm();
        verify(registerRequestDTO).getUsername();
        verify(bindingResult).hasErrors();
        verify(bindingResult).getFieldErrors();
        verify(bindingResult, atLeast(1)).addError(Mockito.<ObjectError>any());
    }

    /**
     * Method under test: {@link UserService#getUserAuthentication(Authentication)}
     */
    @Test
    void testGetUserAuthentication() throws UnsupportedEncodingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(ofResult);
        assertSame(user, userService.getUserAuthentication(new TestingAuthenticationToken("Principal", "Credentials")));
        verify(userRepository).findUserByUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#getUserAuthentication(Authentication)}
     */
    @Test
    void testGetUserAuthentication2() throws UnsupportedEncodingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(ofResult);

        Media avatar2 = new Media();
        avatar2.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar2.setId(1L);
        avatar2.setMediaType("Media Type");
        avatar2.setOriginalFileName("foo.txt");
        avatar2.setSize(3L);

        User user2 = new User();
        user2.setActive(true);
        user2.setAuthorities(new HashSet<>());
        user2.setAvatar(avatar2);
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setName("Name");
        user2.setPassword("iloveyou");
        user2.setPasswordConfirm("Password Confirm");
        user2.setProjects(new HashSet<>());
        user2.setSurname("Doe");
        user2.setUsername("janedoe");
        assertSame(user, userService.getUserAuthentication(new TestingAuthenticationToken(user2, "Credentials")));
        verify(userRepository).findUserByUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#getUserAuthentication(Authentication)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetUserAuthentication3() throws UnsupportedEncodingException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   org.springframework.security.core.userdetails.UsernameNotFoundException: Msg
        //       at com.example.monicio.Services.UserService.getUserAuthentication(UserService.java:202)
        //   See https://diff.blue/R013 to resolve this issue.

        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(ofResult);
        User user2 = mock(User.class);
        when(user2.getUsername()).thenThrow(new UsernameNotFoundException("Msg"));
        userService.getUserAuthentication(new TestingAuthenticationToken(user2, "Credentials"));
    }

    /**
     * Method under test: {@link UserService#registerUser(RegisterRequestDTO)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testRegisterUser() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.example.monicio.DTO.RegisterRequestDTO.getUsername()" because "registerRequestDTO" is null
        //       at com.example.monicio.Services.UserService.registerUser(UserService.java:212)
        //   See https://diff.blue/R013 to resolve this issue.

        userService.registerUser(null);
    }

    /**
     * Method under test: {@link UserService#registerUser(RegisterRequestDTO)}
     */
    @Test
    void testRegisterUser2() throws UnsupportedEncodingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        when(userRepository.save(Mockito.<User>any())).thenReturn(user);
        when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");
        RegisterRequestDTO registerRequestDTO = mock(RegisterRequestDTO.class);
        when(registerRequestDTO.getRole()).thenReturn(Role.USER);
        when(registerRequestDTO.getEmail()).thenReturn("jane.doe@example.org");
        when(registerRequestDTO.getName()).thenReturn("Name");
        when(registerRequestDTO.getPassword()).thenReturn("iloveyou");
        when(registerRequestDTO.getSurname()).thenReturn("Doe");
        when(registerRequestDTO.getUsername()).thenReturn("janedoe");
        assertSame(user, userService.registerUser(registerRequestDTO));
        verify(userRepository).save(Mockito.<User>any());
        verify(passwordEncoder).encode(Mockito.<CharSequence>any());
        verify(registerRequestDTO).getRole();
        verify(registerRequestDTO).getEmail();
        verify(registerRequestDTO).getName();
        verify(registerRequestDTO).getPassword();
        verify(registerRequestDTO).getSurname();
        verify(registerRequestDTO).getUsername();
    }

    /**
     * Method under test: {@link UserService#loginUser(LoginRequestDTO)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testLoginUser() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.example.monicio.DTO.LoginRequestDTO.getUsername()" because "loginRequestDTO" is null
        //       at com.example.monicio.Services.UserService.loginUser(UserService.java:231)
        //   See https://diff.blue/R013 to resolve this issue.

        userService.loginUser(null);
    }

    /**
     * Method under test: {@link UserService#loginUser(LoginRequestDTO)}
     */
    @Test
    void testLoginUser2() throws UnsupportedEncodingException, AuthenticationException {
        when(jWTUtil.generateToken(Mockito.<String>any())).thenReturn("ABC123");

        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(ofResult);
        when(authenticationManager.authenticate(Mockito.<Authentication>any()))
                .thenReturn(new TestingAuthenticationToken("Principal", "Credentials"));
        LoginRequestDTO loginRequestDTO = mock(LoginRequestDTO.class);
        when(loginRequestDTO.getPassword()).thenReturn("iloveyou");
        when(loginRequestDTO.getUsername()).thenReturn("janedoe");
        ResponseEntity<?> actualLoginUserResult = userService.loginUser(loginRequestDTO);
        assertTrue(actualLoginUserResult.hasBody());
        assertTrue(actualLoginUserResult.getHeaders().isEmpty());
        assertEquals(HttpStatus.OK, actualLoginUserResult.getStatusCode());
        assertEquals("ABC123", ((LoginResponseDTO) actualLoginUserResult.getBody()).getJwt());
        UserInfoDTO userInfoDTO = ((LoginResponseDTO) actualLoginUserResult.getBody()).getUserInfoDTO();
        assertSame(avatar, userInfoDTO.getAvatar());
        assertEquals("janedoe", userInfoDTO.getUsername());
        assertEquals("jane.doe@example.org", userInfoDTO.getEmail());
        assertEquals("Name", userInfoDTO.getName());
        assertTrue(userInfoDTO.getRoles().isEmpty());
        assertEquals(1L, userInfoDTO.getId());
        assertEquals("Doe", userInfoDTO.getSurname());
        verify(jWTUtil).generateToken(Mockito.<String>any());
        verify(userRepository).findUserByUsername(Mockito.<String>any());
        verify(authenticationManager).authenticate(Mockito.<Authentication>any());
        verify(loginRequestDTO).getPassword();
        verify(loginRequestDTO, atLeast(1)).getUsername();
    }

    /**
     * Method under test: {@link UserService#loginUser(LoginRequestDTO)}
     */
    @Test
    void testLoginUser3() throws UnsupportedEncodingException, AuthenticationException {
        when(jWTUtil.generateToken(Mockito.<String>any())).thenReturn("ABC123");

        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        Media media = new Media();
        media.setBytes("AXAXAXAX".getBytes("UTF-8"));
        media.setId(1L);
        media.setMediaType("Media Type");
        media.setOriginalFileName("foo.txt");
        media.setSize(3L);
        User user = mock(User.class);
        when(user.getAvatar()).thenReturn(media);
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn("jane.doe@example.org");
        when(user.getName()).thenReturn("Name");
        when(user.getSurname()).thenReturn("Doe");
        when(user.getUsername()).thenReturn("janedoe");
        Mockito.<Collection<? extends GrantedAuthority>>when(user.getAuthorities()).thenReturn(new ArrayList<>());
        doNothing().when(user).setActive(anyBoolean());
        doNothing().when(user).setAuthorities(Mockito.<Set<Role>>any());
        doNothing().when(user).setAvatar(Mockito.<Media>any());
        doNothing().when(user).setEmail(Mockito.<String>any());
        doNothing().when(user).setId(Mockito.<Long>any());
        doNothing().when(user).setName(Mockito.<String>any());
        doNothing().when(user).setPassword(Mockito.<String>any());
        doNothing().when(user).setPasswordConfirm(Mockito.<String>any());
        doNothing().when(user).setProjects(Mockito.<Set<Project>>any());
        doNothing().when(user).setSurname(Mockito.<String>any());
        doNothing().when(user).setUsername(Mockito.<String>any());
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(ofResult);
        when(authenticationManager.authenticate(Mockito.<Authentication>any()))
                .thenReturn(new TestingAuthenticationToken("Principal", "Credentials"));
        LoginRequestDTO loginRequestDTO = mock(LoginRequestDTO.class);
        when(loginRequestDTO.getPassword()).thenReturn("iloveyou");
        when(loginRequestDTO.getUsername()).thenReturn("janedoe");
        ResponseEntity<?> actualLoginUserResult = userService.loginUser(loginRequestDTO);
        assertTrue(actualLoginUserResult.hasBody());
        assertTrue(actualLoginUserResult.getHeaders().isEmpty());
        assertEquals(HttpStatus.OK, actualLoginUserResult.getStatusCode());
        assertEquals("ABC123", ((LoginResponseDTO) actualLoginUserResult.getBody()).getJwt());
        UserInfoDTO userInfoDTO = ((LoginResponseDTO) actualLoginUserResult.getBody()).getUserInfoDTO();
        assertSame(media, userInfoDTO.getAvatar());
        assertEquals("janedoe", userInfoDTO.getUsername());
        assertEquals("jane.doe@example.org", userInfoDTO.getEmail());
        assertEquals("Name", userInfoDTO.getName());
        assertTrue(userInfoDTO.getRoles().isEmpty());
        assertEquals(1L, userInfoDTO.getId());
        assertEquals("Doe", userInfoDTO.getSurname());
        verify(jWTUtil).generateToken(Mockito.<String>any());
        verify(userRepository).findUserByUsername(Mockito.<String>any());
        verify(user).getAvatar();
        verify(user).getId();
        verify(user).getEmail();
        verify(user).getName();
        verify(user).getSurname();
        verify(user, atLeast(1)).getUsername();
        verify(user).getAuthorities();
        verify(user).setActive(anyBoolean());
        verify(user).setAuthorities(Mockito.<Set<Role>>any());
        verify(user).setAvatar(Mockito.<Media>any());
        verify(user).setEmail(Mockito.<String>any());
        verify(user).setId(Mockito.<Long>any());
        verify(user).setName(Mockito.<String>any());
        verify(user).setPassword(Mockito.<String>any());
        verify(user).setPasswordConfirm(Mockito.<String>any());
        verify(user).setProjects(Mockito.<Set<Project>>any());
        verify(user).setSurname(Mockito.<String>any());
        verify(user).setUsername(Mockito.<String>any());
        verify(authenticationManager).authenticate(Mockito.<Authentication>any());
        verify(loginRequestDTO).getPassword();
        verify(loginRequestDTO, atLeast(1)).getUsername();
    }

    /**
     * Method under test: {@link UserService#collectUserData(Authentication)}
     */
    @Test
    void testCollectUserData() throws UnsupportedEncodingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(ofResult);
        ResponseEntity<?> actualCollectUserDataResult = userService
                .collectUserData(new TestingAuthenticationToken("Principal", "Credentials"));
        assertTrue(actualCollectUserDataResult.hasBody());
        assertTrue(actualCollectUserDataResult.getHeaders().isEmpty());
        assertEquals(HttpStatus.OK, actualCollectUserDataResult.getStatusCode());
        assertTrue(((UserInfoDTO) actualCollectUserDataResult.getBody()).getRoles().isEmpty());
        assertEquals("Name", ((UserInfoDTO) actualCollectUserDataResult.getBody()).getName());
        assertEquals(1L, ((UserInfoDTO) actualCollectUserDataResult.getBody()).getId());
        assertEquals("jane.doe@example.org", ((UserInfoDTO) actualCollectUserDataResult.getBody()).getEmail());
        assertSame(avatar, ((UserInfoDTO) actualCollectUserDataResult.getBody()).getAvatar());
        assertEquals("Doe", ((UserInfoDTO) actualCollectUserDataResult.getBody()).getSurname());
        assertEquals("janedoe", ((UserInfoDTO) actualCollectUserDataResult.getBody()).getUsername());
        verify(userRepository).findUserByUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#collectUserData(Authentication)}
     */
    @Test
    void testCollectUserData2() throws UnsupportedEncodingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        Media media = new Media();
        media.setBytes("AXAXAXAX".getBytes("UTF-8"));
        media.setId(1L);
        media.setMediaType("Media Type");
        media.setOriginalFileName("foo.txt");
        media.setSize(3L);
        User user = mock(User.class);
        when(user.getAvatar()).thenReturn(media);
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn("jane.doe@example.org");
        when(user.getName()).thenReturn("Name");
        when(user.getSurname()).thenReturn("Doe");
        when(user.getUsername()).thenReturn("janedoe");
        Mockito.<Collection<? extends GrantedAuthority>>when(user.getAuthorities()).thenReturn(new ArrayList<>());
        doNothing().when(user).setActive(anyBoolean());
        doNothing().when(user).setAuthorities(Mockito.<Set<Role>>any());
        doNothing().when(user).setAvatar(Mockito.<Media>any());
        doNothing().when(user).setEmail(Mockito.<String>any());
        doNothing().when(user).setId(Mockito.<Long>any());
        doNothing().when(user).setName(Mockito.<String>any());
        doNothing().when(user).setPassword(Mockito.<String>any());
        doNothing().when(user).setPasswordConfirm(Mockito.<String>any());
        doNothing().when(user).setProjects(Mockito.<Set<Project>>any());
        doNothing().when(user).setSurname(Mockito.<String>any());
        doNothing().when(user).setUsername(Mockito.<String>any());
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(ofResult);
        ResponseEntity<?> actualCollectUserDataResult = userService
                .collectUserData(new TestingAuthenticationToken("Principal", "Credentials"));
        assertTrue(actualCollectUserDataResult.hasBody());
        assertTrue(actualCollectUserDataResult.getHeaders().isEmpty());
        assertEquals(HttpStatus.OK, actualCollectUserDataResult.getStatusCode());
        assertTrue(((UserInfoDTO) actualCollectUserDataResult.getBody()).getRoles().isEmpty());
        assertEquals("Name", ((UserInfoDTO) actualCollectUserDataResult.getBody()).getName());
        assertEquals(1L, ((UserInfoDTO) actualCollectUserDataResult.getBody()).getId());
        assertEquals("jane.doe@example.org", ((UserInfoDTO) actualCollectUserDataResult.getBody()).getEmail());
        assertSame(media, ((UserInfoDTO) actualCollectUserDataResult.getBody()).getAvatar());
        assertEquals("Doe", ((UserInfoDTO) actualCollectUserDataResult.getBody()).getSurname());
        assertEquals("janedoe", ((UserInfoDTO) actualCollectUserDataResult.getBody()).getUsername());
        verify(userRepository).findUserByUsername(Mockito.<String>any());
        verify(user).getAvatar();
        verify(user).getId();
        verify(user).getEmail();
        verify(user).getName();
        verify(user).getSurname();
        verify(user).getUsername();
        verify(user).getAuthorities();
        verify(user).setActive(anyBoolean());
        verify(user).setAuthorities(Mockito.<Set<Role>>any());
        verify(user).setAvatar(Mockito.<Media>any());
        verify(user).setEmail(Mockito.<String>any());
        verify(user).setId(Mockito.<Long>any());
        verify(user).setName(Mockito.<String>any());
        verify(user).setPassword(Mockito.<String>any());
        verify(user).setPasswordConfirm(Mockito.<String>any());
        verify(user).setProjects(Mockito.<Set<Project>>any());
        verify(user).setSurname(Mockito.<String>any());
        verify(user).setUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#collectUserData(Authentication)}
     */
    @Test
    void testCollectUserData3() {
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class,
                () -> userService.collectUserData(new TestingAuthenticationToken("Principal", "Credentials")));
        verify(userRepository).findUserByUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#collectUserData(Authentication)}
     */
    @Test
    void testCollectUserData4() throws UnsupportedEncodingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        Media media = new Media();
        media.setBytes("AXAXAXAX".getBytes("UTF-8"));
        media.setId(1L);
        media.setMediaType("Media Type");
        media.setOriginalFileName("foo.txt");
        media.setSize(3L);
        User user = mock(User.class);
        when(user.getAvatar()).thenReturn(media);
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn("jane.doe@example.org");
        when(user.getName()).thenReturn("Name");
        when(user.getSurname()).thenReturn("Doe");
        when(user.getUsername()).thenReturn("janedoe");
        Mockito.<Collection<? extends GrantedAuthority>>when(user.getAuthorities()).thenReturn(new ArrayList<>());
        doNothing().when(user).setActive(anyBoolean());
        doNothing().when(user).setAuthorities(Mockito.<Set<Role>>any());
        doNothing().when(user).setAvatar(Mockito.<Media>any());
        doNothing().when(user).setEmail(Mockito.<String>any());
        doNothing().when(user).setId(Mockito.<Long>any());
        doNothing().when(user).setName(Mockito.<String>any());
        doNothing().when(user).setPassword(Mockito.<String>any());
        doNothing().when(user).setPasswordConfirm(Mockito.<String>any());
        doNothing().when(user).setProjects(Mockito.<Set<Project>>any());
        doNothing().when(user).setSurname(Mockito.<String>any());
        doNothing().when(user).setUsername(Mockito.<String>any());
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(ofResult);

        Media avatar2 = new Media();
        avatar2.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar2.setId(1L);
        avatar2.setMediaType("Media Type");
        avatar2.setOriginalFileName("foo.txt");
        avatar2.setSize(3L);

        User user2 = new User();
        user2.setActive(true);
        user2.setAuthorities(new HashSet<>());
        user2.setAvatar(avatar2);
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setName("Name");
        user2.setPassword("iloveyou");
        user2.setPasswordConfirm("Password Confirm");
        user2.setProjects(new HashSet<>());
        user2.setSurname("Doe");
        user2.setUsername("janedoe");
        ResponseEntity<?> actualCollectUserDataResult = userService
                .collectUserData(new TestingAuthenticationToken(user2, "Credentials"));
        assertTrue(actualCollectUserDataResult.hasBody());
        assertTrue(actualCollectUserDataResult.getHeaders().isEmpty());
        assertEquals(HttpStatus.OK, actualCollectUserDataResult.getStatusCode());
        assertTrue(((UserInfoDTO) actualCollectUserDataResult.getBody()).getRoles().isEmpty());
        assertEquals("Name", ((UserInfoDTO) actualCollectUserDataResult.getBody()).getName());
        assertEquals(1L, ((UserInfoDTO) actualCollectUserDataResult.getBody()).getId());
        assertEquals("jane.doe@example.org", ((UserInfoDTO) actualCollectUserDataResult.getBody()).getEmail());
        assertSame(media, ((UserInfoDTO) actualCollectUserDataResult.getBody()).getAvatar());
        assertEquals("Doe", ((UserInfoDTO) actualCollectUserDataResult.getBody()).getSurname());
        assertEquals("janedoe", ((UserInfoDTO) actualCollectUserDataResult.getBody()).getUsername());
        verify(userRepository).findUserByUsername(Mockito.<String>any());
        verify(user).getAvatar();
        verify(user).getId();
        verify(user).getEmail();
        verify(user).getName();
        verify(user).getSurname();
        verify(user).getUsername();
        verify(user).getAuthorities();
        verify(user).setActive(anyBoolean());
        verify(user).setAuthorities(Mockito.<Set<Role>>any());
        verify(user).setAvatar(Mockito.<Media>any());
        verify(user).setEmail(Mockito.<String>any());
        verify(user).setId(Mockito.<Long>any());
        verify(user).setName(Mockito.<String>any());
        verify(user).setPassword(Mockito.<String>any());
        verify(user).setPasswordConfirm(Mockito.<String>any());
        verify(user).setProjects(Mockito.<Set<Project>>any());
        verify(user).setSurname(Mockito.<String>any());
        verify(user).setUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#collectUserData(Authentication)}
     */
    @Test
    void testCollectUserData5() throws UnsupportedEncodingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        Media media = new Media();
        media.setBytes("AXAXAXAX".getBytes("UTF-8"));
        media.setId(1L);
        media.setMediaType("Media Type");
        media.setOriginalFileName("foo.txt");
        media.setSize(3L);
        User user = mock(User.class);
        when(user.getAvatar()).thenReturn(media);
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn("jane.doe@example.org");
        when(user.getName()).thenReturn("Name");
        when(user.getSurname()).thenReturn("Doe");
        when(user.getUsername()).thenReturn("janedoe");
        Mockito.<Collection<? extends GrantedAuthority>>when(user.getAuthorities()).thenReturn(new ArrayList<>());
        doNothing().when(user).setActive(anyBoolean());
        doNothing().when(user).setAuthorities(Mockito.<Set<Role>>any());
        doNothing().when(user).setAvatar(Mockito.<Media>any());
        doNothing().when(user).setEmail(Mockito.<String>any());
        doNothing().when(user).setId(Mockito.<Long>any());
        doNothing().when(user).setName(Mockito.<String>any());
        doNothing().when(user).setPassword(Mockito.<String>any());
        doNothing().when(user).setPasswordConfirm(Mockito.<String>any());
        doNothing().when(user).setProjects(Mockito.<Set<Project>>any());
        doNothing().when(user).setSurname(Mockito.<String>any());
        doNothing().when(user).setUsername(Mockito.<String>any());
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(ofResult);

        Media media2 = new Media();
        media2.setBytes("AXAXAXAX".getBytes("UTF-8"));
        media2.setId(1L);
        media2.setMediaType("Media Type");
        media2.setOriginalFileName("foo.txt");
        media2.setSize(3L);
        ResponseEntity<?> actualCollectUserDataResult = userService
                .collectUserData(new TestingAuthenticationToken(media2, "Credentials"));
        assertTrue(actualCollectUserDataResult.hasBody());
        assertTrue(actualCollectUserDataResult.getHeaders().isEmpty());
        assertEquals(HttpStatus.OK, actualCollectUserDataResult.getStatusCode());
        assertTrue(((UserInfoDTO) actualCollectUserDataResult.getBody()).getRoles().isEmpty());
        assertEquals("Name", ((UserInfoDTO) actualCollectUserDataResult.getBody()).getName());
        assertEquals(1L, ((UserInfoDTO) actualCollectUserDataResult.getBody()).getId());
        assertEquals("jane.doe@example.org", ((UserInfoDTO) actualCollectUserDataResult.getBody()).getEmail());
        assertSame(media, ((UserInfoDTO) actualCollectUserDataResult.getBody()).getAvatar());
        assertEquals("Doe", ((UserInfoDTO) actualCollectUserDataResult.getBody()).getSurname());
        assertEquals("janedoe", ((UserInfoDTO) actualCollectUserDataResult.getBody()).getUsername());
        verify(userRepository).findUserByUsername(Mockito.<String>any());
        verify(user).getAvatar();
        verify(user).getId();
        verify(user).getEmail();
        verify(user).getName();
        verify(user).getSurname();
        verify(user).getUsername();
        verify(user).getAuthorities();
        verify(user).setActive(anyBoolean());
        verify(user).setAuthorities(Mockito.<Set<Role>>any());
        verify(user).setAvatar(Mockito.<Media>any());
        verify(user).setEmail(Mockito.<String>any());
        verify(user).setId(Mockito.<Long>any());
        verify(user).setName(Mockito.<String>any());
        verify(user).setPassword(Mockito.<String>any());
        verify(user).setPasswordConfirm(Mockito.<String>any());
        verify(user).setProjects(Mockito.<Set<Project>>any());
        verify(user).setSurname(Mockito.<String>any());
        verify(user).setUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#changeUserPassword(ChangePasswordDto, BindingResult, Authentication)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testChangeUserPassword() throws UnsupportedEncodingException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.example.monicio.DTO.ChangePasswordDto.getPassword()" because "changePasswordDto" is null
        //       at com.example.monicio.Services.UserService.changeUserPassword(UserService.java:263)
        //   See https://diff.blue/R013 to resolve this issue.

        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(ofResult);
        BindException bindingResult = new BindException("Target", "Object Name");

        userService.changeUserPassword(null, bindingResult, new TestingAuthenticationToken("Principal", "Credentials"));
    }

    /**
     * Method under test: {@link UserService#changeUserPassword(ChangePasswordDto, BindingResult, Authentication)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testChangeUserPassword2() throws UnsupportedEncodingException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "org.springframework.security.core.Authentication.getName()" because "authentication" is null
        //       at com.example.monicio.Services.UserService.getUserAuthentication(UserService.java:202)
        //       at com.example.monicio.Services.UserService.changeUserPassword(UserService.java:261)
        //   See https://diff.blue/R013 to resolve this issue.

        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(ofResult);
        userService.changeUserPassword(null, new BindException("Target", "Object Name"), null);
    }

    /**
     * Method under test: {@link UserService#changeUserPassword(ChangePasswordDto, BindingResult, Authentication)}
     */
    @Test
    void testChangeUserPassword3() {
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenThrow(new UsernameNotFoundException("Msg"));
        BindException bindingResult = new BindException("Target", "Object Name");

        assertThrows(UsernameNotFoundException.class, () -> userService.changeUserPassword(null, bindingResult,
                new TestingAuthenticationToken("Principal", "Credentials")));
        verify(userRepository).findUserByUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#changeUserInfo(Authentication, MultipartFile, String)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testChangeUserInfo() throws IOException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.fasterxml.jackson.core.JsonParseException: Unrecognized token 'Change': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')
        //    at [Source: (String)"Change User Info"; line: 1, column: 7]
        //       at com.fasterxml.jackson.core.JsonParser._constructError(JsonParser.java:2391)
        //       at com.fasterxml.jackson.core.base.ParserMinimalBase._reportError(ParserMinimalBase.java:745)
        //       at com.fasterxml.jackson.core.json.ReaderBasedJsonParser._reportInvalidToken(ReaderBasedJsonParser.java:2961)
        //       at com.fasterxml.jackson.core.json.ReaderBasedJsonParser._handleOddValue(ReaderBasedJsonParser.java:2002)
        //       at com.fasterxml.jackson.core.json.ReaderBasedJsonParser.nextToken(ReaderBasedJsonParser.java:802)
        //       at com.fasterxml.jackson.databind.ObjectMapper._initForReading(ObjectMapper.java:4761)
        //       at com.fasterxml.jackson.databind.ObjectMapper._readMapAndClose(ObjectMapper.java:4667)
        //       at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3629)
        //       at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3597)
        //       at com.example.monicio.Services.UserService.changeUserInfo(UserService.java:288)
        //   See https://diff.blue/R013 to resolve this issue.

        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(ofResult);
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("Principal", "Credentials");

        userService.changeUserInfo(authentication,
                new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))), "Change User Info");
    }

    /**
     * Method under test: {@link UserService#changeUserInfo(Authentication, MultipartFile, String)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testChangeUserInfo2() throws IOException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.fasterxml.jackson.core.JsonParseException: Unrecognized token 'Change': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')
        //    at [Source: (String)"Change User Info"; line: 1, column: 7]
        //       at com.fasterxml.jackson.core.JsonParser._constructError(JsonParser.java:2391)
        //       at com.fasterxml.jackson.core.base.ParserMinimalBase._reportError(ParserMinimalBase.java:745)
        //       at com.fasterxml.jackson.core.json.ReaderBasedJsonParser._reportInvalidToken(ReaderBasedJsonParser.java:2961)
        //       at com.fasterxml.jackson.core.json.ReaderBasedJsonParser._handleOddValue(ReaderBasedJsonParser.java:2002)
        //       at com.fasterxml.jackson.core.json.ReaderBasedJsonParser.nextToken(ReaderBasedJsonParser.java:802)
        //       at com.fasterxml.jackson.databind.ObjectMapper._initForReading(ObjectMapper.java:4761)
        //       at com.fasterxml.jackson.databind.ObjectMapper._readMapAndClose(ObjectMapper.java:4667)
        //       at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3629)
        //       at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3597)
        //       at com.example.monicio.Services.UserService.changeUserInfo(UserService.java:288)
        //   See https://diff.blue/R013 to resolve this issue.

        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(ofResult);

        Media avatar2 = new Media();
        avatar2.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar2.setId(1L);
        avatar2.setMediaType("Media Type");
        avatar2.setOriginalFileName("foo.txt");
        avatar2.setSize(3L);

        User user2 = new User();
        user2.setActive(true);
        user2.setAuthorities(new HashSet<>());
        user2.setAvatar(avatar2);
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setName("Name");
        user2.setPassword("iloveyou");
        user2.setPasswordConfirm("Password Confirm");
        user2.setProjects(new HashSet<>());
        user2.setSurname("Doe");
        user2.setUsername("janedoe");
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(user2, "Credentials");

        userService.changeUserInfo(authentication,
                new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))), "Change User Info");
    }

    /**
     * Method under test: {@link UserService#changeUserInfo(Authentication, MultipartFile, String)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testChangeUserInfo3() throws IOException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   org.springframework.security.core.userdetails.UsernameNotFoundException: Msg
        //       at com.example.monicio.Services.UserService.getUserAuthentication(UserService.java:202)
        //       at com.example.monicio.Services.UserService.changeUserInfo(UserService.java:287)
        //   See https://diff.blue/R013 to resolve this issue.

        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findUserByUsername(Mockito.<String>any())).thenReturn(ofResult);
        User user2 = mock(User.class);
        when(user2.getUsername()).thenThrow(new UsernameNotFoundException("Msg"));
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(user2, "Credentials");

        userService.changeUserInfo(authentication,
                new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))), "Change User Info");
    }

    /**
     * Method under test: {@link UserService#activateUser(String)}
     */
    @Test
    void testActivateUser() throws UnsupportedEncodingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");

        ActivationToken activationToken = new ActivationToken();
        activationToken
                .setExpiryDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        activationToken.setId(1L);
        activationToken.setToken("ABC123");
        activationToken.setUser(user);
        doNothing().when(activationTokenRepository).deleteByToken(Mockito.<String>any());
        when(activationTokenRepository.findByToken(Mockito.<String>any())).thenReturn(activationToken);

        Media avatar2 = new Media();
        avatar2.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar2.setId(1L);
        avatar2.setMediaType("Media Type");
        avatar2.setOriginalFileName("foo.txt");
        avatar2.setSize(3L);

        User user2 = new User();
        user2.setActive(true);
        user2.setAuthorities(new HashSet<>());
        user2.setAvatar(avatar2);
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setName("Name");
        user2.setPassword("iloveyou");
        user2.setPasswordConfirm("Password Confirm");
        user2.setProjects(new HashSet<>());
        user2.setSurname("Doe");
        user2.setUsername("janedoe");
        when(userRepository.save(Mockito.<User>any())).thenReturn(user2);
        userService.activateUser("Code");
        verify(activationTokenRepository).findByToken(Mockito.<String>any());
        verify(activationTokenRepository).deleteByToken(Mockito.<String>any());
        verify(userRepository).save(Mockito.<User>any());
    }

    /**
     * Method under test: {@link UserService#activateUser(String)}
     */
    @Test
    void testActivateUser2() throws UnsupportedEncodingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");

        ActivationToken activationToken = new ActivationToken();
        activationToken
                .setExpiryDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        activationToken.setId(1L);
        activationToken.setToken("ABC123");
        activationToken.setUser(user);
        doNothing().when(activationTokenRepository).deleteByToken(Mockito.<String>any());
        when(activationTokenRepository.findByToken(Mockito.<String>any())).thenReturn(activationToken);
        when(userRepository.save(Mockito.<User>any())).thenThrow(new UsernameNotFoundException("Msg"));
        assertThrows(UsernameNotFoundException.class, () -> userService.activateUser("Code"));
        verify(activationTokenRepository).findByToken(Mockito.<String>any());
        verify(activationTokenRepository).deleteByToken(Mockito.<String>any());
        verify(userRepository).save(Mockito.<User>any());
    }

    /**
     * Method under test: {@link UserService#activateUser(String)}
     */
    @Test
    void testActivateUser3() throws UnsupportedEncodingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");

        Media avatar2 = new Media();
        avatar2.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar2.setId(1L);
        avatar2.setMediaType("Media Type");
        avatar2.setOriginalFileName("foo.txt");
        avatar2.setSize(3L);

        User user2 = new User();
        user2.setActive(true);
        user2.setAuthorities(new HashSet<>());
        user2.setAvatar(avatar2);
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setName("Name");
        user2.setPassword("iloveyou");
        user2.setPasswordConfirm("Password Confirm");
        user2.setProjects(new HashSet<>());
        user2.setSurname("Doe");
        user2.setUsername("janedoe");
        ActivationToken activationToken = mock(ActivationToken.class);
        when(activationToken.getUser()).thenReturn(user2);
        doNothing().when(activationToken).setExpiryDate(Mockito.<Date>any());
        doNothing().when(activationToken).setId(Mockito.<Long>any());
        doNothing().when(activationToken).setToken(Mockito.<String>any());
        doNothing().when(activationToken).setUser(Mockito.<User>any());
        activationToken
                .setExpiryDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        activationToken.setId(1L);
        activationToken.setToken("ABC123");
        activationToken.setUser(user);
        doNothing().when(activationTokenRepository).deleteByToken(Mockito.<String>any());
        when(activationTokenRepository.findByToken(Mockito.<String>any())).thenReturn(activationToken);

        Media avatar3 = new Media();
        avatar3.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar3.setId(1L);
        avatar3.setMediaType("Media Type");
        avatar3.setOriginalFileName("foo.txt");
        avatar3.setSize(3L);

        User user3 = new User();
        user3.setActive(true);
        user3.setAuthorities(new HashSet<>());
        user3.setAvatar(avatar3);
        user3.setEmail("jane.doe@example.org");
        user3.setId(1L);
        user3.setName("Name");
        user3.setPassword("iloveyou");
        user3.setPasswordConfirm("Password Confirm");
        user3.setProjects(new HashSet<>());
        user3.setSurname("Doe");
        user3.setUsername("janedoe");
        when(userRepository.save(Mockito.<User>any())).thenReturn(user3);
        userService.activateUser("Code");
        verify(activationTokenRepository).findByToken(Mockito.<String>any());
        verify(activationTokenRepository).deleteByToken(Mockito.<String>any());
        verify(activationToken).getUser();
        verify(activationToken).setExpiryDate(Mockito.<Date>any());
        verify(activationToken).setId(Mockito.<Long>any());
        verify(activationToken).setToken(Mockito.<String>any());
        verify(activationToken).setUser(Mockito.<User>any());
        verify(userRepository).save(Mockito.<User>any());
    }

    /**
     * Method under test: {@link UserService#activateUser(String)}
     */
    @Test
    void testActivateUser4() throws UnsupportedEncodingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");

        Media avatar2 = new Media();
        avatar2.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar2.setId(1L);
        avatar2.setMediaType("Media Type");
        avatar2.setOriginalFileName("foo.txt");
        avatar2.setSize(3L);
        User user2 = mock(User.class);
        doNothing().when(user2).setActive(anyBoolean());
        doNothing().when(user2).setAuthorities(Mockito.<Set<Role>>any());
        doNothing().when(user2).setAvatar(Mockito.<Media>any());
        doNothing().when(user2).setEmail(Mockito.<String>any());
        doNothing().when(user2).setId(Mockito.<Long>any());
        doNothing().when(user2).setName(Mockito.<String>any());
        doNothing().when(user2).setPassword(Mockito.<String>any());
        doNothing().when(user2).setPasswordConfirm(Mockito.<String>any());
        doNothing().when(user2).setProjects(Mockito.<Set<Project>>any());
        doNothing().when(user2).setSurname(Mockito.<String>any());
        doNothing().when(user2).setUsername(Mockito.<String>any());
        user2.setActive(true);
        user2.setAuthorities(new HashSet<>());
        user2.setAvatar(avatar2);
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setName("Name");
        user2.setPassword("iloveyou");
        user2.setPasswordConfirm("Password Confirm");
        user2.setProjects(new HashSet<>());
        user2.setSurname("Doe");
        user2.setUsername("janedoe");
        ActivationToken activationToken = mock(ActivationToken.class);
        when(activationToken.getUser()).thenReturn(user2);
        doNothing().when(activationToken).setExpiryDate(Mockito.<Date>any());
        doNothing().when(activationToken).setId(Mockito.<Long>any());
        doNothing().when(activationToken).setToken(Mockito.<String>any());
        doNothing().when(activationToken).setUser(Mockito.<User>any());
        activationToken
                .setExpiryDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        activationToken.setId(1L);
        activationToken.setToken("ABC123");
        activationToken.setUser(user);
        doNothing().when(activationTokenRepository).deleteByToken(Mockito.<String>any());
        when(activationTokenRepository.findByToken(Mockito.<String>any())).thenReturn(activationToken);

        Media avatar3 = new Media();
        avatar3.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar3.setId(1L);
        avatar3.setMediaType("Media Type");
        avatar3.setOriginalFileName("foo.txt");
        avatar3.setSize(3L);

        User user3 = new User();
        user3.setActive(true);
        user3.setAuthorities(new HashSet<>());
        user3.setAvatar(avatar3);
        user3.setEmail("jane.doe@example.org");
        user3.setId(1L);
        user3.setName("Name");
        user3.setPassword("iloveyou");
        user3.setPasswordConfirm("Password Confirm");
        user3.setProjects(new HashSet<>());
        user3.setSurname("Doe");
        user3.setUsername("janedoe");
        when(userRepository.save(Mockito.<User>any())).thenReturn(user3);
        userService.activateUser("Code");
        verify(activationTokenRepository).findByToken(Mockito.<String>any());
        verify(activationTokenRepository).deleteByToken(Mockito.<String>any());
        verify(activationToken).getUser();
        verify(activationToken).setExpiryDate(Mockito.<Date>any());
        verify(activationToken).setId(Mockito.<Long>any());
        verify(activationToken).setToken(Mockito.<String>any());
        verify(activationToken).setUser(Mockito.<User>any());
        verify(user2, atLeast(1)).setActive(anyBoolean());
        verify(user2).setAuthorities(Mockito.<Set<Role>>any());
        verify(user2).setAvatar(Mockito.<Media>any());
        verify(user2).setEmail(Mockito.<String>any());
        verify(user2).setId(Mockito.<Long>any());
        verify(user2).setName(Mockito.<String>any());
        verify(user2).setPassword(Mockito.<String>any());
        verify(user2).setPasswordConfirm(Mockito.<String>any());
        verify(user2).setProjects(Mockito.<Set<Project>>any());
        verify(user2).setSurname(Mockito.<String>any());
        verify(user2).setUsername(Mockito.<String>any());
        verify(userRepository).save(Mockito.<User>any());
    }

    /**
     * Method under test: {@link UserService#createActivationCode(User)}
     */
    @Test
    void testCreateActivationCode() throws UnsupportedEncodingException, MessagingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");

        ActivationToken activationToken = new ActivationToken();
        activationToken
                .setExpiryDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        activationToken.setId(1L);
        activationToken.setToken("ABC123");
        activationToken.setUser(user);
        when(activationTokenRepository.save(Mockito.<ActivationToken>any())).thenReturn(activationToken);
        doNothing().when(emailService).sendSimpleMessage(Mockito.<String>any(), Mockito.<String>any());

        Media avatar2 = new Media();
        avatar2.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar2.setId(1L);
        avatar2.setMediaType("Media Type");
        avatar2.setOriginalFileName("foo.txt");
        avatar2.setSize(3L);

        User user2 = new User();
        user2.setActive(true);
        user2.setAuthorities(new HashSet<>());
        user2.setAvatar(avatar2);
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setName("Name");
        user2.setPassword("iloveyou");
        user2.setPasswordConfirm("Password Confirm");
        user2.setProjects(new HashSet<>());
        user2.setSurname("Doe");
        user2.setUsername("janedoe");
        userService.createActivationCode(user2);
        verify(activationTokenRepository).save(Mockito.<ActivationToken>any());
        verify(emailService).sendSimpleMessage(Mockito.<String>any(), Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#createActivationCode(User)}
     */
    @Test
    void testCreateActivationCode2() throws UnsupportedEncodingException, MessagingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");

        ActivationToken activationToken = new ActivationToken();
        activationToken
                .setExpiryDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        activationToken.setId(1L);
        activationToken.setToken("ABC123");
        activationToken.setUser(user);
        when(activationTokenRepository.save(Mockito.<ActivationToken>any())).thenReturn(activationToken);
        doThrow(new UsernameNotFoundException("Msg")).when(emailService)
                .sendSimpleMessage(Mockito.<String>any(), Mockito.<String>any());

        Media avatar2 = new Media();
        avatar2.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar2.setId(1L);
        avatar2.setMediaType("Media Type");
        avatar2.setOriginalFileName("foo.txt");
        avatar2.setSize(3L);

        User user2 = new User();
        user2.setActive(true);
        user2.setAuthorities(new HashSet<>());
        user2.setAvatar(avatar2);
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setName("Name");
        user2.setPassword("iloveyou");
        user2.setPasswordConfirm("Password Confirm");
        user2.setProjects(new HashSet<>());
        user2.setSurname("Doe");
        user2.setUsername("janedoe");
        assertThrows(UsernameNotFoundException.class, () -> userService.createActivationCode(user2));
        verify(activationTokenRepository).save(Mockito.<ActivationToken>any());
        verify(emailService).sendSimpleMessage(Mockito.<String>any(), Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#createActivationCode(User)}
     */
    @Test
    void testCreateActivationCode3() throws UnsupportedEncodingException, MessagingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");

        ActivationToken activationToken = new ActivationToken();
        activationToken
                .setExpiryDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        activationToken.setId(1L);
        activationToken.setToken("ABC123");
        activationToken.setUser(user);
        when(activationTokenRepository.save(Mockito.<ActivationToken>any())).thenReturn(activationToken);
        doNothing().when(emailService).sendSimpleMessage(Mockito.<String>any(), Mockito.<String>any());

        Media avatar2 = new Media();
        avatar2.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar2.setId(1L);
        avatar2.setMediaType("Media Type");
        avatar2.setOriginalFileName("foo.txt");
        avatar2.setSize(3L);
        User user2 = mock(User.class);
        when(user2.getUsername()).thenReturn("janedoe");
        when(user2.getEmail()).thenReturn("jane.doe@example.org");
        doNothing().when(user2).setActive(anyBoolean());
        doNothing().when(user2).setAuthorities(Mockito.<Set<Role>>any());
        doNothing().when(user2).setAvatar(Mockito.<Media>any());
        doNothing().when(user2).setEmail(Mockito.<String>any());
        doNothing().when(user2).setId(Mockito.<Long>any());
        doNothing().when(user2).setName(Mockito.<String>any());
        doNothing().when(user2).setPassword(Mockito.<String>any());
        doNothing().when(user2).setPasswordConfirm(Mockito.<String>any());
        doNothing().when(user2).setProjects(Mockito.<Set<Project>>any());
        doNothing().when(user2).setSurname(Mockito.<String>any());
        doNothing().when(user2).setUsername(Mockito.<String>any());
        user2.setActive(true);
        user2.setAuthorities(new HashSet<>());
        user2.setAvatar(avatar2);
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setName("Name");
        user2.setPassword("iloveyou");
        user2.setPasswordConfirm("Password Confirm");
        user2.setProjects(new HashSet<>());
        user2.setSurname("Doe");
        user2.setUsername("janedoe");
        userService.createActivationCode(user2);
        verify(activationTokenRepository).save(Mockito.<ActivationToken>any());
        verify(emailService).sendSimpleMessage(Mockito.<String>any(), Mockito.<String>any());
        verify(user2, atLeast(1)).getEmail();
        verify(user2).getUsername();
        verify(user2).setActive(anyBoolean());
        verify(user2).setAuthorities(Mockito.<Set<Role>>any());
        verify(user2).setAvatar(Mockito.<Media>any());
        verify(user2).setEmail(Mockito.<String>any());
        verify(user2).setId(Mockito.<Long>any());
        verify(user2).setName(Mockito.<String>any());
        verify(user2).setPassword(Mockito.<String>any());
        verify(user2).setPasswordConfirm(Mockito.<String>any());
        verify(user2).setProjects(Mockito.<Set<Project>>any());
        verify(user2).setSurname(Mockito.<String>any());
        verify(user2).setUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#createActivationCode(User)}
     */
    @Test
    void testCreateActivationCode4() throws UnsupportedEncodingException, MessagingException {
        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");

        ActivationToken activationToken = new ActivationToken();
        activationToken
                .setExpiryDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        activationToken.setId(1L);
        activationToken.setToken("ABC123");
        activationToken.setUser(user);
        when(activationTokenRepository.save(Mockito.<ActivationToken>any())).thenReturn(activationToken);
        doNothing().when(emailService).sendSimpleMessage(Mockito.<String>any(), Mockito.<String>any());

        Media avatar2 = new Media();
        avatar2.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar2.setId(1L);
        avatar2.setMediaType("Media Type");
        avatar2.setOriginalFileName("foo.txt");
        avatar2.setSize(3L);
        User user2 = mock(User.class);
        when(user2.getUsername()).thenThrow(new UsernameNotFoundException("Msg"));
        when(user2.getEmail()).thenReturn("jane.doe@example.org");
        doNothing().when(user2).setActive(anyBoolean());
        doNothing().when(user2).setAuthorities(Mockito.<Set<Role>>any());
        doNothing().when(user2).setAvatar(Mockito.<Media>any());
        doNothing().when(user2).setEmail(Mockito.<String>any());
        doNothing().when(user2).setId(Mockito.<Long>any());
        doNothing().when(user2).setName(Mockito.<String>any());
        doNothing().when(user2).setPassword(Mockito.<String>any());
        doNothing().when(user2).setPasswordConfirm(Mockito.<String>any());
        doNothing().when(user2).setProjects(Mockito.<Set<Project>>any());
        doNothing().when(user2).setSurname(Mockito.<String>any());
        doNothing().when(user2).setUsername(Mockito.<String>any());
        user2.setActive(true);
        user2.setAuthorities(new HashSet<>());
        user2.setAvatar(avatar2);
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setName("Name");
        user2.setPassword("iloveyou");
        user2.setPasswordConfirm("Password Confirm");
        user2.setProjects(new HashSet<>());
        user2.setSurname("Doe");
        user2.setUsername("janedoe");
        userService.createActivationCode(user2);
        verify(activationTokenRepository).save(Mockito.<ActivationToken>any());
        verify(user2).getEmail();
        verify(user2).getUsername();
        verify(user2).setActive(anyBoolean());
        verify(user2).setAuthorities(Mockito.<Set<Role>>any());
        verify(user2).setAvatar(Mockito.<Media>any());
        verify(user2).setEmail(Mockito.<String>any());
        verify(user2).setId(Mockito.<Long>any());
        verify(user2).setName(Mockito.<String>any());
        verify(user2).setPassword(Mockito.<String>any());
        verify(user2).setPasswordConfirm(Mockito.<String>any());
        verify(user2).setProjects(Mockito.<Set<Project>>any());
        verify(user2).setSurname(Mockito.<String>any());
        verify(user2).setUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#changePasswordByToken(PasswordTokenDTO, PasswordToken)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testChangePasswordByToken() throws UnsupportedEncodingException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.example.monicio.DTO.PasswordTokenDTO.getPassword()" because "passwordTokenDTO" is null
        //       at com.example.monicio.Services.UserService.changePasswordByToken(UserService.java:364)
        //   See https://diff.blue/R013 to resolve this issue.

        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");

        PasswordToken token = new PasswordToken();
        token.setExpiryDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        token.setId(1L);
        token.setToken("ABC123");
        token.setUser(user);
        userService.changePasswordByToken(null, token);
    }

    /**
     * Method under test: {@link UserService#changePasswordByToken(PasswordTokenDTO, PasswordToken)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testChangePasswordByToken2() throws UnsupportedEncodingException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.example.monicio.DTO.PasswordTokenDTO.getPassword()" because "passwordTokenDTO" is null
        //       at com.example.monicio.Services.UserService.changePasswordByToken(UserService.java:364)
        //   See https://diff.blue/R013 to resolve this issue.

        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");

        Media avatar2 = new Media();
        avatar2.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar2.setId(1L);
        avatar2.setMediaType("Media Type");
        avatar2.setOriginalFileName("foo.txt");
        avatar2.setSize(3L);

        User user2 = new User();
        user2.setActive(true);
        user2.setAuthorities(new HashSet<>());
        user2.setAvatar(avatar2);
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setName("Name");
        user2.setPassword("iloveyou");
        user2.setPasswordConfirm("Password Confirm");
        user2.setProjects(new HashSet<>());
        user2.setSurname("Doe");
        user2.setUsername("janedoe");
        PasswordToken token = mock(PasswordToken.class);
        when(token.getUser()).thenReturn(user2);
        doNothing().when(token).setExpiryDate(Mockito.<Date>any());
        doNothing().when(token).setId(Mockito.<Long>any());
        doNothing().when(token).setToken(Mockito.<String>any());
        doNothing().when(token).setUser(Mockito.<User>any());
        token.setExpiryDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        token.setId(1L);
        token.setToken("ABC123");
        token.setUser(user);
        userService.changePasswordByToken(null, token);
    }

    /**
     * Method under test: {@link UserService#createPasswordToken(PasswordForgetDTO)}
     */
    @Test
    void testCreatePasswordToken() throws UnsupportedEncodingException, MessagingException {
        doNothing().when(emailService).sendSimpleMessage(Mockito.<String>any(), Mockito.<String>any());

        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");

        PasswordToken passwordToken = new PasswordToken();
        passwordToken
                .setExpiryDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        passwordToken.setId(1L);
        passwordToken.setToken("ABC123");
        passwordToken.setUser(user);
        when(passwordTokenRepository.save(Mockito.<PasswordToken>any())).thenReturn(passwordToken);

        Media avatar2 = new Media();
        avatar2.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar2.setId(1L);
        avatar2.setMediaType("Media Type");
        avatar2.setOriginalFileName("foo.txt");
        avatar2.setSize(3L);

        User user2 = new User();
        user2.setActive(true);
        user2.setAuthorities(new HashSet<>());
        user2.setAvatar(avatar2);
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setName("Name");
        user2.setPassword("iloveyou");
        user2.setPasswordConfirm("Password Confirm");
        user2.setProjects(new HashSet<>());
        user2.setSurname("Doe");
        user2.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user2);
        when(userRepository.findUserByEmail(Mockito.<String>any())).thenReturn(ofResult);

        PasswordForgetDTO passwordForgetDTO = new PasswordForgetDTO();
        passwordForgetDTO.setEmail("jane.doe@example.org");
        assertTrue(userService.createPasswordToken(passwordForgetDTO));
        verify(emailService).sendSimpleMessage(Mockito.<String>any(), Mockito.<String>any());
        verify(passwordTokenRepository).save(Mockito.<PasswordToken>any());
        verify(userRepository).findUserByEmail(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#createPasswordToken(PasswordForgetDTO)}
     */
    @Test
    void testCreatePasswordToken2() throws UnsupportedEncodingException, MessagingException {
        doNothing().when(emailService).sendSimpleMessage(Mockito.<String>any(), Mockito.<String>any());
        when(passwordTokenRepository.save(Mockito.<PasswordToken>any())).thenThrow(new UsernameNotFoundException("Msg"));

        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findUserByEmail(Mockito.<String>any())).thenReturn(ofResult);

        PasswordForgetDTO passwordForgetDTO = new PasswordForgetDTO();
        passwordForgetDTO.setEmail("jane.doe@example.org");
        assertThrows(UsernameNotFoundException.class, () -> userService.createPasswordToken(passwordForgetDTO));
        verify(passwordTokenRepository).save(Mockito.<PasswordToken>any());
        verify(userRepository).findUserByEmail(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#createPasswordToken(PasswordForgetDTO)}
     */
    @Test
    void testCreatePasswordToken3() throws UnsupportedEncodingException, MessagingException {
        doNothing().when(emailService).sendSimpleMessage(Mockito.<String>any(), Mockito.<String>any());

        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");

        PasswordToken passwordToken = new PasswordToken();
        passwordToken
                .setExpiryDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        passwordToken.setId(1L);
        passwordToken.setToken("ABC123");
        passwordToken.setUser(user);
        when(passwordTokenRepository.save(Mockito.<PasswordToken>any())).thenReturn(passwordToken);

        Media avatar2 = new Media();
        avatar2.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar2.setId(1L);
        avatar2.setMediaType("Media Type");
        avatar2.setOriginalFileName("foo.txt");
        avatar2.setSize(3L);
        User user2 = mock(User.class);
        when(user2.getUsername()).thenReturn("janedoe");
        when(user2.getEmail()).thenReturn("jane.doe@example.org");
        doNothing().when(user2).setActive(anyBoolean());
        doNothing().when(user2).setAuthorities(Mockito.<Set<Role>>any());
        doNothing().when(user2).setAvatar(Mockito.<Media>any());
        doNothing().when(user2).setEmail(Mockito.<String>any());
        doNothing().when(user2).setId(Mockito.<Long>any());
        doNothing().when(user2).setName(Mockito.<String>any());
        doNothing().when(user2).setPassword(Mockito.<String>any());
        doNothing().when(user2).setPasswordConfirm(Mockito.<String>any());
        doNothing().when(user2).setProjects(Mockito.<Set<Project>>any());
        doNothing().when(user2).setSurname(Mockito.<String>any());
        doNothing().when(user2).setUsername(Mockito.<String>any());
        user2.setActive(true);
        user2.setAuthorities(new HashSet<>());
        user2.setAvatar(avatar2);
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setName("Name");
        user2.setPassword("iloveyou");
        user2.setPasswordConfirm("Password Confirm");
        user2.setProjects(new HashSet<>());
        user2.setSurname("Doe");
        user2.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user2);
        when(userRepository.findUserByEmail(Mockito.<String>any())).thenReturn(ofResult);

        PasswordForgetDTO passwordForgetDTO = new PasswordForgetDTO();
        passwordForgetDTO.setEmail("jane.doe@example.org");
        assertTrue(userService.createPasswordToken(passwordForgetDTO));
        verify(emailService).sendSimpleMessage(Mockito.<String>any(), Mockito.<String>any());
        verify(passwordTokenRepository).save(Mockito.<PasswordToken>any());
        verify(userRepository).findUserByEmail(Mockito.<String>any());
        verify(user2, atLeast(1)).getEmail();
        verify(user2).getUsername();
        verify(user2).setActive(anyBoolean());
        verify(user2).setAuthorities(Mockito.<Set<Role>>any());
        verify(user2).setAvatar(Mockito.<Media>any());
        verify(user2).setEmail(Mockito.<String>any());
        verify(user2).setId(Mockito.<Long>any());
        verify(user2).setName(Mockito.<String>any());
        verify(user2).setPassword(Mockito.<String>any());
        verify(user2).setPasswordConfirm(Mockito.<String>any());
        verify(user2).setProjects(Mockito.<Set<Project>>any());
        verify(user2).setSurname(Mockito.<String>any());
        verify(user2).setUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#createPasswordToken(PasswordForgetDTO)}
     */
    @Test
    void testCreatePasswordToken4() throws UnsupportedEncodingException, MessagingException {
        doNothing().when(emailService).sendSimpleMessage(Mockito.<String>any(), Mockito.<String>any());

        Media avatar = new Media();
        avatar.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar.setId(1L);
        avatar.setMediaType("Media Type");
        avatar.setOriginalFileName("foo.txt");
        avatar.setSize(3L);

        User user = new User();
        user.setActive(true);
        user.setAuthorities(new HashSet<>());
        user.setAvatar(avatar);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setPasswordConfirm("Password Confirm");
        user.setProjects(new HashSet<>());
        user.setSurname("Doe");
        user.setUsername("janedoe");

        PasswordToken passwordToken = new PasswordToken();
        passwordToken
                .setExpiryDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        passwordToken.setId(1L);
        passwordToken.setToken("ABC123");
        passwordToken.setUser(user);
        when(passwordTokenRepository.save(Mockito.<PasswordToken>any())).thenReturn(passwordToken);

        Media avatar2 = new Media();
        avatar2.setBytes("AXAXAXAX".getBytes("UTF-8"));
        avatar2.setId(1L);
        avatar2.setMediaType("Media Type");
        avatar2.setOriginalFileName("foo.txt");
        avatar2.setSize(3L);
        User user2 = mock(User.class);
        when(user2.getUsername()).thenThrow(new UsernameNotFoundException("Msg"));
        when(user2.getEmail()).thenReturn("jane.doe@example.org");
        doNothing().when(user2).setActive(anyBoolean());
        doNothing().when(user2).setAuthorities(Mockito.<Set<Role>>any());
        doNothing().when(user2).setAvatar(Mockito.<Media>any());
        doNothing().when(user2).setEmail(Mockito.<String>any());
        doNothing().when(user2).setId(Mockito.<Long>any());
        doNothing().when(user2).setName(Mockito.<String>any());
        doNothing().when(user2).setPassword(Mockito.<String>any());
        doNothing().when(user2).setPasswordConfirm(Mockito.<String>any());
        doNothing().when(user2).setProjects(Mockito.<Set<Project>>any());
        doNothing().when(user2).setSurname(Mockito.<String>any());
        doNothing().when(user2).setUsername(Mockito.<String>any());
        user2.setActive(true);
        user2.setAuthorities(new HashSet<>());
        user2.setAvatar(avatar2);
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setName("Name");
        user2.setPassword("iloveyou");
        user2.setPasswordConfirm("Password Confirm");
        user2.setProjects(new HashSet<>());
        user2.setSurname("Doe");
        user2.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user2);
        when(userRepository.findUserByEmail(Mockito.<String>any())).thenReturn(ofResult);

        PasswordForgetDTO passwordForgetDTO = new PasswordForgetDTO();
        passwordForgetDTO.setEmail("jane.doe@example.org");
        assertThrows(UsernameNotFoundException.class, () -> userService.createPasswordToken(passwordForgetDTO));
        verify(passwordTokenRepository).save(Mockito.<PasswordToken>any());
        verify(userRepository).findUserByEmail(Mockito.<String>any());
        verify(user2).getEmail();
        verify(user2).getUsername();
        verify(user2).setActive(anyBoolean());
        verify(user2).setAuthorities(Mockito.<Set<Role>>any());
        verify(user2).setAvatar(Mockito.<Media>any());
        verify(user2).setEmail(Mockito.<String>any());
        verify(user2).setId(Mockito.<Long>any());
        verify(user2).setName(Mockito.<String>any());
        verify(user2).setPassword(Mockito.<String>any());
        verify(user2).setPasswordConfirm(Mockito.<String>any());
        verify(user2).setProjects(Mockito.<Set<Project>>any());
        verify(user2).setSurname(Mockito.<String>any());
        verify(user2).setUsername(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#sendCallback(CallbackRequestDTO)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testSendCallback() throws MessagingException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.example.monicio.DTO.CallbackRequestDTO.getName()" because "callbackRequestDTO" is null
        //       at com.example.monicio.Services.UserService.sendCallback(UserService.java:401)
        //   See https://diff.blue/R013 to resolve this issue.

        userService.sendCallback(null);
    }

    /**
     * Method under test: {@link UserService#sendCallback(CallbackRequestDTO)}
     */
    @Test
    void testSendCallback2() throws MessagingException {
        doNothing().when(emailService).sendSimpleMessage(Mockito.<String>any(), Mockito.<String>any());
        CallbackRequestDTO callbackRequestDTO = mock(CallbackRequestDTO.class);
        when(callbackRequestDTO.getEmail()).thenReturn("jane.doe@example.org");
        when(callbackRequestDTO.getMessage()).thenReturn("Not all who wander are lost");
        when(callbackRequestDTO.getName()).thenReturn("Name");
        when(callbackRequestDTO.getTheme()).thenReturn("Theme");
        userService.sendCallback(callbackRequestDTO);
        verify(emailService).sendSimpleMessage(Mockito.<String>any(), Mockito.<String>any());
        verify(callbackRequestDTO).getEmail();
        verify(callbackRequestDTO).getMessage();
        verify(callbackRequestDTO).getName();
        verify(callbackRequestDTO).getTheme();
    }
}

