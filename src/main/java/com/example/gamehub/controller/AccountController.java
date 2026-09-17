package com.example.gamehub.controller;

import com.example.gamehub.model.User;
import com.example.gamehub.repository.UserRepository;
import com.example.gamehub.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
@Controller
public class AccountController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserService userService;

  public AccountController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.userService = userService;
  }

    @GetMapping("/register")
  public String showRegisterPage() {
    return "register";
  }

  @PostMapping("/register")
  public String registerUser(
      @RequestParam String username,
      @RequestParam String email,
      @RequestParam String password,
      Model model) {

    if (userRepository.existsByUsernameIgnoreCase(username)) {
      model.addAttribute("usernameError", "Username already taken.");
      return "register";
    }
    String hashedPassword = passwordEncoder.encode(password);

    User user = new User(username, email, hashedPassword);

    userRepository.save(user);

    return "redirect:/login";
  }

  @GetMapping("/profile")
  public String showProfilePage(){
    return "profile";
  }

  @PostMapping("/profile")
  public String updateProfile(
          @RequestParam String username,
          @RequestParam String email,
          @RequestParam String password,
          Authentication authentication) {

    String loggedInUserEmail = authentication.getName();
    userService.updateUsername(loggedInUserEmail, username);

    return "redirect:/profile";
  }
}
