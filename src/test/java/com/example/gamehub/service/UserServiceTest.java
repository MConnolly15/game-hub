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

    @Test
    void successfullyUpdatesEmail() {
        User existingUser = new User("daphna", "old@example.com", "hashedPassword");
        when(userRepository.findByEmail("old@example.com"))
                .thenReturn(java.util.Optional.of(existingUser));
        UserService userService = new UserService(userRepository);
        userService.updateEmail("old@example.com", "new@example.com");

        assertEquals("new@example.com", existingUser.getEmail());
    }

    @Test
    void successfullyUpdatesPassword() {
        User existingUser = new User("daphna","daphna@example.com", "oldHashedPassword");
        when(userRepository.findByEmail("daphna@example.com"))
                .thenReturn(java.util.Optional.of(existingUser));
        UserService userService = new UserService(userRepository);
        //find the user by this email, then update their password to this new value
        userService.updatePassword("daphna@example.com", "newHashedPassword");
        assertEquals("newHashedPassword", existingUser.getPassword());

    }
}