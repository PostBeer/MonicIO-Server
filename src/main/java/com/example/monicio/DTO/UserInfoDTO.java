package com.example.monicio.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * A DTO for the {@link com.example.monicio.Models.User} entity
 *
 * @author HukoJlauII, Nikita Zhiznevskiy
 */
@Data
@Builder
public class UserInfoDTO {
    private long id;
    private String username;

    private String email;

    private String name;

    private String surname;

    private List<String> roles;
}
