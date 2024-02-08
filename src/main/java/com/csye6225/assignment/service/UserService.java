package com.csye6225.assignment.service;

import com.csye6225.assignment.model.Account;
import com.csye6225.assignment.repository.UserRepository;

import com.csye6225.assignment.security.BcryptEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class UserService {

    @Autowired
    BcryptEncoder bcryptEncoder;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private static final Base64.Encoder BASE64ENCODER = Base64.getUrlEncoder();
    private static final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    UserRepository userRepository;

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return BASE64ENCODER.encodeToString(randomBytes);
    }

    public Account createUser(Account user){
        user.setAccountCreated(LocalDateTime.now());
        user.setAccountUpdated(LocalDateTime.now());
        String hashedPassword = bcryptEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    public Account getUser(String username){
        return userRepository.findByUsername(username);
    }

    public Account updateUser(Account user) {
        Account existingUser = userRepository.findByUsername(user.getUsername());
        existingUser.setAccountUpdated(LocalDateTime.now());
        if(user.getFirstName() != null) existingUser.setFirstName(user.getFirstName());
        if(user.getLastName() != null) existingUser.setLastName(user.getLastName());
        // encrypt password before storing in db
        if(user.getPassword() != null){ existingUser.setPassword(bcryptEncoder.encode(user.getPassword()));}
        return userRepository.save(existingUser);
    }
}
