package com.example.monicio.DTO;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


/**
 * A DTO for the Callback to developer team
 *
 * @author Maxim Milko
 */
@Data
@Builder
public class CallbackRequestDTO {

    @NotBlank(message = "Поле не может быть пустым")
    private String name;

    @Email(message = "Поле должно иметь формат эл.почты")
    @NotBlank(message = "Поле не может быть пустым")
    private String email;

    @NotBlank(message = "Поле не может быть пустым")
    private String theme;

    @NotBlank(message = "Поле не может быть пустым")
    private String message;
}
