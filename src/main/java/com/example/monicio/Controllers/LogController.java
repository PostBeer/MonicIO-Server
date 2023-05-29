package com.example.monicio.Controllers;

import com.example.monicio.Services.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/log")
@Secured({"ADMIN", "USER", "PROJECT_MANAGER"})
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LogController {
    private final LogService logService;

    @GetMapping("/project/{id}")
    public ResponseEntity<?> tasksLogForProject(@PathVariable Long id) {
        return new ResponseEntity<>(logService.tasksLogForProject(id), HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<?> tasksLogForUser(Authentication authentication) {
        return new ResponseEntity<>(logService.tasksLogForUser(authentication), HttpStatus.OK);
    }
}
