export function startGame(game) {
    game.running = true;
    game.countdown = 3;
    game.status = "countdown";
    game.lives = 3;

}

export function gameCountdown(game) {
    game.countdown--;

    if(game.countdown === 0){
       game.status = "playing";
   } else {
       game.status = "countdown";
   }

}

export function gameTimer(game) {
    if (game.status === "playing") {
        game.timer++;
    }
}

export function createRocket(game) {
    game.rocket = {
        x: 375,
        y: 525,
        speed: 5
    };
}

export function moveRocketRight(game) {
    if (game.rocket.x + game.rocket.speed <= 800) {
        game.rocket.x += game.rocket.speed;
    }
}

export function moveRocketLeft(game) {
    if (game.rocket.x - game.rocket.speed >= 0) {
        game.rocket.x -= game.rocket.speed;
    }
}

export function loseLife(game){
    game.lives--;

    if(game.lives === 0){
        game.status = "gameOver";
    }
}

export function createBullet(game){
    game.bullets.push({});
}

export function moveBullet(game) {
    game.bullets[0].y -= game.bullets[0].speed;
}

export function bulletHitAsteroid(game) {
    const bullet = game.bullets[0];
    const asteroid = game.asteroids[0];

    if (bullet.x === asteroid.x && bullet.y === asteroid.y) {
        return true;
    } else {
        return false;
    }
}

export function createAsteroid(game){
    game.asteroids.push({});
}

export function destroyAsteroid(game) {
    //0 = start at first item of the array
    //1 = remove one item from array
    game.asteroids.splice(0, 1);
}

export function addScore(game){
    game.score += 10;
}

export function removeScore(game){
    game.score -= 10;
}

export function restartGame(game) {
    startGame(game);
    game.timer = 0;
    game.score = 0;
}