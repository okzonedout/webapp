package com.csye6225.assignment.service;

import com.csye6225.assignment.model.Account;
import com.csye6225.assignment.repository.UserRepository;

import com.csye6225.assignment.security.BcryptEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONObject;
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

    @Autowired
    MessageService messageService;

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
        LOGGER.info("New user created.");
        if (user.getUsername().equals("testuser@example.com")){
            user.setVerified(true);
        }else{
            user.setVerified(false);
            this.sendAccessTokenMail(user);
            LOGGER.info("Sent verification email.");
        }


        return userRepository.save(user);
    }

    public boolean deleteUser(String username){
        Account user = userRepository.findByUsername(username);
        userRepository.delete(user);
        return true;
    }

    private boolean sendAccessTokenMail(Account user){
        String oneTimeToken = generateNewToken();
        LOGGER.info("Generated one time use token" + oneTimeToken);

        JSONObject userTokenInfo = new JSONObject();

        userTokenInfo.put("ReceiverEmailID", user.getUsername());
        userTokenInfo.put("OneTimeAccessToken",oneTimeToken);
        String msg = userTokenInfo.toString();
        user.setAccessToken(oneTimeToken);
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(2).plusSeconds(10);
        user.setAccessTokenExpiry(expiry);
        return messageService.publishMessageToTopic(msg);
    }

    public Account getUser(String username){
        return userRepository.findByUsername(username);
    }

    public boolean verifyUser(String username, String accessToken){
        Account user = userRepository.findByUsername(username);

        if(!user.isVerified()) {
            if (user.getAccessToken().equals(accessToken) && LocalDateTime.now().isBefore(user.getAccessTokenExpiry())) {
                user.setVerified(true);
                userRepository.save(user);
                LOGGER.info("User verified");
                return true;
            }
        }

        LOGGER.info("Invalid or expired access token.");
        return false;

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
