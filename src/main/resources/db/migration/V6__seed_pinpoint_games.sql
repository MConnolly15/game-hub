-- Game 1: matches the original hardcoded MVP answer/clues
INSERT INTO pinpoint_games (answer) VALUES ('Things that are red');
INSERT INTO clues (pinpoint_game_id, clue_text, clue_order) VALUES
                                                                (currval('pinpoint_games_id_seq'), 'Brick', 1),
                                                                (currval('pinpoint_games_id_seq'), 'Stop sign', 2),
                                                                (currval('pinpoint_games_id_seq'), 'Fire truck', 3),
                                                                (currval('pinpoint_games_id_seq'), 'Rose', 4),
                                                                (currval('pinpoint_games_id_seq'), 'Ketchup', 5);

-- Game 2: wordplay/connections puzzle - harder, needs the "Moon ___" leap
INSERT INTO pinpoint_games (answer) VALUES ('Words that can follow ''Moon''');
INSERT INTO clues (pinpoint_game_id, clue_text, clue_order) VALUES
                                                                (currval('pinpoint_games_id_seq'), 'Light', 1),
                                                                (currval('pinpoint_games_id_seq'), 'Walk', 2),
                                                                (currval('pinpoint_games_id_seq'), 'Shine', 3),
                                                                (currval('pinpoint_games_id_seq'), 'Beam', 4),
                                                                (currval('pinpoint_games_id_seq'), 'Struck', 5);

-- Game 3: things found in space
INSERT INTO pinpoint_games (answer) VALUES ('Things found in space');
INSERT INTO clues (pinpoint_game_id, clue_text, clue_order) VALUES
                                                                (currval('pinpoint_games_id_seq'), 'Stars', 1),
                                                                (currval('pinpoint_games_id_seq'), 'Rockets', 2),
                                                                (currval('pinpoint_games_id_seq'), 'Astronaut', 3),
                                                                (currval('pinpoint_games_id_seq'), 'Orbit', 4),
                                                                (currval('pinpoint_games_id_seq'), 'Galaxy', 5);

-- Game 4: things at the beach
INSERT INTO pinpoint_games (answer) VALUES ('Things at the beach');
INSERT INTO clues (pinpoint_game_id, clue_text, clue_order) VALUES
                                                                (currval('pinpoint_games_id_seq'), 'Sand', 1),
                                                                (currval('pinpoint_games_id_seq'), 'Waves', 2),
                                                                (currval('pinpoint_games_id_seq'), 'Shells', 3),
                                                                (currval('pinpoint_games_id_seq'), 'Towel', 4),
                                                                (currval('pinpoint_games_id_seq'), 'Sunscreen', 5);