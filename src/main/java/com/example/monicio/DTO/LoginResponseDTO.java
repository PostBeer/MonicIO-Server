package com.example.monicio.DTO;

import lombok.Builder;
import lombok.Data;

/**
 * A DTO for the {@link com.example.monicio.Models.User} entity
 *
 * @author HukoJlauII, Nikita Zhiznevskiy
 */
@Data
@Builder
public class LoginResponseDTO {

    private String jwt;

    private UserInfoDTO userInfoDTO;
}
