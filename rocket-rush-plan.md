# Rocket Rush Plan

## Brainstorm
    - Rocket moves around the screen
    - Dodge asteroids   
    - can shoot weapons at asteroids to blow them up
    - You collect points for blowing up asteriods, extra points for collecting things
    - Charge weapon, pulse type weapon, lasers, bullets, torpedos 
    - 3 lives, can collect hearts for extra lives 
    - Leaderboard, Score, username
    - Score goes up with time, or blowing stuff up, lose points if hit objects
    - Shields 
    - Find game stock images/backgrounds, guis etc
    - boosters to dodge things quicker
    - Space type background
    - Sounds maybe?
    - Small menu to display start game, and then game over when you lose

## MVP
    - Start the game
    - Rocket appears on screen
    - Players can move the rocket 
    - Things appear on screen (asteroids etc)
    - Player has lives
    - Player can lose a life
    - Player can shoot 
    - Player can dodge 
    - Score can go up/down
    - Players can lose
    - Game over appears
    - players can restart the game

## Extras
    - Enemy ships can fire at the player

## 1. Requirements
    - Start game button
    - Game displays with a count down 3...2...1... GO!
    - Player starts with 3 lives
    - Game timer starts
    - Rocket appears when count down is over
    - Player can move the rocket around the screen
    - Player can fire weapons at asteroids
    - Player score can go up or down
    - Player loses a life when asteroid hits
    - Player can lose the game when they run out of lives
    - Player can gain a life by collecting a heart 
    - Game over will be displayed to the player
    - Player can start a new game
    - Leaderboard displayed below game


## 2. User stories
    - As a player, I want to see a game menu so that I can start a new game
    - As a player, I want to see my current high score on the menu so that I know what score I am trying to beat
    - As a player, I want to start the game with 3 lives so that I have more than one chance before the game ends
    - As a player, I want to move my rocket around the screen so that I can avoid asteroids
    - As a player, I want to shoot asteroids so that I can destroy them and earn points
    - As a player, I want my score to increase when I destroy asteroids so that I am rewarded for successful hits
    - As a player, I want my score to decrease when I am hit by an asteroid so that collisions have a penalty
    - As a player, I want to collect hearts so that I can gain an extra life
    - As a player, I want to see a running timer so that I know how long I have survived
    - As a player, I want the timer to stop when the game ends so that I can see my final survival time
    - As a player, I want to see a GAME OVER screen showing my final score and time so that I can see how well I performed
    - As a player, I want to be able to play again after the game ends so that I can try to beat my previous score
    - As a player, I want to see a leaderboard so that I can compare my score with other players

## 3. TDD / Test Cases
### Test 1 - Start game
    - Start a new game
    - Expected: game is running

### Test 2 - Countdown starts at 3
    - Game starts
    - Expected: Countdown begins at 3

### Test 3 - Countdown is running
    - Countdown is running
    - Expected: Countdown stops at 0

### Test 4 - Game timer starts
    - Countdown finishes at 0
    - Expected: Game timer starts counting

### Test 5 - Rocket is created
    - Countdown finishes at 0
    - Expected: Rocket will appear on screen

### Test 6 - Rocket can move right
    - Rocket moves right
    - Expected: Rocket moves right

### Test 7 - Rocket can move left
    - Rocket moves left
    - Expected: Rocket moves left

### Test 8 - Rocket stays within the game area
    - Rocket tries to move past the edge
    - Expected: Rocket stays within the game area

### Test 9 - Game timer starts
    - Countdown finishes
    - Expected: Game timer starts

### Test 10 - Lives displayed
    - Player has 3 lives
    - Expected: 3 lives are displayed <3 <3 <3

### Test 11 - Player starts with 3 lives
    - Start a new game
    - Expected: Player has 3 lives

### Test 12 - Lose a life
    - Player with 3 lives hits an asteroid
    - Expected: Player has 2 lives

### Test 13 - Lose second life
    - Player with 2 lives hits an asteroid
    - Expected: Player has 1 life

### Test 14 - Game over when player reaches 0 lives
    - Player with 1 life hits an asteroid
    - Expected: Player has no lives left, game ends

### Test 15- Bullet is created
    - Player fires the bullet and bullet is created
    - Expected: Bullet comes out of rocket

### Test 16 - Player fires bullet
    - Player fires bullet
    - Expected: Bullet moves 

### Test 17 - Bullet hits asteroid
    - Player shoots at asteroid
    - Expected: Bull hits asteroid

### Test 18 - Asteroid is destroyed
    - Bullet hits asteroid 
    - Expected: Asteroid is destroyed

### Test 13 - Player score increases
    - Player shoots asteroid
    - Expected: Player score goes up

### Test 14 - Score decreases during impact
    - Asteroid collides with rocket
    - Expected: Players score decreases

### Test 15 - Game over screen appears
    - Player loses final life
    - Expected: GAME OVER is displayed

### Test 16 - Final score is shown
    - Game ends
    - Expected: Final score is displayed

### Test 17 - Final time is shown
    - Game ends
    - Expected: Final survival time is displayed

### Test 18 - Restart game
    - Game is over
    - Expected: Player can start a new game

### Test 19 - Restart resets game
    - Player starts a new game
    - Expected: Score, timer and lives reset


## 4. Build Order