package com.example.monicio.DTO;

import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Data
@Component
@Getter
public class userInfo {
    private String userName;
    private Object Roles;
}
