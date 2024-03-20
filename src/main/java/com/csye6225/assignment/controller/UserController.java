package com.csye6225.assignment.controller;

import com.csye6225.assignment.model.Account;
import com.csye6225.assignment.repository.UserRepository;
import com.csye6225.assignment.security.CustomUserDetail;
import com.csye6225.assignment.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.parser.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.csye6225.assignment.Constants.*;

@RestController
@RequestMapping(V1_USER_URI)
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(HttpServletRequest request, @RequestBody Account user){

        if (user.getUsername()==null){
            LOGGER.warn("Invalid Payload. Username is null.");
            return ResponseEntity.badRequest().build();
        }

        if(user.getPassword()==null || user.getPassword().isEmpty()){
            LOGGER.warn("Invalid Payload. Password is null.");
            return ResponseEntity.badRequest().build();
        }

        if(!user.getPassword().matches(PASSWORD_REGEX)){
            LOGGER.warn("Invalid Payload. Password requirements unfulfilled.");
            return ResponseEntity.badRequest().build();
        }

        if(userService.getUser(user.getUsername())!=null){
            LOGGER.warn("Invalid Payload. User already exists.");
            return ResponseEntity.badRequest().build();
        }

        if(!user.getUsername().matches(VALID_EMAIL_REGEX)){
            LOGGER.warn("Invalid Payload. Invalid Email");
            return ResponseEntity.badRequest().build();
        }
        LOGGER.info("New user created.");
        return ResponseEntity.ok().body(userService.createUser(user));
    }

    @GetMapping("/self")
    public ResponseEntity<?> getUser(Authentication authentication){

        CustomUserDetail userAuthDetail = (CustomUserDetail) authentication.getPrincipal();

        if(userAuthDetail.getUsername().equals("") || userAuthDetail.getPassword().equals("")){
            LOGGER.warn("Invalid Payload. Credentials are null.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Account currentUser = userService.getUser(userAuthDetail.getUsername());

        if (currentUser==null){
            LOGGER.warn("User does not exist.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(!currentUser.getPassword().equals(userAuthDetail.getPassword())){
            LOGGER.warn("Invalid Password.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        LOGGER.info("User found. Authenticated");
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(userAuthDetail.getUsername()));

    }

    @PutMapping("/self")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> updateUser(@RequestBody Account user, Authentication authentication){
        CustomUserDetail userAuthDetail = (CustomUserDetail) authentication.getPrincipal();

        // Invalid username and password
        if(userAuthDetail.getUsername().equals("") || userAuthDetail.getPassword().equals("")){
            LOGGER.warn("Invalid Payload. Empty Credentials");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Account currentUser = userService.getUser(userAuthDetail.getUsername());

        if (currentUser==null){
            LOGGER.warn("User not found.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(user.getUsername() != null){
            LOGGER.warn("Invalid Payload. Username is empty");
            return ResponseEntity.badRequest().build();
        }
        if(null != user.getPassword() && user.getPassword().isEmpty()){
            LOGGER.warn("Invalid Payload. Password is empty");
            return ResponseEntity.badRequest().build();
        }
        if(null != user.getPassword() && !user.getPassword().matches(PASSWORD_REGEX)){
            LOGGER.warn("Invalid Payload. Password invalid");
            return ResponseEntity.badRequest().build();
        }
        if(user.getAccountUpdated() != null){
            LOGGER.warn("Invalid Payload. Unsupported fields included.");
            return ResponseEntity.badRequest().build();
        }
        if (user.getAccountCreated()!=null){
            LOGGER.warn("Invalid Payload. Unsupported fields included.");
            return ResponseEntity.badRequest().build();
        }
        user.setUsername(userAuthDetail.getUsername());
        LOGGER.info("User info updated.");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(userService.updateUser(user));
    }
}
