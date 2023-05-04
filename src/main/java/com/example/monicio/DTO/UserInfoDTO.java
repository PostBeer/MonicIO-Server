package com.example.monicio.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserInfoDTO {

    private String username;

    private String email;

    private String name;

    private String surname;

    private List<String> roles;
}
