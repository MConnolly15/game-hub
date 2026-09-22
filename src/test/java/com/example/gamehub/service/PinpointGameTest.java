package com.example.gamehub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class PinpointGameTest {
  @Test

  // Given a PinpointGame with answer "things that are red"
  // When a new PinpointGameSession is started for it
  // Then exactly 1 clue has been revealed

  void newGameSessionStartsWithOneClueRevealed() {
    // creates a PinpointGame object representing the fixed data of one game ,its answer.
    // This is the class that will eventually map to my pinpoint_games database table
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("clue1", "clue2", "clue3", "clue4", "clue5"));
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
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("clue1", "clue2", "clue3", "clue4", "clue5"));
    PinpointGameSession session = new PinpointGameSession(game);

    assertEquals(0, session.getGuessesMade());
  }

  // Given a new game with an answer , When a new PinpointGameSession starts, Then status is
  // IN_PROGRESS
  @Test
  void newGameSessionStartsInProgress() {
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("clue1", "clue2", "clue3", "clue4", "clue5"));
    PinpointGameSession session = new PinpointGameSession(game);
    assertEquals(PinpointGameStatus.IN_PROGRESS, session.getStatus());
  }

  // Given a new PinpointGameSession for a game with answer "things that are red"
  // When the player submits the guess "things that are red"
  // Then the session's status is WON
  @Test
  void correctGuessSetsStatusToWon() {
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("clue1", "clue2", "clue3", "clue4", "clue5"));
    PinpointGameSession session = new PinpointGameSession(game);

    session.submitGuess("things that are red");
    assertEquals(PinpointGameStatus.WON, session.getStatus());
  }

  // Given a new PinpointGameSession for a game with answer "things that are red"
  // When the player submits the guess "THINGS THAT ARE RED"
  // Then the session's status is WON
  @Test
  void correctGuessGivenInCapsIsStillWon() {
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("clue1", "clue2", "clue3", "clue4", "clue5"));
    PinpointGameSession session = new PinpointGameSession(game);

    session.submitGuess("THINGS THAT ARE RED");
    assertEquals(PinpointGameStatus.WON, session.getStatus());
  }

  // Given a new PinpointGameSession for a game with answer "things that are red"
  // When the player submits the correct guess "things that are red"
  // Then submitGuess returns GuessResult.CORREC
  @Test
  void correctGuessReturnsCorrectResult() {
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("clue1", "clue2", "clue3", "clue4", "clue5"));
    PinpointGameSession session = new PinpointGameSession(game);

    PinpointGuessResult result = session.submitGuess("things that are red");

    assertEquals(PinpointGuessResult.CORRECT, result);
  }

  // Given a new PinpointGameSession for a game with answer "things that are red"
  // When the player submits an incorrect guess e.g. "banana"
  // Then cluesRevealed increases from 1 to 2
  @Test
  void incorrectGuessRevealsNextClue() {
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("clue1", "clue2", "clue3", "clue4", "clue5"));
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
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("clue1", "clue2", "clue3", "clue4", "clue5"));
    PinpointGameSession session = new PinpointGameSession(game);

    session.submitGuess("banana");

    assertEquals(1, session.getGuessesMade());
  }

  // Given a new PinpointGameSession for a game with answer "things that are red"
  // When the player submits an incorrect guess e.g. "banana"
  // Then status remains IN_PROGRESS

  @Test
  void incorrectGuessKeepsStatusInProgress() {
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("clue1", "clue2", "clue3", "clue4", "clue5"));
    PinpointGameSession session = new PinpointGameSession(game);

    session.submitGuess("banana");

    assertEquals(PinpointGameStatus.IN_PROGRESS, session.getStatus());
  }

  // Given a new PinpointGameSession for a game with answer "things that are red"
  // When the player submits an incorrect guess e.g. "banana"
  // Then submitGuess returns PinpointGuessResult.INCORRECT

  @Test
  void incorrectGuessReturnsIncorrectResult() {
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("clue1", "clue2", "clue3", "clue4", "clue5"));
    PinpointGameSession session = new PinpointGameSession(game);

    PinpointGuessResult result = session.submitGuess("banana");

    assertEquals(PinpointGuessResult.INCORRECT, result);
  }

  // Given a new PinpointGameSession for a game with answer "things that are red"
  // When the player has already made 4 incorrect guesses, then submits a 5th incorrect guess
  // Then the status becomes LOST

  @Test
  void fiveincorrectGuessSetsStatusToLost() {
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("clue1", "clue2", "clue3", "clue4", "clue5"));
    PinpointGameSession session = new PinpointGameSession(game);

    session.submitGuess("guess1");
    session.submitGuess("guess2");
    session.submitGuess("guess3");
    session.submitGuess("guess4");
    session.submitGuess("guess5");

    assertEquals(PinpointGameStatus.LOST, session.getStatus());
  }

  // Given a new PinpointGameSession for a game with answer "things that are red"
  // When the player makes 5 incorrect guesses
  // Then cluesRevealed stays at 5 and does not go up to 6

  @Test
  void fiveincorrectGuessKeepsCluesRevealedAtFive() {
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("clue1", "clue2", "clue3", "clue4", "clue5"));
    PinpointGameSession session = new PinpointGameSession(game);

    session.submitGuess("guess1");
    session.submitGuess("guess2");
    session.submitGuess("guess3");
    session.submitGuess("guess4");
    session.submitGuess("guess5");
    // The check needs to be on the same data type:
    assertEquals(5, session.getCluesRevealed());
  }

  // Given a new PinpointGameSession for a game with answer "things that are red"
  // When the player makes 5 incorrect guesses
  // Then GussResult.GAME_over is returns
  @Test
  void fiveincorrectGuessReturnsIncorrectResult() {
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("clue1", "clue2", "clue3", "clue4", "clue5"));
    PinpointGameSession session = new PinpointGameSession(game);

    session.submitGuess("guess1");
    session.submitGuess("guess2");
    session.submitGuess("guess3");
    session.submitGuess("guess4");
    // you can only return once so you do it on the last run
    PinpointGuessResult result = session.submitGuess("guess5");

    assertEquals(PinpointGuessResult.GAME_OVER, result);
  }

  // Given a PinpointGameSession where the game has already been won
  // When submitGuess is called again
  // Then it throws an IllegalStateException

  @Test
  void submittingGuessAfterGameWonThrowsException() {
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("clue1", "clue2", "clue3", "clue4", "clue5"));
    PinpointGameSession session = new PinpointGameSession(game);
    // here I am sating this is essentially a game that has already been won:
    session.submitGuess("things that are red");
    // () -> is lamda saying dont run this code yet
    assertThrows(IllegalStateException.class, () -> session.submitGuess("something"));
  }

  // Given a PinpointGameSession where the game has already been won
  // When submitGuess is called again after it ended
  // Then the status and guessesMade stay exactly as they were before that call
  @Test
  void guessingAfterGameEndedLeavesStateUnchanged() {
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("clue1", "clue2", "clue3", "clue4", "clue5"));
    PinpointGameSession session = new PinpointGameSession(game);

    session.submitGuess("things that are red");

    assertThrows(IllegalStateException.class, () -> session.submitGuess("anything"));
    // this checks that the status is still won and that the session is still at 0
    assertEquals(PinpointGameStatus.WON, session.getStatus());
    assertEquals(1, session.getGuessesMade());
  }

  // Given a PinpointGameSession that is in progress
  // When submitGuess is called with a blank guess
  // Then it throws an IllegalArgumentException
  @Test
  void blankGuessThrowsException() {
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("clue1", "clue2", "clue3", "clue4", "clue5"));
    PinpointGameSession session = new PinpointGameSession(game);
    // IllegalArgumentException- the user input  me is invalid
    assertThrows(IllegalArgumentException.class, () -> session.submitGuess("   "));
  }

  // Given a PinpointGameSession that is in progress
  // When submitGuess is called with a guess that is only numbers
  // Then it throws an IllegalArgumentException

  @Test
  void numbersOnlyGuessThrowsException() {
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("clue1", "clue2", "clue3", "clue4", "clue5"));
    PinpointGameSession session = new PinpointGameSession(game);

    assertThrows(IllegalArgumentException.class, () -> session.submitGuess("12345"));
  }

  // Given a PinpointGameSession that is in progress
  // When submitGuess is called with a guess that is only special characters
  // Then it throws an IllegalArgumentException

  @Test
  void specialCharsOnlyGuessThrowsException() {
    PinpointGame game =
        new PinpointGame(
            "things that are red", List.of("clue1", "clue2", "clue3", "clue4", "clue5"));
    PinpointGameSession session = new PinpointGameSession(game);

    assertThrows(IllegalArgumentException.class, () -> session.submitGuess("!@?£#"));
  }
}
