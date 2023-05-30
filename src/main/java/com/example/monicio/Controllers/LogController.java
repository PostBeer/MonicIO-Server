package com.example.monicio.Controllers;

import com.example.monicio.Services.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * The type Log controller.
 */
@RestController
@RequestMapping("/api/log")
@Secured({"ADMIN", "USER", "PROJECT_MANAGER"})
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LogController {
    /**
     * The Log service.
     */
    private final LogService logService;

    /**
     * Tasks log for project response entity.
     *
     * @param id the id
     * @return the response entity
     */
    @GetMapping("/project/{id}")
    public ResponseEntity<?> tasksLogForProject(@PathVariable Long id) {
        return new ResponseEntity<>(logService.tasksLogForProject(id), HttpStatus.OK);
    }

    /**
     * Tasks log for user response entity.
     *
     * @param authentication the authentication
     * @return the response entity
     */
    @GetMapping("/user")
    public ResponseEntity<?> tasksLogForUser(Authentication authentication) {
        return new ResponseEntity<>(logService.tasksLogForUser(authentication), HttpStatus.OK);
    }
}
