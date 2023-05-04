package com.example.monicio.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {

    private String jwt;

    private UserInfoDTO userInfoDTO;
}
