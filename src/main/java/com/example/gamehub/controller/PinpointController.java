package com.example.gamehub.controller;

import com.example.gamehub.model.ClueEntity;
import com.example.gamehub.model.PinpointGameEntity;
import com.example.gamehub.repository.PinpointGameRepository;
import com.example.gamehub.repository.UserRepository;
import com.example.gamehub.service.PinpointGame;
import com.example.gamehub.service.PinpointGameSession;
import com.example.gamehub.service.PinpointGameStatus;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PinpointController {
  // store the game session inside the browser's session storage:
  private static final String SESSION_KEY = "pinpointGameSession";

  private final UserRepository userRepository;
  private final PinpointGameRepository pinpointGameRepository;

  // constructor
  public PinpointController(
      UserRepository userRepository, PinpointGameRepository pinpointGameRepository) {
    this.userRepository = userRepository;
    this.pinpointGameRepository = pinpointGameRepository;
  }

  // loads a random seeded game from the database, along with its clues in order,
  // and turns it into a PinpointGame the game logic understands
  private PinpointGame createGame() {
    PinpointGameEntity entity =
        pinpointGameRepository
            .findRandomGame()
            .orElseThrow(
                () -> new IllegalStateException("No pinpoint games found in the database"));

    List<String> clueTexts =
        entity.getClues().stream().map(ClueEntity::getClueText).collect(Collectors.toList());

    return new PinpointGame(entity.getAnswer(), clueTexts);
  }

  // This tells bootspring when a browser sends a GET req to this endpoint, run this method, and
  // Spring
  // supplies session and authentication to see if user is logged in:

  @GetMapping("/game/pinpoint")
  public ModelAndView loadPage(HttpSession session, Authentication authentication) {

    // this pulls whatever's stored under that key out of the session. It comes back as a
    // generic object, essentially telling Java that this is actually a PinpointGameSession:
    PinpointGameSession pinpointSession = (PinpointGameSession) session.getAttribute(SESSION_KEY);

    // if nothing is stored yet create a fresh game and session and save onto HTTP session for
    // next time
    if (pinpointSession == null) {
      PinpointGame game = createGame();
      pinpointSession = new PinpointGameSession(game);
      session.setAttribute(SESSION_KEY, pinpointSession);
    }
    // handed to buildModelAndView to package up page  display, passing null for the error since
    // there
    // isn't one on a fresh page load
    return buildModelAndView(pinpointSession, authentication, null);
  }

  // submitting a guess:
  @PostMapping("/game/pinpoint/guess")
  // This runs when the visitor submits the guess form
  public ModelAndView submitGuess(
      // takes the session and below tries to run the game logic
      // if it meets an error (blank,nums etc) it catches it and saves the exception's message text
      // instead of crashing the app
      @RequestParam("guess") String guess, HttpSession session, Authentication authentication) {
    PinpointGameSession pinpointSession = (PinpointGameSession) session.getAttribute(SESSION_KEY);

    String error = null;
    try {
      // tries to use the written logic and if it doesn't work to error out with an exception
      pinpointSession.submitGuess(guess);
    } catch (IllegalArgumentException | IllegalStateException e) {
      error = e.getMessage();
    }
    // renders whatever is displayed, meaning if it errors the error message
    return buildModelAndView(pinpointSession, authentication, error);
  }

  // shared by both loadPage and submitGuess, to avoid repeat myself
  // where the actual data going to the page gets assembled
  private ModelAndView buildModelAndView(
      PinpointGameSession pinpointSession, Authentication authentication, String error) {
    ModelAndView modelAndView = new ModelAndView("pinpoint");
    // shows just the visible clues that I want displayed :
    List<String> visibleClues =
        pinpointSession.getGame().getClues().subList(0, pinpointSession.getCluesRevealed());
    // adds these to be able to render using thymleave and access using ${clues} format in the HTML:
    modelAndView.addObject("clues", visibleClues);
    modelAndView.addObject("cluesRevealed", pinpointSession.getCluesRevealed());
    modelAndView.addObject("guessesMade", pinpointSession.getGuessesMade());
    modelAndView.addObject("status", pinpointSession.getStatus());

    if (error != null) {
      modelAndView.addObject("error", error);
    }
    // displays of won /loss
    if (pinpointSession.getStatus() == PinpointGameStatus.WON) {
      modelAndView.addObject(
          "message", "You won! It took you " + pinpointSession.getGuessesMade() + " guess(es).");
    } else if (pinpointSession.getStatus() == PinpointGameStatus.LOST) {
      modelAndView.addObject(
          "message", "Game over! The answer was: " + pinpointSession.getGame().getAnswer());
    }

    // checking if the user is authenticated, only add the username if they are approved:
    if (authentication != null && authentication.isAuthenticated()) {
      userRepository
          .findByEmail(authentication.getName())
          .ifPresent(user -> modelAndView.addObject("username", user.getUsername()));
    }

    return modelAndView;
  }

  // reset game:
  // this always rebuilds a  new game and session, just overides it, stores it and displays a fresh
  // state of the game
  @PostMapping("/game/pinpoint/reset")
  public ModelAndView resetGame(HttpSession session, Authentication authentication) {
    PinpointGame game = createGame();
    PinpointGameSession pinpointSession = new PinpointGameSession(game);
    session.setAttribute(SESSION_KEY, pinpointSession);

    return buildModelAndView(pinpointSession, authentication, null);
  }
}
