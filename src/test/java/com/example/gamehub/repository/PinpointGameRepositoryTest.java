package com.example.gamehub.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.gamehub.model.ClueEntity;
import com.example.gamehub.model.PinpointGameEntity;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

// @DataJpaTest wires up just the repository parts of Spring for a fast test, and
// automatically gives me a safe, temporary, fake database just for this test - it
// never touches my real Postgres database at all, so there's nothing to worry about
// here even if something goes wrong
@DataJpaTest
class PinpointGameRepositoryTest {

  // Spring automatically creates a real, working PinpointGameRepository connected
  // to that temporary test database and hands it to me
  @Autowired private PinpointGameRepository pinpointGameRepository;

  @Test
  @DisplayName("saves and retrieves a game with its clues in the correct order")
  void savesAndRetrievesGameWithCluesInOrder() {
    // build a game in memory first
    PinpointGameEntity game = new PinpointGameEntity("Things that are red");

    // build the clues, each one linked back to the same game object. They are in order here (1, 2,
    // 3)
    // the real test of ordering happens when we read them back below
    List<ClueEntity> clues =
        List.of(
            new ClueEntity(game, "Brick", 1),
            new ClueEntity(game, "Stop sign", 2),
            new ClueEntity(game, "Fire truck", 3));
    game.setClues(clues);

    // .save() writes the game and its clues to the test database in one call, thanks to
    // the cascade = CascadeType.ALL there is a set up on PinpointGameEntity. It hands back
    // the saved version, which now has an id
    PinpointGameEntity saved = pinpointGameRepository.save(game);

    // Now go back and ask for it again by id, rather than just reusing the saved
    // variable, this proves the data actually made it into the database and came
    // back correctly, not just that it existed in memory
    Optional<PinpointGameEntity> found = pinpointGameRepository.findById(saved.getId());

    // check the game itself came back correctly
    assertThat(found).isPresent();
    assertThat(found.get().getAnswer()).isEqualTo("Things that are red");

    // check the clues came back in the right order
    assertThat(found.get().getClues())
        .extracting(ClueEntity::getClueText)
        .containsExactly("Brick", "Stop sign", "Fire truck");
  }

  @Test
  @DisplayName("findFirstByOrderByIdAsc returns the earliest-created game")
  void findFirstByOrderByIdAscReturnsEarliestGame() {
    // save two separate games, in this order
    pinpointGameRepository.save(new PinpointGameEntity("First game"));
    pinpointGameRepository.save(new PinpointGameEntity("Second game"));

    // this calls the custom method in PinpointGameRepository, it should always
    // return whichever game has the lowest id, i.e. the one created first
    Optional<PinpointGameEntity> first = pinpointGameRepository.findFirstByOrderByIdAsc();

    assertThat(first).isPresent();
    assertThat(first.get().getAnswer()).isEqualTo("First game");
  }
}
