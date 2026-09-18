package com.example.gamehub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class RocketRushController {

    @GetMapping("/game/rocket-rush")
    public String rocketRush(){
        return "rocket-rush";
    }
}
