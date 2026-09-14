package com.example.gamehub.controller;

import com.example.gamehub.model.User;
import com.example.gamehub.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AccountController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping("/register")
  public String showRegisterPage() {
    return "register";
  }

  @PostMapping("/register")
  public String registerUser(
      @RequestParam String name, @RequestParam String email, @RequestParam String password) {

    String hashedPassword = passwordEncoder.encode(password);

    User user = new User(name, email, hashedPassword);

    userRepository.save(user);

    return "redirect:/login";
  }
}
