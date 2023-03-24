package com.example.monicio.DTO;

import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Data
@Component
@Getter
public class UserDTO {
    private String userName;
    private String password;
    private Object Roles;
    private String email;
}
