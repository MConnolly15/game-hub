package com.example.gamehub.model;

import java.util.Random;

public class BattleshipGame {

    private Board playerBoard;
    private Board computerBoard;
    private Turn currentTurn;
    private int shipLengthToPlace;


    public BattleshipGame() {
        playerBoard = new Board();
        computerBoard = new Board();

        currentTurn = Turn.PLAYER;

        shipLengthToPlace = 3;

        placeRandomShip(computerBoard, 3);
        placeRandomShip(computerBoard, 2);
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

    public int getShipLengthToPlace() {
        return shipLengthToPlace;
    }



    public void placePlayerShip(int row, int column, boolean horizontal) {

        if (horizontal) {
            playerBoard.placeHorizontalShip(row, column, shipLengthToPlace);
        } else {
            playerBoard.placeVerticalShip(row, column, shipLengthToPlace);
        }

        if (shipLengthToPlace == 3) {
            shipLengthToPlace = 2;
        } else {
            shipLengthToPlace = 0;
        }
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

        if (shipLengthToPlace != 0) {
            return null;
        }

        if (computerBoard.allShipsDestroyed()) {
            return Turn.PLAYER;
        } else if (playerBoard.allShipsDestroyed()){
            return Turn.COMPUTER;
        }

        return null;
    }

    private void placeRandomShip(Board board, int length) {

        Random random = new Random();
        boolean shipPlaced = false;

        while (!shipPlaced) {

            int row = random.nextInt(5);
            int column = random.nextInt(5);
            boolean horizontal = random.nextBoolean();

            try {

                if (horizontal) {
                    board.placeHorizontalShip(row, column, length);
                } else {
                    board.placeVerticalShip(row, column, length);
                }

                shipPlaced = true;

            } catch (IllegalArgumentException e) {
                // Invalid position, so the loop tries again
            }
        }
    }
    
}
