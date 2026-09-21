CREATE TABLE scores (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL,
    score INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_scores_user
        FOREIGN KEY (user_id)
            REFERENCES users(id),

    CONSTRAINT fk_scores_game
        FOREIGN KEY (game_id)
            REFERENCES games(id)
);