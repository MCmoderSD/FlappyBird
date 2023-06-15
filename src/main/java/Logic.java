/**

 Die Logic-Klasse repräsentiert die Spiellogik eines Spiels.
 */
public class Logic {
    public static Logic instance;
    public boolean gamePaused = false, rainbowMode = false, rainbowModeActive = false, developerMode = false, cheatsEnabled = false;
    private boolean gameState = false, gameOver = false;
    private final GameUI gameUI;

    public Logic(GameUI gameUI, UI ui, Utils utils, Movement movement) {
        this.gameUI = gameUI;
        instance = this;
    }

    /**
     Behandelt das Ereignis des Drückens der Leertaste.

     @param utils das Utils-Objekt

     @param movement das Movement-Objekt

     @param width die Breite der Spieloberfläche (UI)

     @param height die Höhe der Spieloberfläche (UI)

     @param title der Titel der Spieloberfläche (UI)

     @param icon das Icon der Spieloberfläche (UI)

     @param resizable gibt an, ob die Spieloberfläche (UI) veränderbar ist

     @param backgroundImage der Pfad zum Hintergrundbild

     @param flapSound der Pfad zum Flatter-Soundeffekt

     @param Tickrate der Tickrate-Wert

     @param sound gibt an, ob der Sound aktiviert ist

     @param args die Argumente
     */
    public void handleSpaceKeyPress(Utils utils, Movement movement, int width, int height, String title, String icon, boolean resizable, String backgroundImage, String flapSound, int Tickrate, boolean sound, String[] args) {

// Wenn das Spiel nicht läuft und nicht beendet ist
        if (!gameUI.tickrate.isRunning() && !gameState && !gameOver) {
            gameUI.tickrate.start(); // Timer starten
            gameUI.gameOver.setVisible(false);
            gameState = true;
            gamePaused = false;
        }

// Wenn das Spiel nicht läuft und beendet ist
        if (!gameUI.tickrate.isRunning() && !gameState && gameOver) {
            new UI(utils, movement, width, height, title, icon, resizable, backgroundImage, Tickrate, sound, args, gameUI.points); // Fenster erneut initialisieren
            gameUI.dispose(); // Aktuelles Fenster schließen
        }

// Wenn das Spiel nicht beendet ist, Sprung ausführen
        if (!gameOver) {
            handleBounce(utils, movement, flapSound, sound);
        }
    }

    /**
     Behandelt das Ereignis des Timer-Ticks.

     @param utils das Utils-Objekt

     @param movement das Movement-Objekt

     @param height der Höhenwert

     @param playerImage der Pfad zum Spielerbild

     @param rainbowImage der Pfad zum Regenbogenbild

     @param percentage der Prozentsatzwert

     @param verticalGap der vertikale Lückenwert

     @param obstacleTopImage der Pfad zum Hindernisbild oben

     @param obstacleBottomImage der Pfad zum Hindernisbild unten

     @param dieSound der Pfad zum Sterben-Soundeffekt

     @param hitSound der Pfad zum Kollisions-Soundeffekt

     @param pointSound der Pfad zum Punkte-Soundeffekt

     @param rainbowSound der Pfad zum Regenbogen-Soundeffekt

     @param Tickrate der Tickrate-Wert

     @param sound gibt an, ob der Sound aktiviert ist
     */
    public void handleTimerTick(Utils utils, Movement movement, int height, String playerImage, String rainbowImage,
                                int percentage, int verticalGap, String obstacleTopImage, String obstacleBottomImage, String dieSound,
                                String hitSound, String pointSound, String rainbowSound, int Tickrate, boolean sound) {

        if (!gamePaused) {

            if (gameUI.player.getY() >= height && gameOver && !gameState) gameUI.tickrate.stop(); // Stop the timer
            movement.movePlayer(utils, Tickrate); // Move the player


            if (gameState && !gameOver) {
                movement.moveObstacles(utils, percentage, verticalGap, obstacleTopImage, obstacleBottomImage, Tickrate); // Move the obstacles
                movement.moveBackground(utils, Tickrate); // Move the background

                gameUI.removeObstacles(); // Remove non-visible obstacles
                gameUI.checkCollision(utils, dieSound, hitSound, pointSound, rainbowSound, sound); // Check for collisions
                gameUI.checkRainbowMode(utils, playerImage, rainbowImage);
            }
        }
    }

    /**
    Behandelt das Kollisionsereignis.

    @param utils das Utils-Objekt

    @param dieSound der Pfad zum Sterben-Soundeffekt

    @param sound gibt an, ob der Sound aktiviert ist
*/
    public void handleCollision(Utils utils, String dieSound, boolean sound) {
        System.out.println("Kollision");
        utils.audioPlayer(dieSound, sound);

        gameOver = true;
        gameState = false;

        gameUI.gameOver.setVisible(true);
    }

    /**
     Behandelt das Ereignis des Sprungs.
     @param utils das Utils-Objekt
     @param movement das Movement-Objekt
     @param flapSound der Pfad zum Flatter-Soundeffekt
     @param sound gibt an, ob der Sound aktiviert ist
     */
    public void handleBounce(Utils utils, Movement movement, String flapSound, boolean sound) {
        utils.audioPlayer(flapSound, sound);
        if (gameUI.player.getY() > 32) movement.xPosition = -Main.JumpHeight; // Spieler nach oben bewegen
    }

    /**
     Behandelt das Ereignis der Punktevergabe.
     @param utils das Utils-Objekt
     @param pointSound der Pfad zum Punkte-Soundeffekt
     @param rainbowSound der Pfad zum Regenbogen-Soundeffekt
     @param sound gibt an, ob der Sound aktiviert ist
     */
    public void handlePoint(Utils utils, String pointSound, String rainbowSound, boolean sound) {
        utils.audioPlayer(pointSound, sound);
        gameUI.points++;
        if (gameUI.points > 0 && gameUI.points % 5 == 0 && (int) (Math.random() * 3 + 1) == 2) handleRainbowMode(utils, rainbowSound, sound);
        gameUI.score.setText("Score: " + gameUI.points);
    }

    /**
     Behandelt das Spiel-Pause-Ereignis.
     */
    public void handleGamePause() {
        if (gamePaused) {
            gameUI.pauseScreen.setVisible(false);
            gamePaused = false;
        } else {
            if (!rainbowMode) {
                gameUI.pauseScreen.setVisible(true);
                gamePaused = true;
            }
        }
    }

    /**
     Behandelt das Regenbogenmodus-Ereignis.
     @param utils das Utils-Objekt
     @param rainbowSound der Pfad zum Regenbogen-Soundeffekt
     @param sound gibt an, ob der Sound aktiviert ist
     */
    private void handleRainbowMode(Utils utils, String rainbowSound, boolean sound) {
        new Thread(() -> {
            try {
                rainbowMode = true;
                utils.audioPlayer(rainbowSound, sound);
                Thread.sleep(7000);
                rainbowMode = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}