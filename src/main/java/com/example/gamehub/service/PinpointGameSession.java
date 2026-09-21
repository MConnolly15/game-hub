package com.example.gamehub.service;

public class PinpointGameSession {
  // what game is played declared so it can hold a reference to the pinpoint object
  private PinpointGame game;
  // tracks how many clues have been shown so far
  private int cluesRevealed;
  // tracks the guesses made:
  private int guessesMade;
  // tracks game status:
  private PinpointGameStatus status;

  // the constructor takes a PinpointGame and stores it and sets the clues revealed to
  // 1 initially since the test expects a brand new session to hae one clue at the start
  public PinpointGameSession(PinpointGame game) {
    this.game = game;
    this.cluesRevealed = 1;
    this.guessesMade = 0;
    this.status = PinpointGameStatus.IN_PROGRESS;
  }

  public int getCluesRevealed() {
    return cluesRevealed;
  }

  public int getGuessesMade() {
    return guessesMade;
  }

  public PinpointGameStatus getStatus() {
    return status;
  }

  //    when someone submits a guess, I check if it matches the answer (not caring about capital
  // letters).
  //    If it matches, I mark the game as won.If not, nothing happens yet
  // string guess is the user input
  // PinpointGuessResult is used like a data type here (like int. String declaration)
  public PinpointGuessResult submitGuess(String guess) {
    // checking if either game WON/LOST that the player isn't still continuing:
    if (status != PinpointGameStatus.IN_PROGRESS) {
      throw new IllegalStateException("The game has already ended!");
    }
    if (guess.isBlank()) {
      throw new IllegalArgumentException("Guess cannot be blank!");
    }
    // used regex to essentially check if at least 1 or more chars that are numbers are in here then
    // throw an exception.
    // this number specific case might not be needed because the next test covers both nums and
    // special chars
    if (guess.matches("[0-9]+")) {
      throw new IllegalArgumentException("Guess cannot be only numbers!");
    }
    if (!guess.matches(".*[a-zA-Z].*")) {
      throw new IllegalArgumentException("Guess must contain at least one letter!");
    }
    // game.getAnswer calls the getter on the PinpointGame object this session is holding onto,
    // retrieving its stored answer
    // EqualIgnoreCase compares two strings for equality and ignores cap sensitivity
    if (guess.equalsIgnoreCase(game.getAnswer())) {
      status = PinpointGameStatus.WON;
      return PinpointGuessResult.CORRECT;
    } else {
      guessesMade++;
      // if 5 guesses have been made, the game ends as a loss:
      if (guessesMade >= 5) {
        status = PinpointGameStatus.LOST;
        return PinpointGuessResult.GAME_OVER;
        // if not used all my tries, continue
      } else {
        cluesRevealed++;
        return PinpointGuessResult.INCORRECT;
      }
    }
  }
}
