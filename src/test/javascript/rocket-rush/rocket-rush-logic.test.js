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
    restartGame,
    removeBullet,
    moveAsteroid,
    handleBulletAsteroidCollision,
    asteroidHitRocket,
    handleAsteroidRocketCollision,
    removeOffscreenAsteroids,
    removeOffscreenBullets
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
            timer: 60
        }

        gameTimer(game);

        expect(game.timer).toBe(59);
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
                speed: 5,
                width: 50
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
            rocket: {
                x: 375,
                width: 50
            },
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
                    width: 10,
                    height: 20
                }
            ],
            asteroids: [
                {
                    x: 100,
                    y: 100,
                    width: 50,
                    height: 50
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
        expect(game.timer).toBe(60);
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

// Test 26 - rocket size/width
it("rocket has a width of 70", () => {
    const game = {
        rocket: null
    }

    createRocket(game);

    expect(game.rocket.width).toBe(70);
});

//Test 27 - rocket has a height
it("rocket has a height of 70", () => {
    const game = {
        rocket: null
    }

    createRocket(game);
    expect(game.rocket.height).toBe(70);
});

// Test 28 - rocket boundary
it("rocket should not go past the boundary", () => {
    const game = {
        rocket: {
            x: 750,
            width: 50,
            speed: 5
        }
    };

    moveRocketRight(game);
    expect(game.rocket.x).toBe(750);
});

//Test 29 - creating bullets width
it("bullet has a width of 14", () => {
    const game = {
        rocket: {
            x: 375,
            width: 50
        },
        bullets: []
    };

    createBullet(game);
    expect(game.bullets[0].width).toBe(14);
});

//Test 30 - creating bullets height
it("bullet has a height of 28", () => {
    const game = {
        rocket:{
            x: 375,
            width: 50
        },
        bullets: []
    };

    createBullet(game);
    expect(game.bullets[0].height).toBe(28);
});

// Test 31 - bullet movement
it("bullet movement with a speed of 10", () => {
    const game = {
        rocket: {
            x: 375,
            width: 50
        },
        bullets: []
    };

    createBullet(game);
    expect(game.bullets[0].speed).toBe(10);
});

// Test 32 - bullet middle of rocket
it("bullet starts in the middle of the rocket", () => {
    const game = {
        rocket: {
            x: 375,
            width: 50
        },
        bullets: []
    };

    createBullet(game);

    expect(game.bullets[0].x).toBe(393);
});

// Test 33, bullet sits directly above rocket
it("bullet to appear above the rocket", () => {
    const game = {
        rocket: {
            x: 375,
            y: 525,
            width: 50
        },
        bullets: []
    };

    createBullet(game);
    expect(game.bullets[0].y).toBe(497);
});

// Test 34, bullet disappears
it("bullet disappears when it hits the top of the canvas", () => {
    const game = {
        bullets: [
            {}
        ]
    }

    removeBullet(game);
    expect(game.bullets.length).toBe(0);
});

// Test 35, moveBullet() should move every bullet not just the first
it("moves every bullet upwards", () => {
    const game = {
        bullets: [
            {
                y: 500,
                speed: 10
            },
            {
                y: 400,
                speed: 10
            }
        ]
    };

    moveBullet(game);
    expect(game.bullets[0].y).toBe(490);
    expect(game.bullets[1].y).toBe(390);
});

// Test 36,asteroid width
it("asteroid has a width of 65", () => {
    const game = {
        asteroids: []
    };

    createAsteroid(game);
    expect(game.asteroids[0].width).toBe(65);
});

// Test 37 - asteroid height
it("asteroid has a height of 65", () => {
    const game = {
        asteroids: []
    };

    createAsteroid(game);

    expect(game.asteroids[0].height).toBe(65);
});

// Test 38 - asteroid starting x position
it("asteroid starting x position", () => {
    const game = {
        asteroids: []
    };

    createAsteroid(game);
    expect(game.asteroids[0].x).toBe(100);
});

// Test 39 - asteroid y position
it("asteroid x position", () => {
    const game = {
        asteroids: []
    };

    createAsteroid(game);
    expect(game.asteroids[0].y).toBe(0);
});

// Test 40 - asteroid falling speed
it("asteroid falling speed, 16", () => {
    const game = {
        asteroids: []
    };

    createAsteroid(game);
    expect(game.asteroids[0].speed).toBe(16);
});

// Test 41 - asteroid moves down
it("asteroid moves down on screen", () => {
    const game = {
        asteroids: [
            {
                y: 100,
                speed: 10
            }
        ]
    };

    moveAsteroid(game);
    expect(game.asteroids[0].y).toBe(110);
});

// Test 42 - asteroids at different positions
it("asteroid fall from different positions", () => {
    const game = {
        asteroids: []
    };

    createAsteroid(game, 300);
    expect(game.asteroids[0].x).toBe(300);
});

// Test 43 - move every asteroid
it("move every asteroid", () => {
    const game = {
        asteroids: [
            {
                y: 100,
                speed: 10
            },
            {
                y: 200,
                speed: 10
            }

        ]
    };

    moveAsteroid(game);
    expect(game.asteroids[0].y).toBe(110);
    expect(game.asteroids[1].y).toBe(210);
});

// Test 44 - horizontal overlap(asteroids)
it("asteroids can never overlap", () => {
    const game = {
        bullets: [
            {
                x: 120,
                y: 100,
                width: 10,
                height: 20
            }
        ],
        asteroids: [
            {
                x: 100,
                y: 100,
                width: 50,
                height: 50
            }
        ]
    };

    const result = bulletHitAsteroid(game);
    expect(result).toBe(true);
});

// Test 45 - collision detects another bullet
it("detects when a later bullet hits an asteroid", () => {
    const game = {
        bullets: [
            {
                x: 10,
                y: 10,
                width: 10,
                height: 20
            },
            {
                x: 120,
                y: 100,
                width: 10,
                height: 20
            }
        ],
        asteroids: [
            {
                x: 100,
                y: 100,
                width: 50,
                height: 50
            }
        ]
    };

    const result = bulletHitAsteroid(game);

    expect(result).toBe(true);
});

// Test 46 - collision detects another asteroid
it("detects when a bullet hits a later asteroid", () => {
    const game = {
        bullets: [
            {
                x: 120,
                y: 100,
                width: 10,
                height: 20
            }
        ],
        asteroids: [
            {
                x: 400,
                y: 400,
                width: 50,
                height: 50
            },
            {
                x: 100,
                y: 100,
                width: 50,
                height: 50
            }
        ]
    };

    const result = bulletHitAsteroid(game);

    expect(result).toBe(true);
});

// Test 47 - destroy a specific asteroid
it("destroys the selected asteroid", () => {
    const game = {
        asteroids: [
            { x: 100 },
            { x: 300 }
        ]
    };

    destroyAsteroid(game, 1);

    expect(game.asteroids.length).toBe(1);
    expect(game.asteroids[0].x).toBe(100);
});

// Test 48 - remove a specific bullet
it("removes the selected bullet", () => {
    const game = {
        bullets: [
            { x: 100 },
            { x: 300 }
        ]
    };

    removeBullet(game, 1);

    expect(game.bullets.length).toBe(1);
    expect(game.bullets[0].x).toBe(100);
});

// Test 49 - collision removes bullet and asteroid
it("removes bullet and asteroid when they collide", () => {
    const game = {
        bullets: [
            {
                x: 120,
                y: 100,
                width: 10,
                height: 20
            }
        ],
        asteroids: [
            {
                x: 100,
                y: 100,
                width: 50,
                height: 50
            }
        ]
    };

    handleBulletAsteroidCollision(game);

    expect(game.bullets.length).toBe(0);
    expect(game.asteroids.length).toBe(0);
});

// Test 50 - removes the bullet and asteroid that actually collided
it("removes the correct bullet and asteroid after collision", () => {
    const game = {
        bullets: [
            {
                x: 10,
                y: 10,
                width: 10,
                height: 20
            },
            {
                x: 320,
                y: 300,
                width: 10,
                height: 20
            }
        ],
        asteroids: [
            {
                x: 100,
                y: 100,
                width: 50,
                height: 50
            },
            {
                x: 300,
                y: 300,
                width: 50,
                height: 50
            }
        ]
    };

    handleBulletAsteroidCollision(game);

    expect(game.bullets.length).toBe(1);
    expect(game.asteroids.length).toBe(1);

    expect(game.bullets[0].x).toBe(10);
    expect(game.asteroids[0].x).toBe(100);
});

//Test 51 - shooting asteroids adds score
it("adds 10 points when an asteroid is destroyed", () => {
    const game = {
        score: 0,
        bullets: [
            {
                x:120,
                y: 100,
                width: 50,
                height: 50
            }
        ],
        asteroids: [
            {
                x:100,
                y:100,
                width: 50,
                height: 50
            }
        ]
    };

    handleBulletAsteroidCollision(game);
    expect(game.score).toBe(10);
});

// Test 52 - asteroid hits rocket
it("detects when an asteroid hits the rocket", () => {
    const game = {
        rocket: {
            x: 100,
            y: 500,
            width: 50,
            height: 50
        },
        asteroids: [
            {
                x: 120,
                y: 500,
                width: 50,
                height: 50
            }
        ]
    };

    const result = asteroidHitRocket(game);

    expect(result).toBe(true);
});

// Test 53 - asteroid collision removes one life
it("removes one life when an asteroid hits the rocket", () => {
    const game = {
        lives: 3,
        rocket: {
            x: 100,
            y: 500,
            width: 50,
            height: 50
        },
        asteroids: [
            {
                x: 120,
                y: 500,
                width: 50,
                height: 50
            }
        ]
    };

    handleAsteroidRocketCollision(game);

    expect(game.lives).toBe(2);
});

// Test 54 - asteroid disappears after hitting rocket
it("removes the asteroid after it hits the rocket", () => {
    const game = {
        lives: 3,
        rocket: {
            x: 100,
            y: 500,
            width: 50,
            height: 50
        },
        asteroids: [
            {
                x: 120,
                y: 500,
                width: 50,
                height: 50
            }
        ]
    };

    handleAsteroidRocketCollision(game);

    expect(game.asteroids.length).toBe(0);
});

// Test 55 - removes the asteroid that actually hit the rocket
it("removes the correct asteroid after hitting the rocket", () => {
    const game = {
        lives: 3,
        rocket: {
            x: 100,
            y: 500,
            width: 50,
            height: 50
        },
        asteroids: [
            {
                x: 400,
                y: 100,
                width: 50,
                height: 50
            },
            {
                x: 120,
                y: 500,
                width: 50,
                height: 50
            }
        ]
    };

    handleAsteroidRocketCollision(game);

    expect(game.asteroids.length).toBe(1);
    expect(game.asteroids[0].x).toBe(400);
});

// Test 56 - lives should never go below 0
it("does not let lives go below 0", () => {
    const game = {
        lives: 0,
        status: "gameOver"
    };

    loseLife(game);

    expect(game.lives).toBe(0);
});

// Test 57 - restart clears bullets and asteroids
it("clears bullets and asteroids when the game restarts", () => {
    const game = {
        status: "gameOver",
        score: 30,
        lives: 0,
        timer: 20,
        bullets: [
            { x: 100 }
        ],
        asteroids: [
            { x: 200 }
        ]
    };

    restartGame(game);

    expect(game.bullets.length).toBe(0);
    expect(game.asteroids.length).toBe(0);
});

// Test 58 - removes asteroids that leave the bottom of the canvas
it("removes an asteroid when it leaves the bottom of the canvas", () => {
    const game = {
        asteroids: [
            {
                y: 610,
                height: 50
            }
        ]
    };

    removeOffscreenAsteroids(game);

    expect(game.asteroids.length).toBe(0);
});

// Test 59 - removes any bullet that leaves the top of the canvas
it("removes bullets that leave the top of the canvas", () => {
    const game = {
        bullets: [
            {
                y: 200
            },
            {
                y: -10
            }
        ]
    };

    removeOffscreenBullets(game);

    expect(game.bullets.length).toBe(1);
    expect(game.bullets[0].y).toBe(200);
});

// Test 60 - game starts with 60 seconds
it("starts the game timer at 60 seconds", () => {
    const game = {
        timer: 0
    };

    startGame(game);

    expect(game.timer).toBe(60);
});

// Test 61 - timer counts down while playing
it("counts the timer down while the game is playing", () => {
    const game = {
        status: "playing",
        timer: 60
    };

    gameTimer(game);

    expect(game.timer).toBe(59);
});

// Test 62 - game ends when timer reaches 0...
it("ends the game when the timer reaches 0", () => {
    const game = {
        status: "playing",
        timer: 1
    };

    gameTimer(game);

    expect(game.timer).toBe(0);
    expect(game.status).toBe("gameOver");
});