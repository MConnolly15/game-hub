package com.example.gamehub.service;

import com.example.gamehub.model.User;
import com.example.gamehub.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public void updateUsername(String email, String newUsername) {
    if (newUsername.isBlank()) {
      throw new IllegalArgumentException("Username cannot be blank");
    } else if (newUsername.length() < 3) {
      throw new IllegalArgumentException("Username cannot be shorter than 3 characters");
    } else if (newUsername.length() > 16) {
      throw new IllegalArgumentException("Username cannot be longer than 16 characters");
    } else if (!newUsername.matches("[A-Za-z0-9_]+")) {
      throw new IllegalArgumentException(
          "Username can't contain special characters apart from an underscore");
    }
    User user = userRepository.findByEmail(email).orElseThrow();
    user.setUsername(newUsername);
    userRepository.save(user);
  }

  public void updateEmail(String currentEmail, String newEmail) {
    if (newEmail.isBlank()) {
      throw new IllegalArgumentException("Email cannot be blank!");
      // regex to cover all email requirements (text before ,@, e.g. ".com)
    } else if (!newEmail.matches("^[^@]+@[^@]+\\.[^@]+$")) {
      throw new IllegalArgumentException("Email must be a valid email address!");
    }
    User user = userRepository.findByEmail(currentEmail).orElseThrow();
    user.setEmail(newEmail);
    userRepository.save(user);
  }

  public void updatePassword(String email, String newPassword) {
    if (newPassword.isBlank()) {
      throw new IllegalArgumentException("Password cannot be blank!");
    } else if (newPassword.length() < 8) {
      throw new IllegalArgumentException("Password cannot be shorter than 8 characters");
    } else if (newPassword.length() > 16) {
      throw new IllegalArgumentException("Password cannot be longer than 16 characters");
    } else if (!newPassword.matches("(?=.*[A-Za-z])(?=.*\\d).+")) {
      throw new IllegalArgumentException(
          "Password must contain at least one letter and one number");
    }
    User user = userRepository.findByEmail(email).orElseThrow();
    String hashedPassword = passwordEncoder.encode(newPassword);
    user.setPassword(hashedPassword);
    userRepository.save(user);
  }
}
