import { startGame, gameCountdown } from "../../../main/resources/static/rocket-rush/js/rocket-rush-logic.js";
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

    // Test 6 - Game timer starts

    // Test 7 - Rocket is created

    // Test 8 - Rocket moves right

    // Test 9 - Rocket moves left

    // Test 10 - Rocket stays inside game area

    // Test 11 - Player starts with 3 lives

    // Test 12 - Player loses a life

    // Test 13 - Game over at 0 lives

    // Test 14 - Bullet is created

    // Test 15 - Bullet moves

    // Test 16 - Bullet hits asteroid

    // Test 17 - Asteroid is destroyed

    // Test 18 - Score increases

    // Test 19 - Score decreases on collision

    // Test 20 - Restart resets game

});