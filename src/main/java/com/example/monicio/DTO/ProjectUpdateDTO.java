package com.example.monicio.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Description of the class or method
 *
 * @author HukoJlauII
 */
@Data
public class ProjectUpdateDTO {


    @NotBlank(message = "Поле не должно быть пустым")
    private String title;

    @NotBlank(message = "Поле не должно быть пустым")
    private String description;


}
