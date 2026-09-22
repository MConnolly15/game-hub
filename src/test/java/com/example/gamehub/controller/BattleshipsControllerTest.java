package com.example.gamehub.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.gamehub.model.BattleshipGame;
import com.example.gamehub.model.CellState;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

public class BattleshipsControllerTest {

  @Test
  void showGameCreatesNewGameWhenSessionHasNoGame() {
    BattleshipsController controller = new BattleshipsController();

    MockHttpSession session = new MockHttpSession();
    Model model = new ConcurrentModel();

    String view = controller.showGame(session, model);

    assertEquals("battleships", view);
    assertNotNull(session.getAttribute("battleshipGame"));
  }

  @Test
  void showGameUsesExistingGameFromSession() {
    BattleshipsController controller = new BattleshipsController();

    MockHttpSession session = new MockHttpSession();
    Model model = new ConcurrentModel();

    BattleshipGame existingGame = new BattleshipGame();
    session.setAttribute("battleshipGame", existingGame);

    String view = controller.showGame(session, model);

    assertEquals("battleships", view);
    assertEquals(existingGame, session.getAttribute("battleshipGame"));
  }

  @Test
  void resetCreatesANewGame() {
    BattleshipsController controller = new BattleshipsController();

    MockHttpSession session = new MockHttpSession();

    BattleshipGame oldGame = new BattleshipGame();
    session.setAttribute("battleshipGame", oldGame);

    String view = controller.reset(session);

    BattleshipGame newGame = (BattleshipGame) session.getAttribute("battleshipGame");

    assertEquals("redirect:/game/battleships", view);
    assertNotNull(newGame);
    assertNotEquals(oldGame, newGame);
  }

  @Test
  void fireAllowsPlayerToFireAtComputerBoard() {
    BattleshipsController controller = new BattleshipsController();

    MockHttpSession session = new MockHttpSession();
    BattleshipGame game = new BattleshipGame();
    session.setAttribute("battleshipGame", game);

    String view = controller.fire(2, 2, session);

    assertEquals("redirect:/game/battleships#battleships-game", view);

    assertNotEquals(CellState.WATER, game.getComputerBoard().getGrid()[2][2]);
  }

  @Test
  void placeShipAllowsValidShipPlacement() {
    BattleshipsController controller = new BattleshipsController();

    MockHttpSession session = new MockHttpSession();
    BattleshipGame game = new BattleshipGame();
    session.setAttribute("battleshipGame", game);

    String view = controller.placeShip(0, 0, true, session);

    assertEquals("redirect:/game/battleships#battleships-game", view);

    assertNull(session.getAttribute("placementError"));
    assertNull(session.getAttribute("selectedOrientation"));
  }

  @Test
  void placeShipShowsErrorForInvalidPlacement() {
    BattleshipsController controller = new BattleshipsController();

    MockHttpSession session = new MockHttpSession();
    BattleshipGame game = new BattleshipGame();
    session.setAttribute("battleshipGame", game);

    String view = controller.placeShip(0, 4, true, session);

    assertEquals("redirect:/game/battleships#battleships-game", view);

    assertEquals(
        "Invalid placement - the ship must fit on the board and cannot overlap another ship.",
        session.getAttribute("placementError"));

    assertEquals(true, session.getAttribute("selectedOrientation"));
  }
}
