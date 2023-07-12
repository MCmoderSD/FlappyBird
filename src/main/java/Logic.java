import static java.lang.Thread.sleep;

@SuppressWarnings("BlockingMethodInNonBlockingContext")
public class Logic {
    // Klassenobjekte
    public static Logic instance;
    private final Config config;
    private final Utils utils;
    private final Movement movement;
    private final GameUI gameUI;

    // Klassenvariablen
    public boolean
            gamePaused = false,
            rainbowMode = false,
            rainbowModeActive = false,
            developerMode = false,
            cheatsEnabled = false,
            gameState = false,
            gameOver = false;

    // Konstruktor und Intanz bildung der Klasse
    public Logic(Config config, GameUI gameUI) {
        instance = this;
        this.config = config;
        this.utils = config.getUtils();
        this.movement = config.getMovement();
        this.gameUI = gameUI;
    }

    // Handler für die Leertaste
    public void handleSpaceKeyPress() {

        // Wenn das Spiel nicht läuft und nicht beendet ist
        if (!(gameUI.tickrate.isRunning() || gameState || gameOver)) {
            utils.audioPlayer(config.getMusic(), config.isSound(), true); // Musik abspielen
            gameUI.tickrate.start(); // Timer starten
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

            if ((gameUI.player.getY() >= gameUI.getHeight() && !gameState && gameOver) || (gameOver && config.getArgs().length > 1)) gameUI.tickrate.stop(); // Stop the timer
            movement.movePlayer(); // Move the player


            if (gameState && !gameOver) {
                movement.moveObstacles(); // Move the obstacles
                movement.moveBackground(); // Move the background

                gameUI.removeObstacles(); // Remove non-visible obstacles
                gameUI.checkCollision(); // Check for collisions
                gameUI.checkRainbowMode(); // Check for rainbow mode
            }
        }
    }

    // Handler für die Kollision
    public void handleCollision() {
        utils.stopHeavyAudio();
        utils.audioPlayer(config.getDieSound(), config.isSound(), false);

        gameOver = true;
        gameState = false;

        gameUI.gameOver.setVisible(true);
    }

    // Handler für den Jump
    public void handleBounce() {
        utils.audioPlayer(config.getFlapSound(), config.isSound(), false);
        if (gameUI.player.getY() > 32) movement.xPosition = - config.getJumpHeight(); // Spieler nach oben bewegen
    }

    // Handler für die Punkte
    public void handlePoint() {
        utils.audioPlayer(config.getPointSound(), config.isSound(), false);
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
                utils.audioPlayer(config.getRainbowSound(), config.isSound(), false);
                sleep(7000);
                rainbowMode = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
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