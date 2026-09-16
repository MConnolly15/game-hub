package com.example.gamehub.service;

import com.example.gamehub.model.User;
import com.example.gamehub.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
//Turns on Mockito's machinery for this test class and without it,
// @Mock wouldn't actually create anything
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void successfullyUpdatesUsername() {
        //creating a user object
        User existingUser = new User("oldUsername", "daphna@example.com", "hashedPassword");
        //when using the email to search give me back the full above details for user,
        // this simulates the db finding a user
        when(userRepository.findByEmail("daphna@example.com"))
                .thenReturn(java.util.Optional.of(existingUser));
        //creates a class under testing handling the fake repo via the constructor (like AccountController)
        UserService userService = new UserService(userRepository);
        userService.updateUsername("daphna@example.com", "newUsername");
        //updated the username:
        assertEquals("newUsername", existingUser.getUsername());
    }
}