import {
    startGame,
    gameCountdown,
    gameTimer,
    createRocket,
    moveRocketRight,
    moveRocketLeft,
    loseLife,
    createBullet,
    moveBullet,
    bulletHitAsteroid,
    createAsteroid,
    destroyAsteroid,
    addScore,
    removeScore,
    restartGame
    } from "../../../main/resources/static/rocket-rush/js/rocket-rush-logic.js";
import { describe, expect, it } from "vitest";

describe("Rocket Rush game logic", () => {

    // Test 1 - Start game
    it("changes the game from stopped to running", () => {
        const game = {
            running: false
        };

        startGame(game);

        expect(game.running).toBe(true);
    });


    // Test 2 - Countdown starts at 3
    it("starts the countdown at 3", () => {
        const game = {
            running: false,
            countdown: 0
        };

        startGame(game);

        expect(game.countdown).toBe(3);
    });


    // Test 3 - Countdown reaches 0
    it("counts down from 3 to 0", () => {
        const game = {
            running: false,
            countdown: 0
        };

        startGame(game);
        gameCountdown(game);
        gameCountdown(game);
        gameCountdown(game);

        expect(game.countdown).toBe(0);
    });


    // Test 4 - Game enters countdown state
    it("changes game status to countdown when game starts", () => {
        const game = {
            //current game status, player @ menu
            status: "menu"
        };

        //Game starts and i expect it to move into the countdown stage
        startGame(game);

        expect(game.status).toBe("countdown");
    });

    // Test 5 - Game enters playing state when countdown ends
    it("changes game status to countdown when game starts", () => {
        // Pretend the game is currently counting down and has reached 1
        const game = {
            status: "countdown",
            countdown: 1
        }

        gameCountdown(game);

        expect(game.status).toBe("playing");
    });

    // Test 6 - Game timer increases while playing
    it("game timer goes up when the game starts playing", () => {
        const game = {
            status: "playing",
            timer: 0
        }

        gameTimer(game);

        expect(game.timer).toBe(1);
    });

    // Test 6B - Timer does not increase when game is not playing
    it("Game timer does not increase when player is not playing the game", () => {
        const game = {
            status: "menu",
            timer: 0
        }

        gameTimer(game);

        expect(game.timer).toBe(0);
    });

    // Test 7 - Rocket is created
    it("Creating a game rocket", () =>{
        const game = {
            rocket: null
        };

        createRocket(game);

        expect(game.rocket).not.toBeNull();
    });

    // Test 8 - Rocket moves right
    it("Rocket moves right" , () => {
        const game = {
            rocket: {
                x: 100,
                speed: 5
            }
        }

        moveRocketRight(game);

        expect(game.rocket.x).toBe(105);
    });


    // Test 9 - Rocket moves left
    it("Rocket moves left" , () => {
        const game = {
            rocket: {
                x: 100,
                speed: 5
            }
        }

        moveRocketLeft(game);

        expect(game.rocket.x).toBe(95);

    });

    // Test 10A - Rocket stays inside game area RIGHT
    it("Rocket stays inside the game area when it hits RIGHT edge" , () => {
        const game = {
            rocket: {
                x: 800,
                speed: 5
            }
        }

        moveRocketRight(game);
        expect(game.rocket.x).toBe(800);
    });

    // Test 10A - Rocket stays inside game area LEFT
    it("Rocket stays inside canvas when it hits LEFT edge" , () => {
        const game = {
            rocket: {
                x: 0,
                speed: 5
            }
        }

        moveRocketLeft(game);

        expect(game.rocket.x).toBe(0);
    });

    // Test 11 - Player starts with 3 lives
    it("Player starts with 3 lives when game starts", () => {
        const game = {
            lives: 0
        }

        startGame(game);

        expect(game.lives).toBe(3);

    });

    // Test 12 - Player loses a life
    it("deducts one life from the player", () => {
        const game = {
            lives: 3
        }

        loseLife(game);
        expect(game.lives).toBe(2);
    } );

    // Test 13 - Game over at 0 lives
    it("game over displays when the player has no lives left",  () => {
        const game = {
            lives: 1,
            status: "playing"
        }

        loseLife(game);
        expect(game.status).toBe("gameOver");
    });

    // Test 14 - Bullet is created
    it("players bullet is created", () =>{
        const game = {
            bullets: []
        }

        createBullet(game);
        expect(game.bullets.length).toBe(1);
    });

    // Test 15 - Bullet moves
    it("bullet shoots upwards", () => {
        const game = {
            bullets: [
                {
                    y: 500,
                    speed: 10
                }
            ]
        }

        moveBullet(game);
        expect(game.bullets[0].y).toBe(490);
    });

    // Test 16 - Asteroid is created
    it("Asteroid is created", () => {
        const game = {
            asteroids: []
        }

        createAsteroid(game);
        expect(game.asteroids.length).toBe(1);
    });

    // Test 17 - Bullet hits asteroid
    it("bullet hits the asteroid", () => {
        const game = {
            bullets: [
                {
                    x: 100,
                    y: 100,
                }
            ],
            asteroids: [
                {
                    x: 100,
                    y: 100
                }
            ]
        }

        const result = bulletHitAsteroid(game);
        expect(result).toBe(true);

    });

    // Test 18 - Asteroid is destroyed
    it("asteroid is destroyed after a hit", () => {
        const game = {
            asteroids: [
                {}
            ]
        }

        destroyAsteroid(game);
        expect(game.asteroids.length).toBe(0);
    });

    // Test 19 - Score increases
    it("score increases when player destroys an asteroid", () => {
        const game = {
            score: 0
        }

        addScore(game);
        expect(game.score).toBe(10);
    });

    // Test 20 - Score decreases on collision
    it("score decreases when a player is hit", () =>{
        const game = {
            score: 20
        }

        removeScore(game);
        expect(game.score).toBe(10);
    });

    // Test 21 - Restart resets game
    it("restarting the game resets the important values", () => {
        const game = {
            status: "gameOver",
            score: 40,
            lives: 0,
            timer: 25
        }

        restartGame(game);
        expect(game.status).toBe("countdown");
        expect(game.score).toBe(0);
        expect(game.lives).toBe(3);
        expect(game.timer).toBe(0);
    })
});

// Test 22 - game stays in countdown while countdown is above 0
it("game stays in countdown while countdown is above 0", () => {
    const game = {
        status: "countdown",
        countdown: 3
    }

    gameCountdown(game);
    expect(game.countdown).toBe(2);
    expect(game.status).toBe("countdown");
});

// Test 23 - rocket starts at position on canvas
it("rocket starts at x position 375", () => {
    const game = {
        rocket: null
    }

    createRocket(game);
    expect(game.rocket.x).toBe(375);
});

// Test 24 - rocket starts at position on canvas
it("rocket starts at y position 525", () => {
    const game = {
        rocket: null
    }

    createRocket(game);
    expect(game.rocket.y).toBe(525);
});

// Test 25 - rocket speed
it("rocket has a movement speed of 5", () => {
    const game = {
        rocket: null
    };

    createRocket(game);
    expect(game.rocket.speed).toBe(5);
});

//rocket size/width
it("rocket has a width of 50", () => {

})