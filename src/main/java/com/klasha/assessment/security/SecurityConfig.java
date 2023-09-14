package com.klasha.assessment.security;


import com.klasha.assessment.security.filter.AuthenticationFilter;
import com.klasha.assessment.security.filter.ExceptionHandlerFilter;
import com.klasha.assessment.security.filter.JWTAuthorizationFilter;
import com.klasha.assessment.security.manager.CustomAuthenticationManager;
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

    private final CustomAuthenticationManager customAuthenticationManager;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationFilter authenticationFilter = new AuthenticationFilter(customAuthenticationManager);
        authenticationFilter.setFilterProcessesUrl("/authenticate");

        http
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())

                .authorizeRequests(authorize -> authorize.requestMatchers(HttpMethod.POST,SecurityConstants.REGISTER_PATH).permitAll()
                .anyRequest()
                .authenticated())
                .addFilterBefore(new ExceptionHandlerFilter(), AuthenticationFilter.class)
                .addFilter(authenticationFilter)
                .addFilterAfter(new JWTAuthorizationFilter(), AuthenticationFilter.class)
                .sessionManagement(mgt->mgt.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();

    }
}