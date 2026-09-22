package com.example.gamehub.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// this maps to my clues table, each row here is one clue that belongs to one game
@Entity
@Table(name = "clues")
public class ClueEntity {

  // same as PinpointGameEntity's id, the DB generates this on its own
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // this is the actual foreign key, matching pinpoint_game_id in my clues table.
  // this is the other side of the relationship I set up on PinpointGameEntity
  // mappedBy = "pinpointGame" over there was pointing at this exact field
  @ManyToOne
  @JoinColumn(name = "pinpoint_game_id", nullable = false)
  private PinpointGameEntity pinpointGame;

  // matches my clue_text column, this is the actual clue text shown to the player
  @Column(nullable = false)
  private String clueText;

  // matches my clue_order column, this is what controls what order the clues get
  // revealed in - @OrderBy on PinpointGameEntity sorts by this
  @Column(nullable = false)
  private Integer clueOrder;

  // JPA needs this empty constructor to build the object first when reading a row
  // back from the database, same reason as PinpointGameEntity
  public ClueEntity() {}

  // the constructor I actually use
  public ClueEntity(PinpointGameEntity pinpointGame, String clueText, Integer clueOrder) {
    this.pinpointGame = pinpointGame;
    this.clueText = clueText;
    this.clueOrder = clueOrder;
  }

  public Long getId() {
    return id;
  }

  public PinpointGameEntity getPinpointGame() {
    return pinpointGame;
  }

  public void setPinpointGame(PinpointGameEntity pinpointGame) {
    this.pinpointGame = pinpointGame;
  }

  public String getClueText() {
    return clueText;
  }

  public void setClueText(String clueText) {
    this.clueText = clueText;
  }

  public Integer getClueOrder() {
    return clueOrder;
  }

  public void setClueOrder(Integer clueOrder) {
    this.clueOrder = clueOrder;
  }
}
