package com.csye6225.assignment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("local")
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testHealthz() throws Exception{
        mockMvc.perform(get("/healthz"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateAndGetUser() throws Exception{
        Map<String, String> newUser = new HashMap<>();
        newUser.put("username", "testuser@example.com");
        newUser.put("password", "Test@1234");
        newUser.put("first_name", "Test");
        newUser.put("last_name", "User");

        // Create user
        mockMvc.perform(post("/v1/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newUser)))
            .andExpect(status().isOk());

        mockMvc.perform(get("/v1/user/self")
                .with(httpBasic("testuser@example.com", "Test@1234"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username",equalTo("testuser@example.com")))
                .andExpect(jsonPath("$.accountCreated",not(empty())));
    }

    @Test
    public void testUpdateUser() throws Exception {
        Map<String, String> newUser = new HashMap<>();
        newUser.put("username", "testuser@example.com");
        newUser.put("password", "Test@1234");
        newUser.put("first_name", "Test");
        newUser.put("last_name", "User");

        // Create user
        MvcResult createResult = mockMvc.perform(post("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk()).andReturn();

        String initialResponse = createResult.getResponse().getContentAsString();
        JsonNode initialJson = objectMapper.readTree(initialResponse);
        String initialAccountUpdated = initialJson.get("accountUpdated").asText();

        Map<String, String> updatedUser = new HashMap<>();
        updatedUser.put("first_name", "UpdatedTest");

        // Update user
        mockMvc.perform(put("/v1/user/self")
                        .with(httpBasic("testuser@example.com", "Test@1234"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNoContent());

        // Verify the update was successful
        MvcResult updatedResult = mockMvc.perform(get("/v1/user/self")
                        .with(httpBasic("testuser1@example.com", "Test@1234"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name", equalTo("UpdatedTest")))
                .andReturn();
        String updatedResponse = updatedResult.getResponse().getContentAsString();
        JsonNode updatedJson = objectMapper.readTree(updatedResponse);
        String updatedAccountUpdated = updatedJson.get("accountUpdated").asText();

        Assertions.assertNotEquals(initialAccountUpdated,updatedAccountUpdated);
    }
}
