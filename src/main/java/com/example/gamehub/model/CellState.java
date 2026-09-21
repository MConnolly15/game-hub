package com.example.gamehub.model;

public enum CellState {
  WATER("~"),
  SHIP("S"),
  HIT("X"),
  MISS("O");

  private final String symbol;

  CellState(String symbol) {
    this.symbol = symbol;
  }

  public String getSymbol() {
    return symbol;
  }
}
