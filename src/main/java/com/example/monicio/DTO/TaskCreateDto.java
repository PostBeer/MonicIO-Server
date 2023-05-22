package com.example.monicio.DTO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Description of the class or method
 *
 * @author HukoJlauII
 */
@Data
public class TaskCreateDto {

    @NotBlank(message = "Поле не должно быть пустым")
    private String name;

    @NotBlank(message = "Поле не должно быть пустым")
    private String description;
    @NotNull(message = "Заполните дату")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime completeDate;

}
