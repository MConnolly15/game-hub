package com.example.gamehub.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

        game.playerFire(2,3);

        assertEquals(CellState.MISS, game.getComputerBoard().getGrid()[2][3]);
        assertEquals(Turn.COMPUTER, game.getCurrentTurn());
    }

    @Test
    void computerCanTakeTurn() {
        BattleshipGame game = new BattleshipGame();

        game.playerFire(2,3); // player need to take their turn first
        assertEquals(Turn.COMPUTER, game.getCurrentTurn());

        game.computerFire(3,2);

        assertEquals(CellState.MISS, game.getPlayerBoard().getGrid()[3][2]);
        assertEquals(Turn.PLAYER, game.getCurrentTurn());
    }

    @Test
    void gameSetsUpPlayerShips() {
        BattleshipGame game = new BattleshipGame();

        assertEquals(CellState.SHIP, game.getPlayerBoard().getGrid()[0][0]);
        assertEquals(CellState.SHIP, game.getPlayerBoard().getGrid()[0][1]);
        assertEquals(CellState.SHIP, game.getPlayerBoard().getGrid()[0][2]);

        assertEquals(CellState.SHIP, game.getPlayerBoard().getGrid()[3][1]);
        assertEquals(CellState.SHIP, game.getPlayerBoard().getGrid()[4][1]);
    }

    @Test
    void gameSetsUpComputerShips() {
        BattleshipGame game = new BattleshipGame();

        assertEquals(CellState.SHIP, game.getComputerBoard().getGrid()[4][2]);
        assertEquals(CellState.SHIP, game.getComputerBoard().getGrid()[4][3]);
        assertEquals(CellState.SHIP, game.getComputerBoard().getGrid()[4][4]);

        assertEquals(CellState.SHIP, game.getComputerBoard().getGrid()[0][0]);
        assertEquals(CellState.SHIP, game.getComputerBoard().getGrid()[1][0]);
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

        // destroy all 5 computer ship cells
        game.playerFire(0, 0);
        game.computerTakeTurn();

        game.playerFire(1, 0);
        game.computerTakeTurn();

        game.playerFire(4, 2);
        game.computerTakeTurn();

        game.playerFire(4, 3);
        game.computerTakeTurn();

        game.playerFire(4, 4);

        assertEquals(Turn.PLAYER, game.getWinner());
    }

    @Test
    void computerWinsWhenAllPlayerShipsAreDestroyed() {
        BattleshipGame game = new BattleshipGame();

        // destroy all 5 player ship cells
        game.playerFire(1,3);
        game.computerFire(0, 0);

        game.playerFire(2, 4);
        game.computerFire(0,1);

        game.playerFire(3, 2);
        game.computerFire(0,2);

        game.playerFire(4, 1);
        game.computerFire(3,1);

        game.playerFire(1, 4);
        game.computerFire(4,1);

        assertEquals(Turn.COMPUTER, game.getWinner());
    }

    @Test
    void gameStartsWithNoWinner() {
        BattleshipGame game = new BattleshipGame();

        assertNull(game.getWinner());
    }

}
