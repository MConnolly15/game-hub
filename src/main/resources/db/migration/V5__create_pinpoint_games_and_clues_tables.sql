CREATE TABLE pinpoint_games(
    id BIGSERIAL PRIMARY KEY,
    answer VARCHAR(255) NOT NULL
);

CREATE TABLE clues(
    id BIGSERIAL PRIMARY KEY,
    pinpoint_game_id BIGINT NOT NULL REFERENCES pinpoint_games(id),
    clue_text VARCHAR(255) NOT NULL,
    clue_order INT NOT NULL

);