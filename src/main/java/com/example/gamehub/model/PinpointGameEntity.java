package com.example.gamehub.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.List;

// @Entity tells Spring this class represents a database table, not just a normal object

@Entity

// @Table says exactly which table:
@Table(name = "pinpoint_games")
public class PinpointGameEntity {

  // @Id marks this as the primary key.
  @Id

  // @GeneratedValue says the database generates this value automatically
  // I never set an id myself when creating a new game
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // matches my : answer VARCHAR(255) NOT NULL from my table creation
  @Column(nullable = false)
  private String answer;

  // this does not  correspond to a column in pinpoint_games,this game has a list of clues attached
  // to it
  // without writing manual SQL joins.
  //
  // mappedBy is used to say the actual foreign key column lives over
  // on ClueEntity in a field called pinpointGame
  //
  // @OrderBy meanswhenever this list loads from the database,sort by clue_order,
  // smallest first
  //
  // cascade is for saving or deleting this game automatically including clues

  @OneToMany(mappedBy = "pinpointGame", cascade = CascadeType.ALL)
  @OrderBy("clueOrder ASC")
  private List<ClueEntity> clues;

  // JPA requires a no argument constructor, it uses this internally to build a
  // blank object first when reading a row from the DB then fills it in.

  public PinpointGameEntity() {}

  // the constructor we actually use:
  public PinpointGameEntity(String answer) {
    this.answer = answer;
  }

  public Long getId() {
    return id;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public List<ClueEntity> getClues() {
    return clues;
  }

  public void setClues(List<ClueEntity> clues) {
    this.clues = clues;
  }
}
