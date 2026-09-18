export function startGame(game){
    game.running = true;
    game.countdown = 3;
    game.status = "countdown";
}

export function gameCountdown(game){
    game.countdown--;
    game.status = "playing";
}