package com.example.gamehub.controller;

import com.example.gamehub.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FaqController {
  private final UserRepository userRepository;

  public FaqController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/faq")
  public ModelAndView faqPage(Authentication authentication) {

    ModelAndView modelAndView = new ModelAndView("faqpage");

    String email = authentication.getName();
    userRepository
        .findByEmail(email)
        .ifPresent(user -> modelAndView.addObject("username", user.getUsername()));

    return modelAndView;
  }
}
