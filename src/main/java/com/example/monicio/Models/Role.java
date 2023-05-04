package com.example.monicio.Models;


import org.springframework.security.core.GrantedAuthority;

/**
 * The enum Role.
 *
 * @author HukoJlauII, Nikita Zhiznevskiy
 */
public enum Role implements GrantedAuthority {

    /**
     * User role.
     */
    USER,
    /**
     * Project manager role.
     */
    PROJECT_MANAGER,
    /**
     * Admin role.
     */
    ADMIN;


    /**
     * Gets authority.
     *
     * @return the authority
     */
    @Override
    public String getAuthority() {
        return name();
    }
}