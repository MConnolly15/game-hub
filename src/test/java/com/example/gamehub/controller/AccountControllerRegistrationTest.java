package com.example.gamehub.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.gamehub.config.SecurityConfiguration;
import com.example.gamehub.model.User;
import com.example.gamehub.repository.UserRepository;
import com.example.gamehub.service.CustomUserDetailsService;
import com.example.gamehub.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.ArgumentCaptor;

@WebMvcTest(AccountController.class)
@Import(SecurityConfiguration.class)
class AccountControllerRegistrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // Tests successful registration:
    // if the username is available, the user is saved and redirected to /login.
    @Test
    void userCanRegisterSuccessfully() throws Exception {

        when(userRepository.existsByUsernameIgnoreCase("Michael"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("hashedPassword");

        mockMvc
                .perform(
                        post("/register")
                                .with(csrf())
                                .param("username", "Michael")
                                .param("email", "michael@example.com")
                                .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userRepository)
                .save(org.mockito.ArgumentMatchers.any(User.class));
    }

    // Tests that registration is rejected when the username is already taken.
    @Test
    void duplicateUsernameCannotRegister() throws Exception {

        when(userRepository.existsByUsernameIgnoreCase("Michael"))
                .thenReturn(true);

        mockMvc
                .perform(
                        post("/register")
                                .with(csrf())
                                .param("username", "Michael")
                                .param("email", "michael@example.com")
                                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("usernameError", "Username already taken."));

        verify(userRepository, never())
                .save(org.mockito.ArgumentMatchers.any(User.class));
    }

    // Tests that the user's plain password is hashed before being saved.
    @Test
    void passwordIsHashedBeforeUserIsSaved() throws Exception {

        when(userRepository.existsByUsernameIgnoreCase("Michael"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("hashedPassword");

        mockMvc
                .perform(
                        post("/register")
                                .with(csrf())
                                .param("username", "Michael")
                                .param("email", "michael@example.com")
                                .param("password", "password123"))
                .andExpect(status().is3xxRedirection());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("hashedPassword", savedUser.getPassword());
    }
}