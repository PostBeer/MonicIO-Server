package com.example.monicio.DTO;

public class RegisterResponseDTO {
    private String message;

    public RegisterResponseDTO(String message) {
        super();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
