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
} from "./rocket-rush-logic.js";

const game = {
    running: false,
    status: "menu",
    countdown: 0,
    timer: 0,
    score: 0,
    lives: 3,
    rocket: null,
    bullets: [],
    asteroids: []
}

const canvas = document.getElementById("gameCanvas");
const ctx = canvas.getContext("2d");

const startButton = document.getElementById("startButton");

//when clicked run everything inside {}
startButton.addEventListener("click", () => {
    startGame(game);
    ctx.fillStyle = "white";
    ctx.font = "60px Arial";
    ctx.textAlign = "center";

    ctx.fillText(
        game.countdown,
        canvas.width / 2,
        canvas.height / 2
    );

    const countdownInterval = setInterval(() => {
        gameCountdown(game);

        ctx.clearRect(0,0, canvas.width, canvas.height);

        if (game.countdown > 0 ){
            ctx.fillText(
                game.countdown,
                canvas.width / 2,
                canvas.height / 2
            );
        } else {
            clearInterval(countdownInterval);
            ctx.fillText(
                "GO!",
                canvas.width / 2,
                canvas.height / 2
            );
            setTimeout(() => {
                ctx.clearRect(0,0, canvas.width, canvas.height);
            }, 1000);

        }
    }, 1000);
});