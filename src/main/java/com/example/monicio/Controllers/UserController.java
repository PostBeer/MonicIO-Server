package com.example.monicio.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
//@PreAuthorize("{hasAuthority('ROLE_USER'),hasAuthority('ROLE_ADMIN')}")
@Secured({"ADMIN", "USER", "PROJECT_MANAGER"})
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    @PostMapping("/home")
    public ResponseEntity<?> testController(Principal principal) {
        return ResponseEntity.ok(principal);
    }
}
