package com.example.gamehub.controller;

import com.example.gamehub.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WordleController {
  private final UserRepository userRepository;

  public WordleController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  private static final int MAX_ATTEMPTS = 6;

  private static final List<String> WORD_LIST =
      List.of(
          "APPLE", "BRAVE", "CRANE", "DOUBT", "EAGLE", "FAITH", "GHOST", "HOUSE", "IRONY", "JOKER",
          "KNEEL", "LEMON", "MONEY", "NOBLE", "OCEAN", "PLANT", "QUIET", "RIVER", "STONE", "TIGER");

  @GetMapping("/game/wordle")
  public ModelAndView loadPage(HttpSession session, Authentication authentication) {
    ModelAndView mv = new ModelAndView("wordle");
    initializeEmptyGrid(mv);

    String wordTarget = getOrCreateTarget(session);
    addAllGuessesToGrid(mv, session);

    mv.addObject("wordTarget", wordTarget);
    addUsernameToModel(mv, authentication);
    return mv;
  }

  @PostMapping("/game/wordle/submit-guess")
  public ModelAndView submitGuess(
      @RequestParam String userGuess, HttpSession session, Authentication authentication) {

    ModelAndView mv = new ModelAndView("wordle");
    initializeEmptyGrid(mv);
    addUsernameToModel(mv, authentication);

    String wordTarget = getOrCreateTarget(session); // guarantee target exists

    Boolean wordLength = checkWordLength(userGuess);
    if (!wordLength) {
      mv.addObject("error", "Guess must be 5 letters");
      addAllGuessesToGrid(mv, session);
      mv.addObject("wordTarget", wordTarget);
      return mv;
    }

    List<String> guesses = (List<String>) session.getAttribute("guesses");
    if (guesses == null) {
      guesses = new ArrayList<>();
    }

    if (guesses.size() >= MAX_ATTEMPTS) {
      mv.addObject("error", "No attempts remaining — start a new game");
      addAllGuessesToGrid(mv, session);
      mv.addObject("wordTarget", wordTarget);
      return mv;
    }

    String userGuessUpper = userGuess.toUpperCase();
    guesses.add(userGuessUpper);
    session.setAttribute("guesses", guesses);

    mv.addObject("userGuess", userGuessUpper);
    mv.addObject("UserAttempts", guesses.size());
    mv.addObject("wordTarget", wordTarget);

    if (userGuessUpper.equals(wordTarget)) {
      mv.addObject("won", true);
      mv.addObject("message", "You won! The word was " + wordTarget);
    } else if (guesses.size() >= MAX_ATTEMPTS) {
      mv.addObject("lost", true);
      mv.addObject("message", "Game over! The word was " + wordTarget);
    }

    addAllGuessesToGrid(mv, session);

    return mv;
  }

  @PostMapping("/game/wordle/reset")
  public ModelAndView resetGame(HttpSession session, Authentication authentication) {
    session.removeAttribute("wordTarget");
    session.removeAttribute("guesses");

    String wordTarget = getOrCreateTarget(session);

    ModelAndView mv = new ModelAndView("wordle");
    initializeEmptyGrid(mv);
    mv.addObject("wordTarget", wordTarget);
    addUsernameToModel(mv, authentication);
    return mv;
  }

  private String getOrCreateTarget(HttpSession session) {
    String wordTarget = (String) session.getAttribute("wordTarget");
    if (wordTarget == null) {
      wordTarget = WORD_LIST.get(new Random().nextInt(WORD_LIST.size()));
      session.setAttribute("wordTarget", wordTarget);
    }
    return wordTarget;
  }

  private Boolean checkWordLength(String userGuess) {
    return userGuess.length() == 5;
  }

  private String[] evaluateGuess(String guess, String target) {
    int len = guess.length();
    String[] statuses = new String[len];
    boolean[] targetUsed = new boolean[len];

    for (int i = 0; i < len; i++) {
      if (guess.charAt(i) == target.charAt(i)) {
        statuses[i] = "correct";
        targetUsed[i] = true;
      }
    }

    for (int i = 0; i < len; i++) {
      if (statuses[i] != null) continue;

      char c = guess.charAt(i);
      boolean found = false;
      for (int j = 0; j < len; j++) {
        if (!targetUsed[j] && target.charAt(j) == c) {
          found = true;
          targetUsed[j] = true;
          break;
        }
      }
      statuses[i] = found ? "present" : "absent";
    }

    return statuses;
  }

  private void addAllGuessesToGrid(ModelAndView mv, HttpSession session) {
    List<String> guesses = (List<String>) session.getAttribute("guesses");
    if (guesses == null) return;

    String wordTarget = getOrCreateTarget(session); // safe even if called standalone

    for (int row = 0; row < guesses.size(); row++) {
      String guess = guesses.get(row);
      String[] statuses = evaluateGuess(guess, wordTarget);

      for (int letter = 0; letter < guess.length(); letter++) {
        String key = "row" + (row + 1) + "_letter" + (letter + 1);
        mv.addObject(key, String.valueOf(guess.charAt(letter)));
        mv.addObject(key + "_status", statuses[letter]);
      }
    }
  }

  private void initializeEmptyGrid(ModelAndView mv) {
    for (int row = 1; row <= MAX_ATTEMPTS; row++) {
      for (int letter = 1; letter <= 5; letter++) {
        mv.addObject("row" + row + "_letter" + letter, "");
        mv.addObject("row" + row + "_letter" + letter + "_status", "");
      }
    }
  }

  private void addUsernameToModel(ModelAndView mv, Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()) {
      String email = authentication.getName();
      userRepository
          .findByEmail(email)
          .ifPresent(user -> mv.addObject("username", user.getUsername()));
    }
  }
}
