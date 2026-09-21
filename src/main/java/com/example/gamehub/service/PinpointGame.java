package com.example.gamehub.service;

import java.util.List;

public class PinpointGame {
  // every pinpoint game will have one answer
  private String answer;
  private List<String> clues;

  public PinpointGame(String answer, List<String> clues) {

    this.answer = answer;
    this.clues = clues;
  }

  public String getAnswer() {
    return answer;
  }

  public List<String> getClues() {
    return clues;
  }
}
