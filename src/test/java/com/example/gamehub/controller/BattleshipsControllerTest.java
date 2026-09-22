package com.example.gamehub.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.gamehub.model.BattleshipGame;
import com.example.gamehub.model.CellState;
import com.example.gamehub.model.User;
import com.example.gamehub.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

public class BattleshipsControllerTest {

  @Test
  void showGameCreatesNewGameWhenSessionHasNoGame() {
    UserRepository userRepository = mock(UserRepository.class);
    BattleshipsController controller = new BattleshipsController(userRepository);

    MockHttpSession session = new MockHttpSession();
    Model model = new ConcurrentModel();

    String view = controller.showGame(session, model, null);

    assertEquals("battleships", view);
    assertNotNull(session.getAttribute("battleshipGame"));
  }

  @Test
  void showGameUsesExistingGameFromSession() {
    UserRepository userRepository = mock(UserRepository.class);
    BattleshipsController controller = new BattleshipsController(userRepository);

    MockHttpSession session = new MockHttpSession();
    Model model = new ConcurrentModel();

    BattleshipGame existingGame = new BattleshipGame();
    session.setAttribute("battleshipGame", existingGame);

    String view = controller.showGame(session, model, null);

    assertEquals("battleships", view);
    assertEquals(existingGame, session.getAttribute("battleshipGame"));
  }

  @Test
  void resetCreatesANewGame() {
    UserRepository userRepository = mock(UserRepository.class);
    BattleshipsController controller = new BattleshipsController(userRepository);

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
    UserRepository userRepository = mock(UserRepository.class);
    BattleshipsController controller = new BattleshipsController(userRepository);

    MockHttpSession session = new MockHttpSession();
    BattleshipGame game = new BattleshipGame();
    session.setAttribute("battleshipGame", game);

    String view = controller.fire(2, 2, session);

    assertEquals("redirect:/game/battleships#battleships-game", view);

    assertNotEquals(CellState.WATER, game.getComputerBoard().getGrid()[2][2]);
  }

  @Test
  void placeShipAllowsValidShipPlacement() {
    UserRepository userRepository = mock(UserRepository.class);
    BattleshipsController controller = new BattleshipsController(userRepository);

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
    UserRepository userRepository = mock(UserRepository.class);
    BattleshipsController controller = new BattleshipsController(userRepository);

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

  @Test
  void showGameAddsUsernameToModel() {
    UserRepository userRepository = mock(UserRepository.class);
    Authentication authentication = mock(Authentication.class);

    User user = new User("cquinn22", "test@example.com", "password");

    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn("test@example.com");
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

    BattleshipsController controller = new BattleshipsController(userRepository);

    MockHttpSession session = new MockHttpSession();
    Model model = new ConcurrentModel();

    String view = controller.showGame(session, model, authentication);

    assertEquals("battleships", view);
    assertEquals("cquinn22", model.getAttribute("username"));
  }
}
