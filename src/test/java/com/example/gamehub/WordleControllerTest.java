package com.example.gamehub.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

import com.example.gamehub.model.User;
import com.example.gamehub.repository.UserRepository;
// NOTE: adjust this import to match wherever your User entity actually lives
// (e.g. com.example.gamehub.entity.User) if it differs in your project.
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
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

/**
 * Unit tests for {@link WordleController}.
 *
 * <p>These tests exercise the controller in isolation (no Spring context is loaded). The {@link
 * HttpSession} is a Mockito mock backed by a real {@link HashMap} so that
 * getAttribute/setAttribute/removeAttribute behave like a real session, which lets us both observe
 * what the controller stores and pre-seed state (e.g. force a specific target word) for
 * deterministic assertions.
 */
@ExtendWith(MockitoExtension.class)
class WordleControllerTest {

  private static final int MAX_ATTEMPTS = 6;
  private static final List<String> WORD_LIST =
      List.of(
          "APPLE", "BRAVE", "CRANE", "DOUBT", "EAGLE", "FAITH", "GHOST", "HOUSE", "IRONY", "JOKER",
          "KNEEL", "LEMON", "MONEY", "NOBLE", "OCEAN", "PLANT", "QUIET", "RIVER", "STONE", "TIGER");

  @Mock private UserRepository userRepository;
  @Mock private HttpSession session;
  @Mock private Authentication authentication;

  private WordleController controller;
  private final Map<String, Object> sessionAttributes = new HashMap<>();

  @BeforeEach
  void setUp() {
    sessionAttributes.clear();
    controller = new WordleController(userRepository);

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
    lenient()
        .doAnswer(
            inv -> {
              sessionAttributes.remove((String) inv.getArgument(0));
              return null;
            })
        .when(session)
        .removeAttribute(anyString());
  }

  private void seedTarget(String target) {
    sessionAttributes.put("wordTarget", target);
  }

  private void seedGuesses(List<String> guesses) {
    sessionAttributes.put("guesses", new ArrayList<>(guesses));
  }

  private void mockAuthenticatedUser(String email, String username) {
    org.mockito.Mockito.when(authentication.isAuthenticated()).thenReturn(true);
    org.mockito.Mockito.when(authentication.getName()).thenReturn(email);
    User user = new User();
    user.setUsername(username);
    org.mockito.Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
  }

  // ---------------------------------------------------------------------
  // GET /game/wordle
  // ---------------------------------------------------------------------

  @Nested
  @DisplayName("loadPage")
  class LoadPage {

    @Test
    @DisplayName("creates a new target word when none exists in the session")
    void createsNewTargetWhenNoneExists() {
      ModelAndView mv = controller.loadPage(session, null);

      assertThat(mv.getViewName()).isEqualTo("wordle");
      String target = (String) mv.getModel().get("wordTarget");
      assertThat(target).isIn(WORD_LIST);
      assertThat(sessionAttributes.get("wordTarget")).isEqualTo(target);
    }

    @Test
    @DisplayName("reuses the existing target word already stored in the session")
    void reusesExistingTarget() {
      seedTarget("TIGER");

      ModelAndView mv = controller.loadPage(session, null);

      assertThat(mv.getModel().get("wordTarget")).isEqualTo("TIGER");
    }

    @Test
    @DisplayName("initializes an empty grid when there are no prior guesses")
    void initializesEmptyGridWithNoGuesses() {
      ModelAndView mv = controller.loadPage(session, null);

      Map<String, Object> model = mv.getModel();
      assertThat(model.get("row1_letter1")).isEqualTo("");
      assertThat(model.get("row1_letter1_status")).isEqualTo("");
      assertThat(model.get("row6_letter5")).isEqualTo("");
      assertThat(model.get("row6_letter5_status")).isEqualTo("");
    }

    @Test
    @DisplayName("populates the grid with prior guesses and their letter statuses")
    void populatesGridWithExistingGuesses() {
      seedTarget("APPLE");
      seedGuesses(List.of("BRAVE"));

      ModelAndView mv = controller.loadPage(session, null);

      Map<String, Object> model = mv.getModel();
      assertThat(model.get("row1_letter1")).isEqualTo("B");
      assertThat(model.get("row1_letter1_status")).isEqualTo("absent");
      // 'A' is in APPLE but not at guess position 2 -> present
      assertThat(model.get("row1_letter2")).isEqualTo("R");
      assertThat(model.get("row1_letter2_status")).isEqualTo("absent");
      assertThat(model.get("row1_letter3")).isEqualTo("A");
      assertThat(model.get("row1_letter3_status")).isEqualTo("present");
      assertThat(model.get("row1_letter4")).isEqualTo("V");
      assertThat(model.get("row1_letter4_status")).isEqualTo("absent");
      assertThat(model.get("row1_letter5")).isEqualTo("E");
      assertThat(model.get("row1_letter5_status")).isEqualTo("correct");
    }

    @Test
    @DisplayName("adds the username to the model when the user is authenticated")
    void addsUsernameWhenAuthenticated() {
      mockAuthenticatedUser("player@example.com", "player1");

      ModelAndView mv = controller.loadPage(session, authentication);

      assertThat(mv.getModel().get("username")).isEqualTo("player1");
    }

    @Test
    @DisplayName("does not add a username when authentication is null")
    void doesNotAddUsernameWhenAuthenticationNull() {
      ModelAndView mv = controller.loadPage(session, null);

      assertNull(mv.getModel().get("username"));
    }

    @Test
    @DisplayName("does not add a username when not authenticated")
    void doesNotAddUsernameWhenNotAuthenticated() {
      org.mockito.Mockito.when(authentication.isAuthenticated()).thenReturn(false);

      ModelAndView mv = controller.loadPage(session, authentication);

      assertNull(mv.getModel().get("username"));
    }
  }

  // ---------------------------------------------------------------------
  // POST /game/wordle/submit-guess
  // ---------------------------------------------------------------------

  @Nested
  @DisplayName("submitGuess")
  class SubmitGuess {

    @Test
    @DisplayName("rejects a guess that is not 5 letters and does not record it")
    void rejectsGuessWithWrongLength() {
      seedTarget("APPLE");

      ModelAndView mv = controller.submitGuess("AB", session, null);

      assertThat(mv.getModel().get("error")).isEqualTo("Guess must be 5 letters");
      assertThat(sessionAttributes.get("guesses")).isNull();
    }

    @Test
    @DisplayName("accepts a valid guess, uppercases it, and stores it in the session")
    void acceptsValidGuessAndUppercases() {
      seedTarget("APPLE");

      ModelAndView mv = controller.submitGuess("apple", session, null);

      assertThat(mv.getModel().get("userGuess")).isEqualTo("APPLE");
      @SuppressWarnings("unchecked")
      List<String> stored = (List<String>) sessionAttributes.get("guesses");
      assertThat(stored).containsExactly("APPLE");
      assertThat(mv.getModel().get("UserAttempts")).isEqualTo(1);
    }

    @Test
    @DisplayName("marks the game as won when the guess matches the target")
    void marksWonOnMatchingGuess() {
      seedTarget("APPLE");

      ModelAndView mv = controller.submitGuess("APPLE", session, null);

      assertThat(mv.getModel().get("won")).isEqualTo(true);
      assertThat(mv.getModel().get("message")).isEqualTo("You won! The word was APPLE");
      assertNull(mv.getModel().get("lost"));
    }

    @Test
    @DisplayName("marks the game as lost after the final non-matching attempt")
    void marksLostAfterFinalAttempt() {
      seedTarget("APPLE");
      seedGuesses(List.of("BRAVE", "CRANE", "DOUBT", "EAGLE", "FAITH"));

      ModelAndView mv = controller.submitGuess("GHOST", session, null);

      assertThat(mv.getModel().get("lost")).isEqualTo(true);
      assertThat(mv.getModel().get("message")).isEqualTo("Game over! The word was APPLE");
      assertThat(mv.getModel().get("UserAttempts")).isEqualTo(6);
    }

    @Test
    @DisplayName("rejects further guesses once max attempts have been used")
    void rejectsGuessWhenNoAttemptsRemain() {
      seedTarget("APPLE");
      seedGuesses(
          List.of("BRAVE", "CRANE", "DOUBT", "EAGLE", "FAITH", "GHOST")); // 6 used, none match

      ModelAndView mv = controller.submitGuess("HOUSE", session, null);

      assertThat(mv.getModel().get("error")).isEqualTo("No attempts remaining — start a new game");
      @SuppressWarnings("unchecked")
      List<String> stored = (List<String>) sessionAttributes.get("guesses");
      assertThat(stored).hasSize(6); // unchanged, guess was not appended
    }

    @Test
    @DisplayName("marks correctly placed, present, and absent letters")
    void evaluatesSimpleLetterStatuses() {
      seedTarget("APPLE");

      ModelAndView mv = controller.submitGuess("ALARM", session, null);

      Map<String, Object> model = mv.getModel();
      assertThat(model.get("row1_letter1_status")).isEqualTo("correct"); // A-A
      assertThat(model.get("row1_letter2_status")).isEqualTo("present"); // L not in remaining
      assertThat(model.get("row1_letter3_status")).isEqualTo("absent"); // A elsewhere in target
      assertThat(model.get("row1_letter4_status")).isEqualTo("absent"); // R
      assertThat(model.get("row1_letter5_status")).isEqualTo("absent"); // M
    }

    @Test
    @DisplayName("handles duplicate letters correctly: correct pass wins over present pass")
    void evaluatesDuplicateLettersCorrectly() {
      // Target ALLEY = A L L E Y
      // Guess  LLAMA = L L A M A
      // Expected: [present, correct, present, absent, absent]
      // i0 L vs A -> not correct; later matched against target's L at idx2 -> present
      // i1 L vs L -> correct, consumes target idx1
      // i2 A vs L -> not correct; matched against target's A at idx0 -> present
      // i3 M vs E -> absent (no M in target)
      // i4 A vs Y -> not correct; both target A's already consumed -> absent
      seedTarget("ALLEY");

      ModelAndView mv = controller.submitGuess("LLAMA", session, null);

      Map<String, Object> model = mv.getModel();
      assertThat(model.get("row1_letter1")).isEqualTo("L");
      assertThat(model.get("row1_letter1_status")).isEqualTo("present");
      assertThat(model.get("row1_letter2")).isEqualTo("L");
      assertThat(model.get("row1_letter2_status")).isEqualTo("correct");
      assertThat(model.get("row1_letter3")).isEqualTo("A");
      assertThat(model.get("row1_letter3_status")).isEqualTo("present");
      assertThat(model.get("row1_letter4")).isEqualTo("M");
      assertThat(model.get("row1_letter4_status")).isEqualTo("absent");
      assertThat(model.get("row1_letter5")).isEqualTo("A");
      assertThat(model.get("row1_letter5_status")).isEqualTo("absent");
    }

    @Test
    @DisplayName("keeps the username in the model on a guess submission")
    void keepsUsernameOnSubmit() {
      seedTarget("APPLE");
      mockAuthenticatedUser("player@example.com", "player1");

      ModelAndView mv = controller.submitGuess("BRAVE", session, authentication);

      assertThat(mv.getModel().get("username")).isEqualTo("player1");
    }
  }

  // ---------------------------------------------------------------------
  // POST /game/wordle/reset
  // ---------------------------------------------------------------------

  @Nested
  @DisplayName("resetGame")
  class ResetGame {

    @Test
    @DisplayName("clears prior target and guesses and issues a fresh target")
    void clearsSessionAndCreatesNewTarget() {
      seedTarget("APPLE");
      seedGuesses(List.of("BRAVE", "CRANE"));

      ModelAndView mv = controller.resetGame(session, null);

      assertThat(sessionAttributes.get("guesses")).isNull();
      String newTarget = (String) sessionAttributes.get("wordTarget");
      assertThat(newTarget).isIn(WORD_LIST);
      assertThat(mv.getModel().get("wordTarget")).isEqualTo(newTarget);
    }

    @Test
    @DisplayName("resets the grid back to empty")
    void resetsGridToEmpty() {
      seedTarget("APPLE");
      seedGuesses(List.of("BRAVE"));

      ModelAndView mv = controller.resetGame(session, null);

      assertThat(mv.getModel().get("row1_letter1")).isEqualTo("");
      assertThat(mv.getModel().get("row1_letter1_status")).isEqualTo("");
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
