package com.example.monicio.Models;


import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    USER, PROJECT_MANAGER, ADMIN;


    @Override
    public String getAuthority() {
        return name();
    }
}