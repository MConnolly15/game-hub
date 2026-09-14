package com.example.gamehub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomePageController {

  @RequestMapping("/")
  public RedirectView redirect() {
    return new RedirectView("/home");
  }
}
