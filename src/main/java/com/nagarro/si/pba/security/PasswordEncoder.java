package com.nagarro.si.pba.security;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncoder {
    private final String salt;

    public PasswordEncoder() {
        int defaultCost = 12;
        this.salt = BCrypt.gensalt(defaultCost);
    }

    public  String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, salt);
    }

    public  boolean checkPasswordMatch(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
