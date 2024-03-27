package com.csye6225.assignment.service;

import com.csye6225.assignment.controller.UserController;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Service
public class MessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    PubSubTemplate pubSubTemplate;

    public boolean publishMessageToTopic(String msg){

        try {
            CompletableFuture<String> temp = pubSubTemplate.publish("verify_email",msg);
            if(temp.isDone()){
                return true;
            }
            LOGGER.error("Message Publish Not Done. ");
        }catch (Exception e){
            LOGGER.error("Message Publish Failed. " + e.getMessage());
            LOGGER.debug("Message Publish Error. \n" + Arrays.toString(e.getStackTrace()));
        }
        return false;
    }
}
