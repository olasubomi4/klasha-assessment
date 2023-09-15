package com.klasha.assessment.security;


import com.klasha.assessment.security.filter.AuthenticationFilter;
import com.klasha.assessment.security.filter.ExceptionHandlerFilter;
import com.klasha.assessment.security.filter.JWTAuthorizationFilter;
import com.klasha.assessment.security.manager.CustomAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.config.http.SessionCreationPolicy;



@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.secret.key}")
    public String secretKey;
    @Value("${jwt.token.expiration}")
    public Integer tokenExpiration;
    @Autowired
    private CustomAuthenticationManager customAuthenticationManager;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        AuthenticationFilter authenticationFilter = new AuthenticationFilter(secretKey,tokenExpiration,customAuthenticationManager);
        authenticationFilter.setFilterProcessesUrl("/api/v1/user/authenticate");

        http
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
                .authorizeRequests(authorize -> authorize.requestMatchers(HttpMethod.POST,SecurityConstants.REGISTER_PATH).permitAll().requestMatchers(SecurityConstants.AUTH_WHITELIST).permitAll()
                .anyRequest()
                .authenticated())
                .addFilterBefore(new ExceptionHandlerFilter(), AuthenticationFilter.class)
                .addFilter(authenticationFilter)
                .addFilterAfter(new JWTAuthorizationFilter(secretKey,tokenExpiration), AuthenticationFilter.class)
                .sessionManagement(mgt->mgt.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();

    }
}