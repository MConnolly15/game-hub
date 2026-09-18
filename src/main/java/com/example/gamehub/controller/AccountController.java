package com.example.gamehub.controller;

import com.example.gamehub.model.User;
import com.example.gamehub.repository.UserRepository;
import com.example.gamehub.service.UserService;
import com.example.gamehub.service.CustomUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
public class AccountController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserService userService;
  private final CustomUserDetailsService customUserDetailsService;

  public AccountController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService, CustomUserDetailsService customUserDetailsService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.userService = userService;
    this.customUserDetailsService = customUserDetailsService;
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
  public String showProfilePage(Authentication authentication, Model model) {
    String loggedInUserEmail = authentication.getName();
    User user = userRepository.findByEmail(loggedInUserEmail).orElseThrow();
    model.addAttribute("username", user.getUsername());
    model.addAttribute("email", user.getEmail());
    return "profile";
  }

  @PostMapping("/profile")
  public String updateProfile(
          @RequestParam String username,
          @RequestParam String email,
          @RequestParam String password,
          Authentication authentication,
          Model model) {

    String loggedInUserEmail = authentication.getName();

    try {
      userService.updateUsername(loggedInUserEmail, username);
      userService.updateEmail(loggedInUserEmail, email);
      userService.updatePassword(email, password);
    } catch (IllegalArgumentException exception) {
      model.addAttribute("usernameError", exception.getMessage());
      return "profile";
    }

    UserDetails updatedUserDetails = customUserDetailsService.loadUserByUsername(email);
    Authentication newAuth = new UsernamePasswordAuthenticationToken(
            updatedUserDetails, authentication.getCredentials(), updatedUserDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(newAuth);

    return "redirect:/profile";
  }
}