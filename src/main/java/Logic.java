import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import static java.lang.Thread.sleep;

public class Logic {
    public static boolean developerMode = false, cheatsEnabled = false;
    // Klassenobjekte
    private final Config config;
    private final Utils utils;
    private final GameUI gameUI;
    private final Logger logger = LoggerFactory.getLogger(Main.class);
    // Klassenvariablen
    public boolean gamePaused = false, rainbowMode = false, gameOver = false;
    private boolean gameState = false;

    // Konstruktor und Intanz bildung der Klasse
    public Logic(Config config, GameUI gameUI) {
        this.config = config;
        this.utils = config.getUtils();
        //this.movement = config.getMovement();
        this.gameUI = gameUI;
    }

    // Handler für die Leertaste
    public void handleSpaceKeyPress() {

        // Wenn das Spiel nicht läuft und nicht beendet ist
        if (!(gameUI.TimerIsRunning || gameState || gameOver)) {
            //utils.audioPlayer(config.getMusic(), config.isSound(), true, this); // Musik abspielen
            gameUI.TimerIsRunning = true; // Timer starten
            gameUI.gameOver.setVisible(false);
            gameState = true;
            gamePaused = false;


        }

        // Wenn das Spiel nicht läuft und beendet ist
        if (!gameState && gameOver) {
            config.setPoints(gameUI.points);
            new UI(config, utils); // Neues UI erstellen
            gameUI.dispose(); // Aktuelles Fenster schließen
        }

        // Wenn das Spiel nicht beendet ist, Sprung ausführen
        if (!gameOver) handleBounce();
    }

    // Handler für den Timer Tick
    public void handleTimerTick() {

        if (!gamePaused) {

            if ((gameUI.player.getY() >= gameUI.getHeight() && !gameState && gameOver) || (gameOver && config.getArgs().length > 1)) gameUI.TimerIsRunning = false;
            //movement.movePlayer(gameUI); // Move the player


            if (gameState && !gameOver) {
                //movement.moveObstacles(gameUI); // Move the obstacles
                //movement.moveBackground(gameUI); // Move the background

                gameUI.removeObstacles(); // Remove non-visible obstacles
                gameUI.checkCollision(); // Check for collisions
                gameUI.checkRainbowMode(); // Check for rainbow mode
            }
        }
    }

    // Handler für die Kollision
    public void handleCollision() {
        utils.stopHeavyAudio();
        //utils.audioPlayer(config.getDieSound(), config.isSound(), false, this);

        gameOver = true;
        gameState = false;

        gameUI.gameOver.setVisible(true);
    }

    // Handler für den Jump
    public void handleBounce() {
        //utils.audioPlayer(config.getFlapSound(), config.isSound(), false, this);
        //if (gameUI.player.getY() > 32) movement.xPosition = - config.getJumpHeight(); // Spieler nach oben bewegen
    }

    // Handler für die Punkte
    public void handlePoint() {
        //utils.audioPlayer(config.getPointSound(), config.isSound(), false, this);
        gameUI.points++;
        if (gameUI.points > 0 && gameUI.points % 5 == 0 && (int) (Math.random() * 6 + 1) == 3) handleRainbowMode();
        gameUI.score.setText("Score: " + gameUI.points);
    }

    // Handler für die Pause
    public void handleGamePause() {
        if (gamePaused) {
            gameUI.pauseScreen.setVisible(false);
            gamePaused = false;
        } else {
            if (!rainbowMode && !gameOver) {
                gameUI.pauseScreen.setVisible(true);
                gamePaused = true;
            }
        }
    }

    // Handler für den Rainbow Mode
    private void handleRainbowMode() {
        Thread rainbow = new Thread(() -> {
            try {
                rainbowMode = true;
                //utils.audioPlayer(config.getRainbowSound(), config.isSound(), false, this);
                sleep(7000);
                rainbowMode = false;
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        });
        long startTime = System.currentTimeMillis();
        rainbow.start();
        if (System.currentTimeMillis() > startTime + 7000) {
            rainbowMode = false;
            rainbow.interrupt();
        }
    }
}