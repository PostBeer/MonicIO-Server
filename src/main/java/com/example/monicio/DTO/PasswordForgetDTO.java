package com.example.monicio.DTO;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * A DTO for the email {@link com.example.monicio.Models.User} entity
 *
 * @author Maxim Milko
 */

@Data
public class PasswordForgetDTO {
    @Email(message = "Поле должно иметь формат эл.почты")
    @NotBlank(message = "Поле не может быть пустым")
    private String email;
}
