package com.staylo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staylo.dto.AuthDTO;
import com.staylo.entity.User;
import com.staylo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Authentication Controller Tests")
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should register a new user and return JWT token")
    void testRegisterSuccess() throws Exception {
        AuthDTO.RegisterRequest request = AuthDTO.RegisterRequest.builder()
                .name("Test Admin")
                .email("admin@test.com")
                .password("password123")
                .role(User.Role.ADMIN)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.email", is("admin@test.com")))
                .andExpect(jsonPath("$.data.role", is("ADMIN")));
    }

    @Test
    @DisplayName("Should return 400 when registering with duplicate email")
    void testRegisterDuplicateEmail() throws Exception {
        AuthDTO.RegisterRequest request = AuthDTO.RegisterRequest.builder()
                .name("Test User")
                .email("duplicate@test.com")
                .password("password123")
                .role(User.Role.WARDEN)
                .build();

        // First registration
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Duplicate registration
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Should login successfully and return JWT token")
    void testLoginSuccess() throws Exception {
        // Register first
        AuthDTO.RegisterRequest registerReq = AuthDTO.RegisterRequest.builder()
                .name("Login User")
                .email("login@test.com")
                .password("password123")
                .role(User.Role.WARDEN)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        // Now login
        AuthDTO.LoginRequest loginReq = new AuthDTO.LoginRequest("login@test.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.tokenType", is("Bearer")));
    }

    @Test
    @DisplayName("Should return 401 with wrong password")
    void testLoginWrongPassword() throws Exception {
        // Register first
        AuthDTO.RegisterRequest registerReq = AuthDTO.RegisterRequest.builder()
                .name("User")
                .email("user@test.com")
                .password("correctpassword")
                .role(User.Role.STUDENT)
                .build();
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        // Wrong password
        AuthDTO.LoginRequest loginReq = new AuthDTO.LoginRequest("user@test.com", "wrongpassword");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Should return 400 when email is invalid format")
    void testRegisterInvalidEmail() throws Exception {
        AuthDTO.RegisterRequest request = AuthDTO.RegisterRequest.builder()
                .name("User")
                .email("not-an-email")
                .password("password123")
                .role(User.Role.STUDENT)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.email", notNullValue()));
    }
}
