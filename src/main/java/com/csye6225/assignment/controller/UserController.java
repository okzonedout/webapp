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

import static com.csye6225.assignment.Constants.V1_USER_URI;
import static com.csye6225.assignment.Constants.VALID_EMAIL_REGEX;

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
            return ResponseEntity.badRequest().build();
        }

        if(user.getPassword()==null){
            return ResponseEntity.badRequest().build();
        }

        if(userService.getUser(user.getUsername())!=null){
            return ResponseEntity.badRequest().build();
        }

        if(!user.getUsername().matches(VALID_EMAIL_REGEX)){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().body(userService.createUser(user));
    }

    @GetMapping("/self")
    public ResponseEntity<?> getUser(Authentication authentication){

        CustomUserDetail userAuthDetail = (CustomUserDetail) authentication.getPrincipal();

        if(userAuthDetail.getUsername().equals("") || userAuthDetail.getPassword().equals("")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Account currentUser = userService.getUser(userAuthDetail.getUsername());

        if (currentUser==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(!currentUser.getPassword().equals(userAuthDetail.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(userAuthDetail.getUsername()));

    }

    @PutMapping("/self")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> updateUser(@RequestBody Account user, Authentication authentication){
        CustomUserDetail userAuthDetail = (CustomUserDetail) authentication.getPrincipal();

        // Invalid username and password
        if(userAuthDetail.getUsername().equals("") || userAuthDetail.getPassword().equals("")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Account currentUser = userService.getUser(userAuthDetail.getUsername());

        if (currentUser==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if(user.getUsername() != null){
            return ResponseEntity.badRequest().build();
        }
        if(user.getAccountUpdated() != null){
            return ResponseEntity.badRequest().build();
        }
        if (user.getAccountCreated()!=null){
            return ResponseEntity.badRequest().build();
        }
        user.setUsername(userAuthDetail.getUsername());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(userService.updateUser(user));
    }
}
