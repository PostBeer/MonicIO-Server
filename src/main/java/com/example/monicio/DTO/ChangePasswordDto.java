package com.example.monicio.DTO;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO for the {@link com.example.monicio.Models.User} entity
 */
@Data
@Builder
public class ChangePasswordDto implements Serializable {
    /**
     * The Password.
     */
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
    private final String password;
    /**
     * The New password.
     */
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
    private final String newPassword;
    /**
     * The New password confirm.
     */
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
    private final String newPasswordConfirm;
}