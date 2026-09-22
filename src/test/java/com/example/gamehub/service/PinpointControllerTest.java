package com.example.gamehub.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

import com.example.gamehub.model.User;
import com.example.gamehub.repository.UserRepository;
import com.example.gamehub.service.PinpointGame;
import com.example.gamehub.service.PinpointGameSession;
import com.example.gamehub.service.PinpointGameStatus;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.ModelAndView;

@ExtendWith(MockitoExtension.class)
class PinpointControllerTest {

  private static final String SESSION_KEY = "pinpointGameSession";

  @Mock private UserRepository userRepository;
  @Mock private HttpSession session;
  @Mock private Authentication authentication;

  private PinpointController controller;
  // sessionAttributes is standing for what's stored in the browser session, as the fake session
  // object still
  // needs something to stand behind it
  private final Map<String, Object> sessionAttributes = new HashMap<>();

  // runs before every test, wiring the fake session
  @BeforeEach
  void setUp() {
    sessionAttributes.clear();
    controller = new PinpointController(userRepository);

    lenient()
        .when(session.getAttribute(anyString()))
        .thenAnswer(inv -> sessionAttributes.get((String) inv.getArgument(0)));
    lenient()
        .doAnswer(
            inv -> {
              sessionAttributes.put(inv.getArgument(0), inv.getArgument(1));
              return null;
            })
        .when(session)
        .setAttribute(anyString(), any());
  }

  // seedSession() is a helper , instead of seeing the game with raw values, this is a real pinpoint
  // session object built, dropped into the mocked session
  private PinpointGameSession seedSession() {
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("Brick", "Stop sign", "Fire truck", "Rose", "Ketchup"));
    PinpointGameSession pinpointSession = new PinpointGameSession(game);
    sessionAttributes.put(SESSION_KEY, pinpointSession);
    return pinpointSession;
  }

  private void mockAuthenticatedUser(String email, String username) {
    org.mockito.Mockito.when(authentication.isAuthenticated()).thenReturn(true);
    org.mockito.Mockito.when(authentication.getName()).thenReturn(email);
    User user = new User();
    user.setUsername(username);
    org.mockito.Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
  }

  // ---------------------------------------------------------------------
  // GET /game/pinpoint
  // ---------------------------------------------------------------------
  // each Nested class group tests for one controller method
  @Nested
  @DisplayName("loadPage")
  class LoadPage {

    @Test
    @DisplayName("creates a fresh game when none exists in the session")
    void createsFreshGameWhenNoneExists() {
      ModelAndView mv = controller.loadPage(session, null);

      assertThat(mv.getViewName()).isEqualTo("pinpoint");
      assertThat(mv.getModel().get("cluesRevealed")).isEqualTo(1);
      assertThat(mv.getModel().get("guessesMade")).isEqualTo(0);
      assertThat(mv.getModel().get("status")).isEqualTo(PinpointGameStatus.IN_PROGRESS);
      // side effect: a session was created and stored
      assertThat(sessionAttributes.get(SESSION_KEY)).isNotNull();
    }

    @Test
    @DisplayName("reuses the existing game session already stored in the session")
    void reusesExistingSession() {
      PinpointGameSession seeded = seedSession();
      seeded.submitGuess("banana"); // one wrong guess, so state isn't the fresh default

      ModelAndView mv = controller.loadPage(session, null);

      assertThat(mv.getModel().get("cluesRevealed")).isEqualTo(2);
      assertThat(mv.getModel().get("guessesMade")).isEqualTo(1);
    }

    @Test
    @DisplayName("adds the username to the model when authenticated")
    void addsUsernameWhenAuthenticated() {
      mockAuthenticatedUser("player@example.com", "player1");

      ModelAndView mv = controller.loadPage(session, authentication);

      assertThat(mv.getModel().get("username")).isEqualTo("player1");
    }

    @Test
    @DisplayName("does not add a username when authentication is null")
    void noUsernameWhenAuthenticationNull() {
      ModelAndView mv = controller.loadPage(session, null);

      assertThat(mv.getModel().get("username")).isNull();
    }
  }

  // ---------------------------------------------------------------------
  // POST /game/pinpoint/guess
  // ---------------------------------------------------------------------

  @Nested
  @DisplayName("submitGuess")
  class SubmitGuess {

    @Test
    @DisplayName("a correct guess wins the game and adds a win message")
    void correctGuessWinsGame() {
      seedSession();

      ModelAndView mv = controller.submitGuess("things that are red", session, null);

      assertThat(mv.getModel().get("status")).isEqualTo(PinpointGameStatus.WON);
      assertThat(mv.getModel().get("message")).isNotNull();
    }

    @Test
    @DisplayName("an incorrect guess reveals the next clue and increases the guess count")
    void incorrectGuessRevealsNextClue() {
      seedSession();

      ModelAndView mv = controller.submitGuess("banana", session, null);

      assertThat(mv.getModel().get("cluesRevealed")).isEqualTo(2);
      assertThat(mv.getModel().get("guessesMade")).isEqualTo(1);
      assertThat(mv.getModel().get("message")).isNull();
    }

    @Test
    @DisplayName("a blank guess adds an error and does not change guess count")
    void blankGuessAddsError() {
      seedSession();

      ModelAndView mv = controller.submitGuess("   ", session, null);

      assertThat(mv.getModel().get("error")).isNotNull();
      assertThat(mv.getModel().get("guessesMade")).isEqualTo(0);
    }

    @Test
    @DisplayName("guessing again after the game has already ended adds an error")
    void guessingAfterGameEndedAddsError() {
      PinpointGameSession seeded = seedSession();
      seeded.submitGuess("things that are red"); // wins the game

      ModelAndView mv = controller.submitGuess("anything", session, null);

      assertThat(mv.getModel().get("error")).isNotNull();
      assertThat(mv.getModel().get("status")).isEqualTo(PinpointGameStatus.WON);
    }

    @Test
    @DisplayName("keeps the username in the model on a guess submission")
    void keepsUsernameOnSubmit() {
      seedSession();
      mockAuthenticatedUser("player@example.com", "player1");

      ModelAndView mv = controller.submitGuess("banana", session, authentication);

      assertThat(mv.getModel().get("username")).isEqualTo("player1");
    }
  }

  // ---------------------------------------------------------------------
  // POST /game/pinpoint/reset
  // ---------------------------------------------------------------------

  @Nested
  @DisplayName("resetGame")
  class ResetGame {

    @Test
    @DisplayName("replaces the existing session with a fresh game")
    void replacesExistingSessionWithFreshGame() {
      PinpointGameSession seeded = seedSession();
      seeded.submitGuess("banana"); // dirty the state first

      ModelAndView mv = controller.resetGame(session, null);

      assertThat(mv.getModel().get("cluesRevealed")).isEqualTo(1);
      assertThat(mv.getModel().get("guessesMade")).isEqualTo(0);
      assertThat(mv.getModel().get("status")).isEqualTo(PinpointGameStatus.IN_PROGRESS);
      // side effect: the session attribute was overwritten with a new session
      // isNotSameAs(seeded) is the reet test, checks that is really a different object, not just
      // one with the same values,
      // proving that rest game actually created a new session
      assertThat(sessionAttributes.get(SESSION_KEY)).isNotSameAs(seeded);
    }

    @Test
    @DisplayName("adds the username to the model when authenticated")
    void addsUsernameWhenAuthenticated() {
      mockAuthenticatedUser("player@example.com", "player1");

      ModelAndView mv = controller.resetGame(session, authentication);

      assertThat(mv.getModel().get("username")).isEqualTo("player1");
    }
  }
}
