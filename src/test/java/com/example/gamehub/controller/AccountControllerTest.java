package com.example.gamehub.controller;

import com.example.gamehub.config.SecurityConfiguration;
import com.example.gamehub.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.example.gamehub.service.UserService;

//This will allow me to use status() later down the line instead of longer name
@WebMvcTest(AccountController.class)
@Import(SecurityConfiguration.class)
//names the test class
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;
    //give me a fake userRepo
    @MockitoBean
    private UserRepository userRepository;
    //give me a fake password
    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserService userService;

    //fake web requests:

    @Test
    //tells it to run this
    @WithMockUser(username = "daphna")
    //test user is logged in for this test
    void loggedInUserCanAccessAccountPage() throws Exception {
        //names what this test checks
        mockMvc.perform(get("/profile"))
       //sends a fake request
                .andExpect(status().isOk());
    }

    @Test
    //by not using @WithMockUser essentially I am not a signed in user
    void unauthenticatedUserCannotAccessAccountPage() throws Exception {
        mockMvc.perform(get("/profile"))
                //checks that this is a redirect
                .andExpect(status().is3xxRedirection())
                //as this is not a real server listening, no need for port in this case
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(username = "daphna@example.com")
    void updatesTheLoggedInUsersOwnAccount() throws Exception {
        mockMvc.perform(post("/profile")
                        //Spring Security blocks post requests without a valid CSRF token by default,
                        //this tells MockMvc to attach a valid fake token so the request isn't rejected on that technicality
                        .with(csrf())
                        //simulates a submitted form field, same idea as a real browser POST:
                        .param("username", "newUsername")
                        .param("email", "daphna@example.com")
                        .param("password", "newPass1"))
                //this checks if after submission , were we redirect somewhere (like back to /profile)
                .andExpect(status().is3xxRedirection());
        //this checks that the method was actually called, controller only return values like "redirect:.."
        // and by doing the below I can see that the controller correctly delegated to the service.
        verify(userService).updateUsername("daphna@example.com", "newUsername");
    }
}