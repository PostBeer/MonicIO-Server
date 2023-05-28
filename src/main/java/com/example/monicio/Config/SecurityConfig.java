package com.example.monicio.Config;


import com.example.monicio.Config.JWT.JWTAuthFilter;
import com.example.monicio.Config.JWT.JWTUtil;
import com.example.monicio.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration
 * Includes methods for handling authentication,checking public and protected mappings
 *
 * @author Nikita Zhiznevskiy
 * @see com.example.monicio.Config.JWT.RestAuthEntryPoint
 * @see JWTAuthFilter
 * @see JWTUtil
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * The User service.
     */
    private final UserService userService;

    /**
     * The Jwt utility.
     */
    private final JWTUtil jwtUtil;

    /**
     * The Authentication entry point.
     */
    private final AuthenticationEntryPoint authenticationEntryPoint;

    /**
     * Create authentication manager bean.
     *
     * @param httpSecurity the http security
     * @return the authentication manager
     * @throws Exception the exception
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder())
                .and().build();
    }

    /**
     * Create password encoder bean.
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Create filter chain bean for configuration security.
     *
     * @param httpSecurity the http security
     * @return the security filter chain
     * @throws Exception the exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors().and()
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .authorizeHttpRequests()
                .antMatchers("/api/auth/register", "/api/auth/login", "/activate/*", "/forget/*", "/api/media/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JWTAuthFilter(userService, jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .authenticationManager(authenticationManager(httpSecurity))
                .build();

    }
}