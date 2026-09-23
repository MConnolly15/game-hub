package com.example.gamehub.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// this will allow me to use status() etc later down the line instead of the longer name
@WebMvcTest(HomePageController.class)
@Import(SecurityConfiguration.class)
// checks that the navbar shows the right links depending on if you're logged in or not
class HomePageControllerNavbarTest {

  @Autowired private MockMvc mockMvc;

  // give me a fake userRepo, same as the other controller tests
  @MockitoBean private UserRepository userRepository;

  // needed even though I'm not testing login itself, SecurityConfiguration expects this bean to
  // exist or the context won't start
  @MockitoBean private CustomUserDetailsService customUserDetailsService;

  // by not using @WithMockUser essentially I am not a signed in user, so I should get bounced
  // straight to the login page and never actually see the homepage or its navbar at all
  @Test
  void loggedOutUserIsRedirectedToLogin() throws Exception {
    mockMvc
        .perform(get("/"))
        // checks that this is a redirect
        .andExpect(status().is3xxRedirection())
        // as this is not a real server listening, no need for port in this case
        .andExpect(redirectedUrl("/login"));
  }

  // tells it to run this as a logged in user, so this time I should see Profile and Logout, and
  // should NOT see Login or Register anywhere in the page
  @Test
  @WithMockUser(username = "daphna@example.com")
  void loggedInUserSeesProfileAndLogoutNotLoginOrRegister() throws Exception {
    User user = new User("daphna", "daphna@example.com", "hashedPassword");
    when(userRepository.findByEmail("daphna@example.com")).thenReturn(Optional.of(user));

    mockMvc
        .perform(get("/"))
        .andExpect(status().isOk())
        // checking the actual HTML that comes back, same idea as searching View Page Source
        // by hand
        .andExpect(content().string(containsString("href=\"/profile\"")))
        .andExpect(content().string(containsString("/logout")))
        .andExpect(content().string(not(containsString("href=\"/login\""))))
        .andExpect(content().string(not(containsString("href=\"/register\""))));
  }
}
