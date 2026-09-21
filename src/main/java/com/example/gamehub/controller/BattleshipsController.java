package com.example.gamehub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.gamehub.model.BattleshipGame;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BattleshipsController {

    @GetMapping("/game/battleships")
    public String showGame(HttpSession session, Model model) {

        BattleshipGame game =
                (BattleshipGame) session.getAttribute("battleshipGame");

        if (game == null) {
            game = new BattleshipGame();
            session.setAttribute("battleshipGame", game);
        }

        model.addAttribute("playerBoard", game.getPlayerBoard().getGrid());
        model.addAttribute("computerBoard", game.getComputerBoard().getGrid());
        model.addAttribute("winner", game.getWinner());
        model.addAttribute("shipLengthToPlace", game.getShipLengthToPlace());
        model.addAttribute("placementError",
                session.getAttribute("placementError"));
        model.addAttribute("selectedOrientation",
                session.getAttribute("selectedOrientation"));

        return "battleships";
    }

    @PostMapping("/game/battleships/fire")
    public String fire(
            @RequestParam int row,
            @RequestParam int column,
            HttpSession session) {

        BattleshipGame game =
                (BattleshipGame) session.getAttribute("battleshipGame");

        game.playerFire(row, column);

        if (game.getWinner() == null) {
            game.computerTakeTurn();
        }

        return "redirect:/game/battleships#battleships-game";
    }

    @PostMapping("/game/battleships/reset")
    public String reset(HttpSession session) {

        BattleshipGame game = new BattleshipGame();

        session.setAttribute("battleshipGame", game);

        return "redirect:/game/battleships";
    }

    @PostMapping("/game/battleships/place")
    public String placeShip(
            @RequestParam int row,
            @RequestParam int column,
            @RequestParam boolean horizontal,
            HttpSession session) {

        BattleshipGame game =
                (BattleshipGame) session.getAttribute("battleshipGame");

        try {
            game.placePlayerShip(row, column, horizontal);

            session.removeAttribute("placementError");
            session.removeAttribute("selectedOrientation");

        } catch (IllegalArgumentException e) {

            session.setAttribute(
                    "placementError",
                    "Invalid placement - the ship must fit on the board and cannot overlap another ship."
            );

            session.setAttribute("selectedOrientation", horizontal);
        }

        return "redirect:/game/battleships#battleships-game";
    }

}