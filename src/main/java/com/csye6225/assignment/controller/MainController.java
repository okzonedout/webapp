package com.csye6225.assignment.controller;

import com.csye6225.assignment.service.MessageService;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.mashape.unirest.http.JsonNode;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import static com.csye6225.assignment.Constants.HEALTH_URI;

@RestController
@RequestMapping(HEALTH_URI)
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    MessageService messageService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> healthCheck(HttpServletRequest request, @RequestBody @Nullable String body){
        if(request.getQueryString()!=null || body != null || request.getHeader("Authorization") !=null){
            LOGGER.warn("Payload provided. Bad Request");
            return ResponseEntity.badRequest().build();
        }

        try {
            jdbcTemplate.execute("SELECT 1");
        }catch(Exception e){
            LOGGER.error("Database connection failed. Service Unavailable.");
            LOGGER.debug("Database connection failed." + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        LOGGER.info("Database connection valid.");
//        messageService.publishMessageToTopic("Database connection valid.");
        return ResponseEntity.ok().contentLength(0).build();
    }

}
