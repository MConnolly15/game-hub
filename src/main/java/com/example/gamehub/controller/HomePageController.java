package com.example.gamehub.controller;

import com.example.gamehub.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomePageController {

  //giving controller access to users table
  private final UserRepository userRepository;

  //automatically passes the UserRepository into this controller
  public HomePageController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }


  @GetMapping("/")
  public ModelAndView homePage( Authentication authentication) {
    ModelAndView modelAndView = new ModelAndView("homepage");

    // get the email of the person who is logged in
    String email = authentication.getName();

    //Find that user in the database
    userRepository
            .findByEmail(email)

            // If we find them, send their username to the homepage
            .ifPresent(
                    user ->
                            modelAndView.addObject(
                                    "username",
                                    user.getUsername()));
    // Show the homepage
    return modelAndView;
  }
}
