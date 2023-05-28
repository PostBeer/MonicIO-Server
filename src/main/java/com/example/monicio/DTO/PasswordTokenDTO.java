package com.example.monicio.DTO;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * A DTO for the {@link com.example.monicio.Models.PasswordToken} entity
 *
 * @author Maxim Milko
 */
@Data
@Builder
public class PasswordTokenDTO {

    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
    private String password;

    @NotBlank(message = "Поле не может быть пустым")
    private String passwordConfirm;
}
