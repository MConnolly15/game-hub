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

  public AccountController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService  ) {
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
  //both automatically supplied by Spring for any request to show who is logged in:
  public String showProfilePage(Authentication authentication, Model model){
    String loggedInUserEmail = authentication.getName();
    //pulls the logged-in user's email out of the authentication object
    //looking up the user and if not fown throws an error
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
//attempt to run everything inside here,but if any line throws an exception
// immediately stop and jump to the matching catch block below skipping whatever was left
    try {
      userService.updateUsername(loggedInUserEmail, username);
      userService.updateEmail(loggedInUserEmail, email);
      userService.updatePassword(loggedInUserEmail, password);
    } catch (IllegalArgumentException exception) {
      model.addAttribute("usernameError", exception.getMessage());
      //the name of a view to render, then controller fetches it and renders it
      return "profile";
    }

    return "redirect:/profile";
  }
}
