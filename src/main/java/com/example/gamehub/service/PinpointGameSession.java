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
  // PinpointGuessResult is used like a data type here (like int. String decleration)
  public PinpointGuessResult submitGuess(String guess) {
    // game.getAnswer calls the getter on the PinpointGame object this session is holding onto,
    // retriving its stored answer
    // EqualIgnoreCase compares two strings for equaltiry and ignores cap sensitivity
    if (guess.equalsIgnoreCase(game.getAnswer())) {
      status = PinpointGameStatus.WON;
      return PinpointGuessResult.CORRECT;
    } else {
      cluesRevealed++;
      guessesMade++;
      return PinpointGuessResult.INCORRECT;
    }
  }
}
