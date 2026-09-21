package com.example.gamehub.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.AssertionsKt.assertNull;

public class BattleshipGameTest {

  @Test
  void gameStartsWithPlayerAndComputerBoards() {
    BattleshipGame game = new BattleshipGame();

    assertNotNull(game.getPlayerBoard());
    assertNotNull(game.getComputerBoard());
  }

  @Test
  void gameStartsWithPlayerTurn() {
    BattleshipGame game = new BattleshipGame();

    assertEquals(Turn.PLAYER, game.getCurrentTurn());
  }

  @Test
  void playerCanTakeTurn() {
    BattleshipGame game = new BattleshipGame();

    game.playerFire(2, 3);

    CellState result = game.getComputerBoard().getGrid()[2][3];

    assertTrue(result == CellState.HIT || result == CellState.MISS);
    assertEquals(Turn.COMPUTER, game.getCurrentTurn());
  }

  @Test
  void computerCanTakeTurn() {
    BattleshipGame game = new BattleshipGame();

    game.playerFire(2, 3); // player need to take their turn first
    assertEquals(Turn.COMPUTER, game.getCurrentTurn());

    game.computerFire(3, 2);

    assertEquals(CellState.MISS, game.getPlayerBoard().getGrid()[3][2]);
    assertEquals(Turn.PLAYER, game.getCurrentTurn());
  }

  @Test
  void gameStartsWithEmptyPlayerBoard() {
    BattleshipGame game = new BattleshipGame();

    for (int row = 0; row < 5; row++) {
      for (int column = 0; column < 5; column++) {
        assertEquals(CellState.WATER, game.getPlayerBoard().getGrid()[row][column]);
      }
    }

    assertEquals(3, game.getShipLengthToPlace());
  }

  @Test
  void gameSetsUpComputerShips() {
    BattleshipGame game = new BattleshipGame();

    int shipCells = 0;

    for (int row = 0; row < 5; row++) {
      for (int column = 0; column < 5; column++) {

        if (game.getComputerBoard().getGrid()[row][column] == CellState.SHIP) {
          shipCells++;
        }
      }
    }

    assertEquals(5, shipCells);
  }

  @Test
  void computerTakesRandomTurnAndReturnsTurnToPlayer() {
    BattleshipGame game = new BattleshipGame();

    game.playerFire(2, 3);
    assertEquals(Turn.COMPUTER, game.getCurrentTurn());

    game.computerTakeTurn();

    assertEquals(Turn.PLAYER, game.getCurrentTurn());
  }

  @Test
  void playerWinsWhenAllComputerShipsAreDestroyed() {
    BattleshipGame game = new BattleshipGame();

    game.placePlayerShip(0, 0, true);
    game.placePlayerShip(3, 1, false);

    for (int row = 0; row < 5; row++) {
      for (int column = 0; column < 5; column++) {

        if (game.getComputerBoard().getGrid()[row][column] == CellState.SHIP) {
          game.playerFire(row, column);
        }
      }
    }

    assertEquals(Turn.PLAYER, game.getWinner());
  }

  @Test
  void computerWinsWhenAllPlayerShipsAreDestroyed() {
    BattleshipGame game = new BattleshipGame();

    game.placePlayerShip(0, 0, true);
    game.placePlayerShip(3, 1, false);

    // destroy all 5 player ship cells
    game.playerFire(1, 3);
    game.computerFire(0, 0);

    game.playerFire(2, 4);
    game.computerFire(0, 1);

    game.playerFire(3, 2);
    game.computerFire(0, 2);

    game.playerFire(4, 1);
    game.computerFire(3, 1);

    game.playerFire(1, 4);
    game.computerFire(4, 1);

    assertEquals(Turn.COMPUTER, game.getWinner());
  }

  @Test
  void gameStartsWithNoWinner() {
    BattleshipGame game = new BattleshipGame();

    assertNull(game.getWinner());
  }

  @Test
  void playerCanPlaceThreeCellShip() {
    BattleshipGame game = new BattleshipGame();

    game.placePlayerShip(1, 1, true);

    assertEquals(CellState.SHIP, game.getPlayerBoard().getGrid()[1][1]);
    assertEquals(CellState.SHIP, game.getPlayerBoard().getGrid()[1][2]);
    assertEquals(CellState.SHIP, game.getPlayerBoard().getGrid()[1][3]);

    assertEquals(2, game.getShipLengthToPlace());
  }
}
