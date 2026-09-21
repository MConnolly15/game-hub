package com.example.gamehub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PinpointGameTest {
  @Test

  // Given a PinpointGame with answer "things that are red"
  // When a new PinpointGameSession is started for it
  // Then exactly 1 clue has been revealed

  void newGameSessionStartsWithOneClueRevealed() {
    // creates a PinpointGame object representing the fixed data of one game ,its answer.
    // This is the class that will eventually map to my pinpoint_games database table
    PinpointGame game = new PinpointGame("things that are red");
    // the object representing someone actively playing that game right now, it tracks guesses made
    // & current status.
    // It's handed the PinpointGame it belongs to
    PinpointGameSession session = new PinpointGameSession(game);
    // when a session starts, exactly 1 clue should already be revealed
    assertEquals(1, session.getCluesRevealed());
  }

  // Given a new PinpointGame with an answer
  // When a new PinpointGameSession is started for it
  // Then guessesMade is 0

  @Test
  void newGameSessionStartsWithZeroGuessesMade() {
    PinpointGame game = new PinpointGame("things that are red");
    PinpointGameSession session = new PinpointGameSession(game);

    assertEquals(0, session.getGuessesMade());
  }

  // Given a new game with an answer , When a new PinpointGameSession starts, Then status is
  // IN_PROGRESS
  @Test
  void newGameSessionStartsInProgress() {
    PinpointGame game = new PinpointGame("things that are red");
    PinpointGameSession session = new PinpointGameSession(game);
    assertEquals(PinpointGameStatus.IN_PROGRESS, session.getStatus());
  }

  // Given a new PinpointGameSession for a game with answer "things that are red"
  // When the player submits the guess "things that are red"
  // Then the session's status is WON
  @Test
  void correctGuessSetsStatusToWon() {
    PinpointGame game = new PinpointGame("things that are red");
    PinpointGameSession session = new PinpointGameSession(game);

    session.submitGuess("things that are red");
    assertEquals(PinpointGameStatus.WON, session.getStatus());
  }

  // Given a new PinpointGameSession for a game with answer "things that are red"
  // When the player submits the guess "THINGS THAT ARE RED"
  // Then the session's status is WON
  @Test
  void correctGuessGivenInCapsIsStillWon() {
    PinpointGame game = new PinpointGame("things that are red");
    PinpointGameSession session = new PinpointGameSession(game);

    session.submitGuess("THINGS THAT ARE RED");
    assertEquals(PinpointGameStatus.WON, session.getStatus());
  }

  // Given a new PinpointGameSession for a game with answer "things that are red"
  // When the player submits the correct guess "things that are red"
  // Then submitGuess returns GuessResult.CORREC
  @Test
  void correctGuessReturnsCorrectResult() {
    PinpointGame game = new PinpointGame("things that are red");
    PinpointGameSession session = new PinpointGameSession(game);

    PinpointGuessResult result = session.submitGuess("things that are red");

    assertEquals(PinpointGuessResult.CORRECT, result);
  }

  // Given a new PinpointGameSession for a game with answer "things that are red"
  // When the player submits an incorrect guess e.g. "banana"
  // Then cluesRevealed increases from 1 to 2
  @Test
  void incorrectGuessRevealsNextClue() {
    PinpointGame game = new PinpointGame("things that are red");
    PinpointGameSession session = new PinpointGameSession(game);
    // as I am not testing for the result, just the side effect I only need this submitted result:
    session.submitGuess("banana");

    assertEquals(2, session.getCluesRevealed());
  }

  // Given a new PinpointGameSession for a game with answer "things that are red"
  // When the player submits an incorrect guess e.g. "banana"
  // Then guessesMade increases from 0 to 1

  @Test
  void incorrectGuessIncreasesGuessesMade() {
    PinpointGame game = new PinpointGame("things that are red");
    PinpointGameSession session = new PinpointGameSession(game);

    session.submitGuess("banana");

    assertEquals(1, session.getGuessesMade());
  }

  // Given a new PinpointGameSession for a game with answer "things that are red"
  // When the player submits an incorrect guess e.g. "banana"
  // Then status remains IN_PROGRESS

  @Test
  void incorrectGuessKeepsStatusInProgress() {
    PinpointGame game = new PinpointGame("things that are red");
    PinpointGameSession session = new PinpointGameSession(game);

    session.submitGuess("banana");

    assertEquals(PinpointGameStatus.IN_PROGRESS, session.getStatus());
  }

  // Given a new PinpointGameSession for a game with answer "things that are red"
  // When the player submits an incorrect guess e.g. "banana"
  // Then submitGuess returns PinpointGuessResult.INCORRECT

  @Test
  void incorrectGuessReturnsIncorrectResult() {
    PinpointGame game = new PinpointGame("things that are red");
    PinpointGameSession session = new PinpointGameSession(game);

    PinpointGuessResult result = session.submitGuess("banana");

    assertEquals(PinpointGuessResult.INCORRECT, result);
  }
}
