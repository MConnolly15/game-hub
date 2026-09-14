package com.example.gamehub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomePageController {

    @GetMapping("/")
    public ModelAndView homePage() {
        ModelAndView modelAndView = new ModelAndView("homepage");
        String user = "User";
        modelAndView.addObject("username", user);
        return modelAndView;
    }
}
