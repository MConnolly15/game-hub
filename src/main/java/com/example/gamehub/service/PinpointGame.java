package com.example.gamehub.service;

public class PinpointGame {
  // every pinpoint game will have one anser
  private String answer;

  public PinpointGame(String answer) {
    this.answer = answer;
  }

  public String getAnswer() {
    return answer;
  }
}
