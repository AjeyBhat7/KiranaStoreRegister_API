package com.jar.kiranaregister.auth.constants;

import io.jsonwebtoken.security.Keys;
import java.security.Key;

public class AuthConstants {

    public static final Key SECRET_KEY =
            Keys.hmacShaKeyFor("bikwanJNSDbkbkBKHu8yuerk3jb8R4KHi9JBKiy8HKhJHVb".getBytes());

    public static final String LOG_ILLEGAL_STATE = "Illegal state encountered: {0}";
    public static final String LOG_TOKEN_EXPIRED = "JWT token is expired: {0}";
    public static final String LOG_TOKEN_UNSUPPORTED = "JWT token is unsupported: {0}";
    public static final String LOG_TOKEN_MALFORMED = "JWT token is malformed: {0}";
    public static final String LOG_TOKEN_VALIDATION_ERROR =
            "Unexpected error while validating token: {0}";
}
