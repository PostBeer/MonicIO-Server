package com.example.monicio.DTO;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * A DTO for the {@link com.example.monicio.Models.User} entity
 *
 * @author HukoJlauII, Nikita Zhiznevskiy
 */
@Data
@Builder
public class LoginRequestDTO {
    @NotBlank(message = "Логин не может быть пустым")
    private String username;

    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

}
