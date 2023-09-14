package com.klasha.assessment.security;

import org.springframework.beans.factory.annotation.Value;

public class SecurityConstants {
    @Value("${JWT_SECRET_KEY}")
    public static String SECRET_KEY;
    @Value("${JWT_TOKEN_EXPIRATION}")
    public static int TOKEN_EXPIRATION = 7200000; // 7200000 milliseconds = 7200 seconds = 2 hours.
    @Value("{BEARER}")
    public static String BEARER = "Bearer "; // Authorization : "Bearer " + Token
    @Value("${AUTHORIZATION}")
    public static String AUTHORIZATION = "Authorization"; // "Authorization" : Bearer Token
    @Value("${REGISTER_PATH}")
    public static String REGISTER_PATH = "/api/V1/user/register"; // Public path that clients can use to register.
}