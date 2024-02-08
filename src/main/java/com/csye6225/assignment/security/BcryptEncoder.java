package com.csye6225.assignment.security;

import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class BcryptEncoder {
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public BCryptPasswordEncoder getEncoder() {
        return encoder;
    }

    public String encode(String password) {
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }
}