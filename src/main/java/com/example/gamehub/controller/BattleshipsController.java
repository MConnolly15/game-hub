package com.example.gamehub.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class BattleshipsController {

    @GetMapping("/game/battleships")
    public String showGame() {
        return "battleships";
    }

}
