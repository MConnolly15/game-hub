package com.example.gamehub.repository;

import com.example.gamehub.model.PinpointGameEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// same pattern as UserRepository: extending JpaRepository provides save(), findById(),
// findAll(), delete() without writing any implementation
public interface PinpointGameRepository extends JpaRepository<PinpointGameEntity, Long> {

  // JPA reads the method name and works out the SQL from it. findFirstByOrderByIdAsc sorts all
  // games
  // by id, ascending, and give me just the first one. This is how I implement
  // picking the first game without writing additional SQL
  Optional<PinpointGameEntity> findFirstByOrderByIdAsc();

  // picks one random game from all the seeded games, using Postgres's RANDOM()
  // function directly in the query
  //
  // nativeQuery = true means this is raw SQL not a Spring query
  @Query(value = "SELECT * FROM pinpoint_games ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
  Optional<PinpointGameEntity> findRandomGame();
}
