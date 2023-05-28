package com.example.monicio.Controllers;


import com.example.monicio.DTO.CallbackRequestDTO;
import com.example.monicio.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.mail.MessagingException;
import javax.validation.Valid;

/**
 * Controller sending callback
 *
 * @author Maxim Milko
 */
@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CallbackController {

    /**
     * The User service.
     */
    private final UserService userService;

    /**
     * Activate user after click on activation link.
     *
     * @param callbackRequestDTO the information about callback
     * @return response with status 200 if information is valid <br>
     * response with status 409 if information is not valid
     */
    @PostMapping("/callback")
    public ResponseEntity<?> sendCallBack(@Valid @RequestBody CallbackRequestDTO callbackRequestDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.CONFLICT);
        }
        try{
            userService.sendCallback(callbackRequestDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (MessagingException e) {
            return new ResponseEntity<>("Ошибка при отправке сообщения", HttpStatus.CONFLICT);
        }
    }
}
