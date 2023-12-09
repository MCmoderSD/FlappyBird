# Flappy Bird

A Flappy Bird game written in Java 8 with lots of features.
<br>
It's for this year's final IT-software school project.
<br>

## How to run

Currently only Windows is supported. <br>
You need Java 8 installed on your computer to run this game.
You can download it [here](https://www.java.com/de/download/manual.jsp).
<br> <br>
You can download the latest game release [here](https://github.com/MCmoderSD/FlappyBird/releases/latest).
<br> <br> <br>
To run with alternative assets, you need to run the game via command line. <br>
You can only upload your score as long as you use valid assets configurations.
<br>

1. Open the command line
2. Navigate to the folder where the game is located
3. Run the following command: `java -jar FlappyBird.jar <language> <assets> <reverse>`
4. Replace `<language>` with your language, currently supported languages are: `en`, `de`, `it`.
5. If you want to use a custom language look [here](#How-to-use-custom-language)
5. Replace `<assets>` with the path to your [custom assets](#How-to-use-custom-assets) or use one of the valid
   presets: <br>
   `lena`, `911`, `lenabeta`, `911beta`, `alpha`.
6. To play with reverse mode replace for example the <reverse> with `-r` otherwise leave that blank.
   <br> <br>

## How to play

Press `Space` or any mouse button start the game and jump.
<br>
Press `Escape` to pause the game.
<br>
After you die, press `Space` or any mouse button to restart.
<br>
Enter your username and click `Submit` to submit your score to the leaderboard.
<br>
If you don't want to submit your score, leave the username blank and click `Submit`.
<br>
Press `F3` + `B` to toggle the hitbox visualization or `F3` + `F` to toggle the FPS counter.
<br>
Press `F3` + `R` to toggle the reverse mode or `F3` + `C` to change through the assets.
<br> <br>

## How to use custom assets

1. Create a `.json` file with the following structure: <br> For example name it `MyCustomConfig.json`.

```json
{
  "width": 800,
  "height": 800,
  "resizable": false,
  "blockedTermsPath": "PATH TO YOUR BLOCKED TERMS",
  "percentage": 25,
  "gap": 200,
  "jumpHeight": 1.5,
  "gravity": 0.00981,
  "backgroundSpeed": 0.15,
  "obstacleSpeed": 0.25,
  "cloudSpeed": 0.1,
  "rainbowSpawnChance": 0.3,
  "rainbowDuration": 7000,
  "maxFPS": 360,
  "icon": "PATH TO YOUR ICON",
  "backgroundImage": "PATH TO YOUR BACKGROUND",
  "playerImage": "PATH TO YOUR PLAYER",
  "obstacleTopImage": "PATH TO YOUR OBSTACLE TOP",
  "obstacleBottomImage": "PATH TO YOUR OBSTACLE BOTTOM",
  "gameOverImage": "PATH TO YOUR GAME OVER SCREEN",
  "pauseImage": "PATH TO YOUR PAUSE IMAGE",
  "clouds": "PATH TO YOUR VARIANT LIST",
  "rainbowAnimation": "PATH TO YOUR RAINBOW ANIMATION",
  "playerColor": "#ff0000",
  "playerHitboxColor": "#00ff00",
  "cloudColor": "#0000ff",
  "cloudHitboxColor": "#ffff00",
  "obstacleTopColor": "#ff00ff",
  "obstacleTopHitboxColor": "#ff0000",
  "obstacleBottomColor": "#ffffff",
  "obstacleBottomHitboxColor": "#ff0000",
  "obstacleHitboxColor": "#ff0000",
  "safeZoneColor": "#00ff00",
  "safeZoneHitboxColor": "#00ff00",
  "backgroundColor": "#ffffff",
  "fontColor": "#000000",
  "scoreColor": "#ffff00",
  "fpsColor": "#00ff00",
  "dieSound": "PATH TO YOUR DIE SOUND",
  "flapSound": "PATH TO YOUR FLAP SOUND",
  "hitSound": "PATH TO YOUR HIT SOUND",
  "pointSound": "PATH TO YOUR POINT SOUND",
  "rainbowSound": "PATH TO YOUR RAINBOW SOUND",
  "backgroundMusic": "PATH TO YOUR BACKGROUND MUSIC"
}
```

2. In the `PATH TO YOUR VARIANT LIST` you link a variantList`.json` file with the following structure:

```json
{
  "variant0": "PATH TO YOUR VARIANT 1",
  "variant1": "PATH TO YOUR VARIANT 2",
  "variant2": "PATH TO YOUR VARIANT 3"
}
```   

and so on, you can add as many variants as you want, you need at least one.

3. Replace the values with the absolute path to your assets.
   <br> <br> For example: `"Background": "C:\\Users\\User\\Desktop\\MyBackground.png"`
   <br> You can only use `.png` files and for audio only `.wav` files encoded with 16 bit.
   <br> <br> The size of the assets should be the same as the default assets.
   <br> Pipes should be around 32x1024 px and the player around 32x32 px.
   <br> The background should be bigger than the window width and height.
   <br> Rainbow should the same size as the player and `.gif` format.
   <br> <br>
4. Run the game with the custom config file.
   <br> For example: `java -jar FlappyBird.jar en <PathToTheConfigFile>` <br> <br>

## How to use custom language

1. Create a `.json` file with the following structure: <br> For example name it `MyCustomLanguage.json`.

```json
{
  "title": "Flappy Bird",
  "score": "Score",
  "username": "Username",
  "usernameToolTip": "Enter your username",
  "rank": "Rank",
  "scorePrefix": "Score: ",
  "fpsPrefix": "FPS: ",
  "start": "Start",
  "startToolTip": "Start the game",
  "sound": "Sound",
  "soundToolTip": "Turn sound on/off",
  "fpsToolTip": "Set FPS",
  "cheatsDetected": "Cheats detected!",
  "cheatsDetectedTitle": "My anti-cheat detected that you've modified the Memory of the game.",
  "instruction": "Fill in your username and click confirm to upload your score of ",
  "confirm": "Submit",
  "confirmToolTip": "Confirm your username and upload your score",
  "invalidUsername": "Your username is not valid! Please try again.",
  "invalidUsernameTitle": "Invalid username!"
}
```

2. Replace the values with your language.
   <br> <br>
3. Run the game with the custom language file. <br>
   For example: `java -jar FlappyBird.jar <PathToTheLanguageFile> <assets> <reverse>` <br>
   <br>
4. If you want to contribute to the project, you can create an issue or pull request with your language file.

## Features

- Leaderboard
- Reverse mode
- Custom assets
- Custom Language
- Customizable window size
- Everything is customizable
- Hitbox visualization
- FPS counter

## Credits

Special thanks to: <br>

[Nini](https://www.instagram.com/nini_125x/) the primary artist for the beautiful assets. <br>
[RedSmileTV](https://github.com/RedSmileTV) for being my rubber duck and translator.