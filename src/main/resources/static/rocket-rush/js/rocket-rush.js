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
    restartGame, removeBullet, moveAsteroid, handleBulletAsteroidCollision, handleAsteroidRocketCollision,
    removeOffscreenAsteroids, removeOffscreenBullets
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

const keys = {
    left: false,
    right: false
}

const canvas = document.getElementById("gameCanvas");
const ctx = canvas.getContext("2d");
const backgroundImage = new Image();
backgroundImage.src = "/rocket-rush/images/background.png";
const rocketImage = new Image();
rocketImage.src = "/rocket-rush/images/rocket.png";
const bulletImage = new Image();
bulletImage.src = "/rocket-rush/images/bullet.png";
const asteroidImage1 = new Image();
asteroidImage1.src = "/rocket-rush/images/asteroid1.png";
const asteroidImage2 = new Image();
asteroidImage2.src = "/rocket-rush/images/asteroid2.png";
const asteroidImage3 = new Image();
asteroidImage3.src = "/rocket-rush/images/asteroid3.png";

const stars = [];

for (let i = 0; i < 40; i++) {
    stars.push({
        x: Math.random() * canvas.width,
        y: Math.random() * canvas.height,
        speed: Math.random() * 3 + 2,
        size: Math.random() * 2 + 1
    });
}


const startButton = document.getElementById("startButton");
const gameTitle = document.querySelector(".game-title");

function drawRocket(){
    if (!game.rocket) {
        return;
    }

    ctx.drawImage(
        rocketImage,
        game.rocket.x,
        game.rocket.y,
        game.rocket.width,
        game.rocket.height
    );
}

function drawBullet(){
    for (const bullet of game.bullets){
        ctx.drawImage(
            bulletImage,
            bullet.x,
            bullet.y,
            bullet.width,
            bullet.height
        );
    }
}

function drawBackground(){
    ctx.drawImage(
        backgroundImage,
        0,
        0,
        canvas.width,
        canvas.height
    );
}

function drawStars(){
    ctx.fillStyle = "white";

    for (const star of stars) {
        ctx.fillRect(
            star.x,
            star.y,
            star.size,
            star.size * 3
        );
    }
}

function moveStars(){
    for (const star of stars) {
        star.y += star.speed;

        if (star.y > canvas.height) {
            star.y = 0;
            star.x = Math.random() * canvas.width;
        }
    }
}

function drawAsteroid(){
    const asteroidImages = [
        asteroidImage1,
        asteroidImage2,
        asteroidImage3
    ];

    for (const asteroid of game.asteroids){
        const asteroidImage = asteroidImages[asteroid.sprite];

        ctx.drawImage(
            asteroidImage,
            asteroid.x,
            asteroid.y,
            asteroid.width,
            asteroid.height
        );
    }
}


function drawScore(){
    ctx.font = "24px Orbitron";
    ctx.textAlign = "left";

    ctx.fillText(
        "Score: " + game.score,
        20,
        35
    );
}

function drawLives(){
    ctx.font = "24px Orbitron";
    ctx.textAlign = "right";

    ctx.fillText(
        "Lives: " + game.lives,
        780,
        35
    );
}

function drawTimer() {
    ctx.font = "24px Orbitron";
    ctx.textAlign = "center";
    ctx.fillText("TIME: " + game.timer, canvas.width / 2, 35);
}

function drawGameOver(){
    ctx.font = "60px Bangers";
    ctx.textAlign = "center";

    ctx.fillText(
        "GAME OVER",
        canvas.width / 2,
        canvas.height / 2
    );

    startButton.style.display = "block";
    startButton.textContent = "PLAY AGAIN";
}

function drawGame(){
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    drawBackground();
    drawStars();
    drawRocket();
    drawTimer();
    drawScore();
    drawLives();

    if(game.bullets.length > 0){
        drawBullet()
    }

    if (game.asteroids.length > 0){
        drawAsteroid();
    }

    if (game.status === "gameOver") {
        drawGameOver();
    }
}

//when clicked run everything inside {}
startButton.addEventListener("click", () => {
    if (game.status === "countdown" || game.status === "playing") {
        return;
    }

    startButton.style.display = "none";
    gameTitle.style.display = "none";

    if (game.status === "gameOver") {
        restartGame(game);
    } else {
        startGame(game);
    }
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = "white";
    ctx.font = "72px Bangers";
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
            createRocket(game);
            const randomX = Math.floor(Math.random() * 750);
            createAsteroid(game, randomX);

            setTimeout(() => {
                drawAsteroid(game);
            }, 1000);
        }
    }, 1000);
});

document.addEventListener("keydown", (event) => {
    if (event.key === "ArrowRight") {
        keys.right = true;
    } else if (event.key === "ArrowLeft") {
        keys.left = true;
    } else if (event.key === " " && !event.repeat) {
        event.preventDefault();
        createBullet(game);
        drawGame();
    }
});

document.addEventListener("keyup", (event) => {
    if (event.key === "ArrowRight") {
        keys.right = false;
    } else if (event.key === "ArrowLeft") {
        keys.left = false;
    }
});

setInterval(() => {
    if (game.status === "playing" && game.bullets.length > 0){
        moveBullet(game);
        removeOffscreenBullets(game);
        handleBulletAsteroidCollision(game);
        drawGame();
    }
}, 50);

setInterval(() => {
    if (game.status === "playing" && game.asteroids.length > 0) {
        moveAsteroid(game);
        removeOffscreenAsteroids(game);
        handleAsteroidRocketCollision(game);
        drawGame();
    }
}, 50);

setInterval(() => {
    if (game.status === "playing") {
        const randomX = Math.floor(Math.random() * 750);

        createAsteroid(game, randomX);
    }
}, 700);

setInterval(() => {
    if (game.status === "playing" && keys.right) {
        moveRocketRight(game);
        drawGame();
    }

    if (game.status === "playing" && keys.left) {
        moveRocketLeft(game);
        drawGame();
    }
}, 16);

setInterval(() => {
    if (game.status === "playing") {
        moveStars();
        drawGame();
    }
}, 50);

setInterval(() => {
    if (game.status === "playing") {
        gameTimer(game);
        drawGame();
    }
}, 1000);