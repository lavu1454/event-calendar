package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = new User();
        user.setEmail("integration@test.com");
        user.setUsername("integrationuser");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);
    }

    @Test
    void getEvents_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getEvents_withValidToken_returns200() throws Exception {
        String token = jwtUtil.generateToken("integration@test.com");
        mockMvc.perform(get("/api/events")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void login_validCredentials_returnsToken() throws Exception {
        Map<String, String> body = Map.of(
            "usernameOrEmail", "integration@test.com",
            "password", "password123"
        );
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void login_invalidCredentials_returns400() throws Exception {
        Map<String, String> body = Map.of(
            "usernameOrEmail", "integration@test.com",
            "password", "wrongpassword"
        );
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }

    @Test
    void register_validData_returns200() throws Exception {
        Map<String, String> body = Map.of(
            "email", "newuser@test.com",
            "username", "newuser",
            "password", "password123",
            "confirmPassword", "password123"
        );
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }
}
