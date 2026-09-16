package com.example.gamehub.controller;

import com.example.gamehub.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TeamController {
  private final UserRepository userRepository;

  public TeamController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/team")
  public ModelAndView teamPage(Authentication authentication) {

    ModelAndView modelAndView = new ModelAndView("teampage");

    String email = authentication.getName();
    userRepository
        .findByEmail(email)
        .ifPresent(user -> modelAndView.addObject("username", user.getUsername()));

    return modelAndView;
  }
}
