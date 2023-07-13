# Flappy Bird

A Flappy Bird game written in Java.
<Br>
It's for this year's final IT-software school project.
<br> <br>
## How to run
You need Java 8 installed on your computer to run this game.
You can download it [here](https://www.java.com/de/download/manual.jsp).
<br> <br>
You can download the latest game release [here](https://github.com/MCmoderSD/FlappyBird/releases/latest).
<br> <br> <br>
To run with alternative assets, you need to run the game via command line.
<br>
1. Open the command line
2. Navigate to the folder where the game is located
3. Run the following command: `java -jar FlappyBird.jar <firstArgument> <secondArgument>`
4. Replace `<firstArgument>` with the path to the asset config file you want to run. `911`, `911beta`, `alpha`, `lenabeta`. <br> The Default-Config is: `lena`
5. Put as second argument whatever you want, to run in reverse mode.
   As example put `reverse`
   <br> <br>
## How to play
Press `Space` or any mouse button to jump.
<br>
Press `Escape` to pause the game.
<br>
After you died, press `Space` or any mouse button to restart.
<br>
Enter your username and click `Submit` to submit your score to the leaderboard.
<br>
If you don't want to submit your score, leave the username field empty and click `Submit`.
<br> <br>
## How to use custom assets
1. Create a `.json` file with the following structure: <br> For example name it `MyCustomConfig.json`.
```json
{
  "Title": "YOUR TITLE",
  "Background": "PATH TO YOUR BACKGROUND",
  "Player": "PATH TO YOUR PLAYER",
  "Rainbow": "PATH TO YOUR RAINBOW",
  "ObstacleTop": "PATH TO YOUR OBSTACLE TOP",
  "ObstacleBottom": "PATH TO YOUR OBSTACLE BOTTOM",
  "Icon": "PATH TO YOUR ICON",
  "GameOver": "PATH TO YOUR GAME OVER SCREEN",
  "Pause": "PATH TO YOUR PAUSE SCREEN",
  "dieSound": "PATH TO YOUR DIE SOUND",
  "flapSound": "PATH TO YOUR FLAP SOUND",
  "hitSound": "PATH TO YOUR HIT SOUND",
  "pointSound": "PATH TO YOUR POINT SOUND",
  "rainbowSound": "PATH TO YOUR RAINBOW SOUND",
  "backgroundMusic": "PATH TO YOUR BACKGROUND MUSIC",
  "WindowSizeX": "YOUR WINDOW WIDTH",
  "WindowSizeY": "YOUR WINDOW HEIGHT",
  "Resizeable": "true/false"
}
```
2. Replace the values with the absolute path to your assets.
   <br> <br> For example: `"Background": "C:\\Users\\User\\Desktop\\MyBackground.png"`
   <br> You can only use `.png` files and for audio only `.wav` files encoded with 16 bit.
   <br> <br> The size of the assets should be the same as the default assets.
   <br> Pipes should be around 32x1024 px and the player around 32x32 px.
   <br> The background should be bigger than the window width and height.
   <br> Rainbow should the same size as the player and `.gif` format.
   <br> <br>
3. Run the game with the custom config file.
   <br> For example: `java -jar FlappyBird.jar <PathToTheConfigFile>` <br> <br>
## Features

- [x] Assets
- [x] Player movement
- [x] Pipe movement
- [x] Pipe generation
- [x] Collision
- [x] Score
- [x] Background movement
- [x] Smooth Infinite Background
- [x] Game-over
- [x] Restart
- [x] Menu
- [x] Sounds
- [x] Rainbow Mode (PowerUp)
- [x] Developer Mode
- [x] Music
- [x] Variable Tickrate
- [x] Pause
- [x] Username Checker
- [x] Highscore
- [x] Leaderboard
- [x] MySQL Database 
- [x] Offline Mode
- [x] Game Optimization
- [x] Further Optimization
- [x] Reverse Mode 
- [x] Reverse Mode Leaderboard
- [x] Mod Support (Custom Assets)
- [x] Code de-spaghetification
- [ ] Maxim-test passed 
- [ ] Timon-test passed 

## Credits

Primary Developer: [MCmoderSD](https://github.com/MCmoderSD/) <br>
Secondary Developer: [Hamburger](https://github.com/HamburgerPaul) <br> <br>
Special thanks to: <br>
[Nini](https://www.instagram.com/nini_125x/) the primary artist for the beautiful assets.
<Br>
[Rebix](https://github.com/Reebix) for the database driver and coding help.
<Br>
[RedSmileTV](https://github.com/RedSmileTV) for beeing my rubber duck.
<Br>
[Leonard](https://github.com/Leo-160905) for small ideas and code improvements.
<Br>
[Redflame125](https://github.com/Redflame125) for being the main tester.