package com.example.monicio.DTO;

import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String userName;
    private String password;
    private Object Roles;
    private String email;
}
