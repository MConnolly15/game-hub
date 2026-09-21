export function startGame(game) {
    game.running = true;
    game.countdown = 3;
    game.status = "countdown";
    game.lives = 3;
    game.timer = 60;

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
    if (game.status === "playing" && game.timer > 0) {
        game.timer--;

        if (game.timer === 0) {
            game.status = "gameOver";
        }
    }
}

export function createRocket(game) {
    game.rocket = {
        x: 375,
        y: 525,
        speed: 5,
        width: 70,
        height: 70
    };
}

export function moveRocketRight(game) {
    if (game.rocket.x + game.rocket.speed + game.rocket.width <= 800) {
        game.rocket.x += game.rocket.speed;
    }
}

export function moveRocketLeft(game) {
    if (game.rocket.x - game.rocket.speed >= 0) {
        game.rocket.x -= game.rocket.speed;
    }
}

export function loseLife(game){
    if (game.lives > 0) {
        game.lives--;
    }

    if (game.lives === 0){
        game.status = "gameOver";
    }
}

export function createBullet(game){
    const bulletWidth = 14;
    const bulletHeight = 28;

    game.bullets.push({
        width: bulletWidth,
        height: bulletHeight,
        speed: 10,
        x: game.rocket.x + game.rocket.width / 2 - bulletWidth / 2,
        y: game.rocket.y - bulletHeight
    });
}

export function moveBullet(game) {
    for (const bullet of game.bullets){
        bullet.y -= bullet.speed;
    }
}

export function bulletHitAsteroid(game) {

    for (const asteroid of game.asteroids) {

        for (const bullet of game.bullets) {

            if (
                bullet.x < asteroid.x + asteroid.width &&
                bullet.x + bullet.width > asteroid.x &&
                bullet.y < asteroid.y + asteroid.height &&
                bullet.y + bullet.height > asteroid.y
            ) {
                return true;
            }
        }
    }

    return false;
}

export function createAsteroid(game, x = 100){
    game.asteroids.push({
        width: 65,
        height: 65,
        x: x,
        y: 0,
        speed: 16,
        sprite: Math.floor(Math.random() * 3)
    });
}

export function destroyAsteroid(game, asteroidIndex) {
    //0 = start at first item of the array
    //1 = remove one item from array
    game.asteroids.splice(asteroidIndex, 1);
}

export function addScore(game){
    game.score += 10;
}

export function removeScore(game){
    game.score -= 10;
}

export function restartGame(game) {
    startGame(game);
    game.score = 0;
    game.bullets = [];
    game.asteroids = [];
}

export function removeBullet(game, bulletIndex = 0) {
    game.bullets.splice(bulletIndex, 1);
}

export function moveAsteroid(game) {
    for(const asteroid of game.asteroids){
        asteroid.y += asteroid.speed;
    }
}

export function handleBulletAsteroidCollision(game) {

    for (let asteroidIndex = 0; asteroidIndex < game.asteroids.length; asteroidIndex++) {
        const asteroid = game.asteroids[asteroidIndex];

        const asteroidPadding = 12;

        for (let bulletIndex = 0; bulletIndex < game.bullets.length; bulletIndex++) {
            const bullet = game.bullets[bulletIndex];

            const bulletPadding = 3;

            if (
                bullet.x + bulletPadding < asteroid.x + asteroid.width - asteroidPadding &&
                bullet.x + bullet.width - bulletPadding > asteroid.x + asteroidPadding &&
                bullet.y + bulletPadding < asteroid.y + asteroid.height - asteroidPadding &&
                bullet.y + bullet.height - bulletPadding > asteroid.y + asteroidPadding
            ) {
                removeBullet(game, bulletIndex);
                destroyAsteroid(game, asteroidIndex);
                addScore(game);
                return;
            }
        }
    }
}

export function asteroidHitRocket(game) {
    for (const asteroid of game.asteroids) {
        if (
            game.rocket.x < asteroid.x + asteroid.width &&
            game.rocket.x + game.rocket.width > asteroid.x &&
            game.rocket.y < asteroid.y + asteroid.height &&
            game.rocket.y + game.rocket.height > asteroid.y
        ) {
            return true;
        }
    }

    return false;
}

export function handleAsteroidRocketCollision(game) {
    const rocketPadding = 10;
    const asteroidPadding = 8;

    for (let asteroidIndex = 0; asteroidIndex < game.asteroids.length; asteroidIndex++) {
        const asteroid = game.asteroids[asteroidIndex];

        if (
            game.rocket.x + rocketPadding <
            asteroid.x + asteroid.width - asteroidPadding &&

            game.rocket.x + game.rocket.width - rocketPadding >
            asteroid.x + asteroidPadding &&

            game.rocket.y + rocketPadding <
            asteroid.y + asteroid.height - asteroidPadding &&

            game.rocket.y + game.rocket.height - rocketPadding >
            asteroid.y + asteroidPadding
        ) {
            loseLife(game);
            destroyAsteroid(game, asteroidIndex);
            return;
        }
    }
}

export function removeOffscreenAsteroids(game) {
    game.asteroids = game.asteroids.filter((asteroid) => {
        return asteroid.y < 600;
    });
}

export function removeOffscreenBullets(game) {
    game.bullets = game.bullets.filter((bullet) => {
        return bullet.y > 0;
    });
}