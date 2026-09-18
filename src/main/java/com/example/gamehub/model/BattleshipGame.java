package com.example.gamehub.model;

import java.util.Random;

public class BattleshipGame {

    private Board playerBoard;
    private Board computerBoard;

    private Turn currentTurn;

    public BattleshipGame() {
        playerBoard = new Board();
        computerBoard = new Board();

        currentTurn = Turn.PLAYER;

        playerBoard.placeHorizontalShip(0, 0, 3);
        playerBoard.placeVerticalShip(3, 1, 2);

        computerBoard.placeHorizontalShip(4, 2, 3);
        computerBoard.placeVerticalShip(0, 0, 2);
    }

    public Board getPlayerBoard() {
        return playerBoard;
    }

    public Board getComputerBoard() {
        return computerBoard;
    }

    public Turn getCurrentTurn() {
        return currentTurn;
    }

    public void playerFire(int row, int column) {
        computerBoard.fireAt(row, column);
        currentTurn = Turn.COMPUTER;
    }

    public void computerFire(int row, int column) {
        playerBoard.fireAt(row, column);
        currentTurn = Turn.PLAYER;
    }

    public void computerTakeTurn() {
        boolean successfulShot = false;

        Random random = new Random();
        do {
            int randomRow = random.nextInt(5);
            int randomColumn = random.nextInt(5);

            try {
                computerFire(randomRow, randomColumn);
                successfulShot = true;
            } catch (IllegalArgumentException e) {
                successfulShot = false;
            }
        }while (!successfulShot);
    }

    public Turn getWinner() {
        if (computerBoard.allShipsDestroyed()) {
            return Turn.PLAYER;
        } else if (playerBoard.allShipsDestroyed()){
            return Turn.COMPUTER;
        }
        return null;
    }
    
}
