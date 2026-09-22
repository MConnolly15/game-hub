package com.example.gamehub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.gamehub.model.User;
import com.example.gamehub.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

// Turns on Mockito's machinery for this test class and without it,
// @Mock wouldn't actually create anything
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;

  // happy paths:
  @Test
  void successfullyUpdatesUsername() {
    // creating a user object
    User existingUser = new User("oldUsername", "daphna@example.com", "hashedPassword");
    // when using the email to search give me back the full above details for user,
    // this simulates the db finding a user
    when(userRepository.findByEmail("daphna@example.com"))
        .thenReturn(java.util.Optional.of(existingUser));
    // creates a class under testing handling the fake repo via the constructor (like
    // AccountController)
    UserService userService = new UserService(userRepository, passwordEncoder);
    userService.updateUsername("daphna@example.com", "newUsername");
    // updated the username:
    assertEquals("newUsername", existingUser.getUsername());
    verify(userRepository).save(existingUser);
  }

  @Test
  void successfullyUpdatesEmail() {
    User existingUser = new User("daphna", "old@example.com", "hashedPassword");
    when(userRepository.findByEmail("old@example.com"))
        .thenReturn(java.util.Optional.of(existingUser));
    UserService userService = new UserService(userRepository, passwordEncoder);
    userService.updateEmail("old@example.com", "new@example.com");

    assertEquals("new@example.com", existingUser.getEmail());
    verify(userRepository).save(existingUser);
  }

  @Test
  void successfullyUpdatesPassword() {
    User existingUser = new User("daphna", "daphna@example.com", "oldHashedPassword");
    when(userRepository.findByEmail("daphna@example.com"))
        .thenReturn(java.util.Optional.of(existingUser));
    when(passwordEncoder.encode("newPass1")).thenReturn("encodedNewPass1");
    UserService userService = new UserService(userRepository, passwordEncoder);
    // find the user by this email, then update their password to this new value
    userService.updatePassword("daphna@example.com", "newPass1");
    assertEquals("encodedNewPass1", existingUser.getPassword());
    verify(userRepository).save(existingUser);
  }

  // unhappy paths?:
  // in these we do not need the line with User existingUser because we never reach this point, if
  // it fails for being
  // empty, too short etc it never looks for the user.

  @Test
  void rejectsBlankUsername() {

    // creating instance of the user service:
    UserService userService = new UserService(userRepository, passwordEncoder);
    // we expect to throw an IllegalArgumentException when we call updateUsername with a blank
    // username:
    assertThrows(
        IllegalArgumentException.class, () -> userService.updateUsername("daphna@example.com", ""));
  }

  @Test
  void rejectsUsernameThatIsTooShort() {
    UserService userService = new UserService(userRepository, passwordEncoder);

    assertThrows(
        IllegalArgumentException.class,
        () -> userService.updateUsername("daphna@example.com", "da"));
  }

  @Test
  void rejectsUsernameThatIsTooLong() {
    UserService userService = new UserService(userRepository, passwordEncoder);
    assertThrows(
        IllegalArgumentException.class,
        () -> userService.updateUsername("daphna@example.com", "daaaaaaaaaaaaaaaa"));
  }

  @Test
  void rejectsDisallowedChars() {
    UserService userService = new UserService(userRepository, passwordEncoder);
    assertThrows(
        IllegalArgumentException.class,
        () -> userService.updateUsername("daphna@example.com", "daphna!"));
  }

  @Test
  void rejectsBlankEmail() {
    UserService userService = new UserService(userRepository, passwordEncoder);
    assertThrows(
        IllegalArgumentException.class, () -> userService.updateEmail("daphna@example.com", ""));
  }

  @Test
  void rejectsEmailMissingAtSign() {
    UserService userService = new UserService(userRepository, passwordEncoder);
    assertThrows(
        IllegalArgumentException.class,
        () -> userService.updateEmail("daphna@example.com", "daphnaexample.com"));
  }

  @Test
  void rejectsEmailWithAtSignButNoEndOfEmail() {
    UserService userService = new UserService(userRepository, passwordEncoder);
    assertThrows(
        IllegalArgumentException.class,
        () -> userService.updateEmail("daphna@example.com", "@daphnaexample"));
  }

  @Test
  void rejectsPasswordUnder8Chars() {
    UserService userService = new UserService(userRepository, passwordEncoder);
    assertThrows(
        IllegalArgumentException.class,
        () -> userService.updatePassword("daphna@example.com", "hello12"));
  }

  @Test
  void rejectsEmptyPassword() {
    UserService userService = new UserService(userRepository, passwordEncoder);
    assertThrows(
        IllegalArgumentException.class, () -> userService.updatePassword("daphna@example.com", ""));
  }

  @Test
  void rejectsPasswordOver16Chars() {
    UserService userService = new UserService(userRepository, passwordEncoder);
    assertThrows(
        IllegalArgumentException.class,
        () -> userService.updatePassword("daphna@example.com", "hellohellohello12"));
  }

  // to test multiple parameters:
  @ParameterizedTest
  // ran once for letters, once for nums
  @ValueSource(strings = {"hellohellohello", "12345678"})
  // method takes invalid password as a paremetr JUnit fills this in with the next value from the
  // list above
  void rejectsPasswordMissingLetterOrDigit(String invalidPassword) {
    UserService userService = new UserService(userRepository, passwordEncoder);
    assertThrows(
        IllegalArgumentException.class,
        () -> userService.updatePassword("daphna@example.com", invalidPassword));
  }

  @Test
  // test to check that the password is not saved as is but is encoded when saved.
  void hashedPasswordBeforeSaving() {
    // user object created as usual representing a user that already exsists
    User existingUser = new User("daphna", "daphna@example.com", "oldHashedPassword");
    // find user by email like before:
    when(userRepository.findByEmail("daphna@example.com"))
        .thenReturn(java.util.Optional.of(existingUser));
    // encode the raw string newPassword1 hand back in encodedNewPassword1, simulates real hashing:
    when(passwordEncoder.encode("newPassword1")).thenReturn("encodedNewPassword1");
    // this creates the actual class under test, now handing it two fake dependencies via the
    // constructor
    // the repository (to find/save users)and the encoder (to scramble the password)
    UserService userService = new UserService(userRepository, passwordEncoder);
    // method is called and newPassword1 has the new raw value saved to it
    userService.updatePassword("daphna@example.com", "newPassword1");
    // password is encoded so this is to deal with it
    assertEquals("encodedNewPassword1", existingUser.getPassword());
  }
}
