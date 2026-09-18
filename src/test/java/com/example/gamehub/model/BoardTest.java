package com.example.gamehub.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
        @Test
        void boardHasFiveRows() {
            Board board = new Board();

            assertEquals(5, board.getGrid().length);
            assertEquals(5, board.getGrid()[0].length);
        }

    @Test
    void newBoardStartsWithWater() {
        Board board = new Board();

        for (int row = 0; row < 5; row++) {
            for (int column = 0; column < 5; column++) {
                assertEquals(CellState.WATER, board.getGrid()[row][column]);
            }
        }
    }

    @Test
    void canPlaceShipOnBoard() {
        Board board = new Board();

        board.placeShip(2, 3);

        assertEquals(CellState.SHIP, board.getGrid()[2][3]);
    }

    @Test
    void canPlaceHorizontalShip() {
        Board board = new Board();

        board.placeHorizontalShip(1, 1, 3);

        assertEquals(CellState.SHIP, board.getGrid()[1][1]);
        assertEquals(CellState.SHIP, board.getGrid()[1][2]);
        assertEquals(CellState.SHIP, board.getGrid()[1][3]);
    }

    @Test
    void cannotPlaceHorizontalShipOutsideBoard() {
        Board board = new Board();

        try {
            board.placeHorizontalShip(2, 4, 3);
            fail("Expected an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Ship does not fit on board", e.getMessage());
        }
    }

    @Test
    void canPlaceVerticalShip() {
        Board board = new Board();

        board.placeVerticalShip(1, 2, 3);

        assertEquals(CellState.SHIP, board.getGrid()[1][2]);
        assertEquals(CellState.SHIP, board.getGrid()[2][2]);
        assertEquals(CellState.SHIP, board.getGrid()[3][2]);
    }

    @Test
    void cannotPlaceVerticalShipOutsideBoard() {
        Board board = new Board();

        try {
            board.placeVerticalShip(4, 2, 3);
            fail("Expected an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Ship does not fit on board", e.getMessage());
        }
    }

    @Test
    void cannotPlaceHorizontalShipOverAnotherShip() {
        Board board = new Board();

        board.placeHorizontalShip(1, 1, 3);

        try {
            board.placeHorizontalShip(1, 2, 2);
            fail("Expected an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Ships cannot overlap", e.getMessage());
        }
    }

    @Test
    void cannotPlaceVerticalShipOverAnotherShip() {
        Board board = new Board();

        board.placeHorizontalShip(2, 1, 3);

        try {
            board.placeVerticalShip(1, 2, 3);
            fail("Expected an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Ships cannot overlap", e.getMessage());
        }
    }

    @Test
    void firingAtWaterResultsInMiss() {
        Board board = new Board();

        CellState result = board.fireAt(2, 3);

        assertEquals(CellState.MISS, result);
        assertEquals(CellState.MISS, board.getGrid()[2][3]);
    }

    @Test
    void firingAtShipResultsInHit() {
        Board board = new Board();
        board.placeShip(2, 3);

        CellState result = board.fireAt(2, 3);

        assertEquals(CellState.HIT, result);
        assertEquals(CellState.HIT, board.getGrid()[2][3]);
    }

    @Test
    void cannotFireAtCellAlreadyFiredAt() {
        Board board = new Board();

        board.fireAt(1, 1);

        try {
            board.fireAt(1, 1);
            fail("Expected an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot fire on already fired at coordinate", e.getMessage());
        }
    }


    @Test
    void areAllShipsDestroyed() {
        Board board = new Board();

        board.placeHorizontalShip(1, 1, 3);

        board.fireAt(1, 1);

        assertFalse(board.allShipsDestroyed());
        }

    @Test
    void destroyShip() {
            Board board = new Board();
        board.placeHorizontalShip(1, 1, 3);

        board.fireAt(1, 1);
        board.fireAt(1, 2);
        board.fireAt(1, 3);

        assertTrue(board.allShipsDestroyed());

    }

}


