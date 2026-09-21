package com.example.gamehub.model;

public class Board {

  private CellState[][] grid;

  public Board() {
    grid = new CellState[5][5];
    for (int row = 0; row < 5; row++) {
      for (int column = 0; column < 5; column++) {
        grid[row][column] = CellState.WATER;
      }
    }
  }

  public CellState[][] getGrid() {
    return grid;
  }

  public void placeShip(int row, int column) {
    grid[row][column] = CellState.SHIP;
  }

  public void placeHorizontalShip(int row, int column, int length) {
    if (column + length > 5) { // checks if the ship length will go beyond the boards column
      throw new IllegalArgumentException("Ship does not fit on board");
    }
    for (int i = 0; i < length; i++) {
      if (grid[row][column + i] == CellState.SHIP) {
        throw new IllegalArgumentException("Ships cannot overlap");
      }
    }
    for (int i = 0; i < length; i++) {
      grid[row][column + i] = CellState.SHIP;
    }
  }

  public void placeVerticalShip(int row, int column, int length) {
    if (row + length > 5) { // checks if the ship length will go beyond the boards rows
      throw new IllegalArgumentException("Ship does not fit on board");
    }
    for (int i = 0; i < length; i++) {
      if (grid[row + i][column] == CellState.SHIP) {
        throw new IllegalArgumentException("Ships cannot overlap");
      }
    }
    for (int i = 0; i < length; i++) {
      grid[row + i][column] = CellState.SHIP;
    }
  }

  public CellState fireAt(int row, int column) {
    if (grid[row][column] == CellState.SHIP) {
      grid[row][column] = CellState.HIT;
      return CellState.HIT;

    } else if (grid[row][column] == CellState.HIT || grid[row][column] == CellState.MISS) {
      throw new IllegalArgumentException("Cannot fire on already fired at coordinate");

    } else {
      grid[row][column] = CellState.MISS;
      return CellState.MISS;
    }
  }

  public boolean allShipsDestroyed() {
    for (int row = 0; row < 5; row++) {
      for (int column = 0; column < 5; column++) {
        if (grid[row][column] == CellState.SHIP) {
          return false;
        }
      }
    }
    return true;
  }
}
