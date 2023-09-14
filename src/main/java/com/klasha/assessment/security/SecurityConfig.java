package com.klasha.assessment.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


import lombok.AllArgsConstructor;

import org.springframework.security.config.http.SessionCreationPolicy;


@Configuration
@AllArgsConstructor
public class SecurityConfig {

//    private final CustomAuthenticationManager customAuthenticationManager;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

//        AuthenticationFilter authenticationFilter = new AuthenticationFilter(customAuthenticationManager);
//        authenticationFilter.setFilterProcessesUrl("/authenticate");

        http
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
                .sessionManagement(mgt->mgt.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                  .authorizeHttpRequests(req->req.requestMatchers("**").permitAll());
        return http.build();
    }
    
}