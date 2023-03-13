package com.example.monicio.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@PreAuthorize("hasAuthority('ROLE_USER')")
public class UserController {
    @PostMapping("/home")
    public ResponseEntity<?> testController(Principal principal) {
        return ResponseEntity.ok(principal);
    }
}
