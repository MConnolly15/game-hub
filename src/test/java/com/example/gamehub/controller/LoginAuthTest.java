package com.example.gamehub.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.gamehub.config.SecurityConfiguration;
import com.example.gamehub.model.User;
import com.example.gamehub.repository.UserRepository;
import com.example.gamehub.service.CustomUserDetailsService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LoginController.class)
@Import({SecurityConfiguration.class, CustomUserDetailsService.class})
class LoginAuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserRepository userRepository;

    // Tests that a user can successfully log in using their email and password.
    @Test
    void userCanLoginSuccessfully() throws Exception {

        User user =
                new User(
                        "Michael",
                        "michael@example.com",
                        passwordEncoder.encode("password123"));

        when(userRepository.findByEmail("michael@example.com"))
                .thenReturn(Optional.of(user));

        mockMvc
                .perform(
                        formLogin("/login")
                                .user("michael@example.com")
                                .password("password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withUsername("michael@example.com"));
    }

    // Tests that a user cannot log in with the wrong password.
    @Test
    void userCannotLoginWithWrongPassword() throws Exception {

        User user =
                new User(
                        "Michael",
                        "michael@example.com",
                        passwordEncoder.encode("password123"));

        when(userRepository.findByEmail("michael@example.com"))
                .thenReturn(Optional.of(user));

        mockMvc
                .perform(
                        formLogin("/login")
                                .user("michael@example.com")
                                .password("wrongPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(unauthenticated());
    }

    // Tests that a logged-in user can log out and is no longer authenticated.
    @Test
    @WithMockUser(username = "michael@example.com")
    void userCanLogoutSuccessfully() throws Exception {

        mockMvc
                .perform(logout())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"))
                .andExpect(unauthenticated());
    }
}