package com.example.gamehub.controller;

import com.example.gamehub.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HangmanController {

  private static final int MAX_WRONG_GUESSES = 6;

  private static final List<String> WORD_LIST =
      List.of(
          "APPLE",
          "BRAVE",
          "CRANE",
          "DOUBT",
          "EAGLE",
          "FAITH",
          "GHOST",
          "MOUSE",
          "IRONIC",
          "JOKER",
          "KNEEL",
          "LEMON",
          "MONEY",
          "NOBLE",
          "OCEAN",
          "PROGRAMMING",
          "MAKERS",
          "PYTHON",
          "COMPUTER",
          "LAPTOP",
          "KEYBOARD",
          "PLANET",
          "QUIET",
          "RIVER",
          "STONE",
          "TIGER");

  private static final String WORD_KEY = "hangmanWord";
  private static final String GUESSED_KEY = "hangmanGuessedLetters";
  private static final String WRONG_COUNT_KEY = "hangmanWrongGuesses";

  private final UserRepository userRepository;

  public HangmanController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/game/hangman")
  public ModelAndView loadPage(HttpSession session, Authentication authentication) {
    return buildGameView(session, authentication, null);
  }

  @PostMapping("/game/hangman/guess")
  public ModelAndView guessLetter(
      @RequestParam String letterGuess, HttpSession session, Authentication authentication) {

    String error = null;

    if (letterGuess == null
        || letterGuess.length() != 1
        || !Character.isLetter(letterGuess.charAt(0))) {
      error = "Guess must be a single letter";
    } else {
      String word = getOrCreateWord(session);
      Set<String> guessedLetters = getOrCreateGuessedLetters(session);
      String letter = letterGuess.toUpperCase();

      boolean alreadyGuessed = guessedLetters.contains(letter);
      boolean gameOver = isWon(word, guessedLetters) || isLost(session);

      if (!gameOver && !alreadyGuessed) {
        guessedLetters.add(letter);
        session.setAttribute(GUESSED_KEY, guessedLetters);

        if (!word.contains(letter)) {
          int wrongGuesses = getWrongGuesses(session) + 1;
          session.setAttribute(WRONG_COUNT_KEY, wrongGuesses);
        }
      }
    }

    return buildGameView(session, authentication, error);
  }

  @PostMapping("/game/hangman/reset")
  public ModelAndView resetGame(HttpSession session, Authentication authentication) {
    session.removeAttribute(WORD_KEY);
    session.removeAttribute(GUESSED_KEY);
    session.removeAttribute(WRONG_COUNT_KEY);
    return buildGameView(session, authentication, null);
  }

  private ModelAndView buildGameView(
      HttpSession session, Authentication authentication, String error) {
    ModelAndView mv = new ModelAndView("hangman");

    String word = getOrCreateWord(session);
    Set<String> guessedLetters = getOrCreateGuessedLetters(session);
    int wrongGuesses = getWrongGuesses(session);

    boolean won = isWon(word, guessedLetters);
    boolean lost = isLost(session);

    mv.addObject("displayWord", buildDisplayWord(word, guessedLetters));
    mv.addObject("guessedLetters", String.join(", ", guessedLetters));
    mv.addObject("wrongGuesses", wrongGuesses);
    mv.addObject("hangmanArt", buildHangmanArt(wrongGuesses));
    mv.addObject("maxWrongGuesses", MAX_WRONG_GUESSES);
    mv.addObject("won", won);
    mv.addObject("lost", lost);
    mv.addObject("error", error);

    if (won) {
      mv.addObject("message", "You won! The word was " + word);
    } else if (lost) {
      mv.addObject("message", "Game over! The word was " + word);
    }

    addUsernameToModel(mv, authentication);
    return mv;
  }

  private void addUsernameToModel(ModelAndView mv, Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()) {
      String email = authentication.getName();
      userRepository
          .findByEmail(email)
          .ifPresent(user -> mv.addObject("username", user.getUsername()));
    }
  }

  private String getOrCreateWord(HttpSession session) {
    String word = (String) session.getAttribute(WORD_KEY);
    if (word == null) {
      word = WORD_LIST.get(new Random().nextInt(WORD_LIST.size()));
      session.setAttribute(WORD_KEY, word);
    }
    return word;
  }

  @SuppressWarnings("unchecked")
  private Set<String> getOrCreateGuessedLetters(HttpSession session) {
    Set<String> guessedLetters = (Set<String>) session.getAttribute(GUESSED_KEY);
    if (guessedLetters == null) {
      guessedLetters = new LinkedHashSet<>();
      session.setAttribute(GUESSED_KEY, guessedLetters);
    }
    return guessedLetters;
  }

  private int getWrongGuesses(HttpSession session) {
    Integer wrongGuesses = (Integer) session.getAttribute(WRONG_COUNT_KEY);
    return wrongGuesses == null ? 0 : wrongGuesses;
  }

  private boolean isWon(String word, Set<String> guessedLetters) {
    for (char c : word.toCharArray()) {
      if (!guessedLetters.contains(String.valueOf(c))) {
        return false;
      }
    }
    return true;
  }

  private boolean isLost(HttpSession session) {
    return getWrongGuesses(session) >= MAX_WRONG_GUESSES;
  }

  private String buildDisplayWord(String word, Set<String> guessedLetters) {
    StringBuilder sb = new StringBuilder();
    for (char c : word.toCharArray()) {
      String letter = String.valueOf(c);
      sb.append(guessedLetters.contains(letter) ? letter : "_").append(" ");
    }
    return sb.toString().trim();
  }

  private String buildHangmanArt(int wrongGuesses) {
    String[] stages = {
      // 0
      "  +---+\n"
          + "  |   |\n"
          + "      |\n"
          + "      |\n"
          + "      |\n"
          + "      |\n"
          + "=========",
      // 1
      "  +---+\n"
          + "  |   |\n"
          + "  O   |\n"
          + "      |\n"
          + "      |\n"
          + "      |\n"
          + "=========",
      // 2
      "  +---+\n"
          + "  |   |\n"
          + "  O   |\n"
          + "  |   |\n"
          + "      |\n"
          + "      |\n"
          + "=========",
      // 3
      "  +---+\n"
          + "  |   |\n"
          + "  O   |\n"
          + " /|   |\n"
          + "      |\n"
          + "      |\n"
          + "=========",
      // 4
      "  +---+\n"
          + "  |   |\n"
          + "  O   |\n"
          + " /|\\  |\n"
          + "      |\n"
          + "      |\n"
          + "=========",
      // 5
      "  +---+\n"
          + "  |   |\n"
          + "  O   |\n"
          + " /|\\  |\n"
          + " /    |\n"
          + "      |\n"
          + "=========",
      // 6
      "  +---+\n"
          + "  |   |\n"
          + "  O   |\n"
          + " /|\\  |\n"
          + " / \\  |\n"
          + "      |\n"
          + "========="
    };

    int index = Math.min(wrongGuesses, stages.length - 1);
    return stages[index];
  }
}
