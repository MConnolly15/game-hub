package com.example.gamehub.controller;

import com.example.gamehub.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RocketRushController {

  private final UserRepository userRepository;

  public RocketRushController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/game/rocket-rush")
  public String rocketRush(Model model, Authentication authentication) {

    if (authentication != null && authentication.isAuthenticated()) {

      String email = authentication.getName();

      userRepository
          .findByEmail(email)
          .ifPresent(user -> model.addAttribute("username", user.getUsername()));
    }

    return "rocket-rush";
  }
}
