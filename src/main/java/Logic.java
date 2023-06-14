/**

 Die Logic-Klasse repräsentiert die Spiellogik eines Spiels.
 */
public class Logic {
    public static Logic instance;
    private final GameUI ui;
    public boolean gamePaused = false, rainbowMode = false, rainbowModeActive = false, developerMode = false, cheatsEnabled = false;
    private boolean gameState = false, gameOver = false;

    /**
     Konstruiert ein Logic-Objekt.

     @param utils das Utils-Objekt

     @param movement das Movement-Objekt

     @param width die Breite der Spieloberfläche (UI)

     @param height die Höhe der Spieloberfläche (UI)

     @param title der Titel der Spieloberfläche (UI)

     @param icon das Icon der Spieloberfläche (UI)

     @param resizable gibt an, ob die Spieloberfläche (UI) veränderbar ist

     @param backgroundImage der Pfad zum Hintergrundbild

     @param playerImage der Pfad zum Spielerbild

     @param rainbowImage der Pfad zum Regenbogenbild

     @param percentage der Prozentsatzwert

     @param verticalGap der vertikale Lückenwert

     @param obstacleTopImage der Pfad zum Hindernisbild oben

     @param obstacleBottomImage der Pfad zum Hindernisbild unten

     @param gameOverImage der Pfad zum Bild für das Spielende

     @param pauseScreen der Pfad zur Pause-Bildschirm-Grafik

     @param dieSound der Pfad zum Soundeffekt für das Sterben

     @param flapSound der Pfad zum Flatter-Soundeffekt

     @param hitSound der Pfad zum Kollisions-Soundeffekt

     @param pointSound der Pfad zum Soundeffekt für das Punkte-Erzielen

     @param rainbowSound der Pfad zum Soundeffekt für den Regenbogen-Modus

     @param Tickrate der Tickrate-Wert

     @param sound gibt an, ob der Sound aktiviert ist

     @param args die Argumente
     */
    public Logic(Utils utils, Movement movement, int width, int height, String title, String icon, boolean resizable,
                 String backgroundImage, String playerImage, String rainbowImage,
                 int percentage, int verticalGap,
                 String obstacleTopImage, String obstacleBottomImage, String gameOverImage, String pauseScreen,
                 String dieSound, String flapSound, String hitSound, String pointSound, String rainbowSound,
                 int Tickrate, boolean sound, String[] args) {

        instance = this;

        if (Tickrate >= 100) Tickrate = 100;
        ui = new GameUI(utils, movement, width, height, title, icon, resizable,
                backgroundImage, playerImage, rainbowImage,
                percentage, verticalGap,
                obstacleTopImage, obstacleBottomImage, gameOverImage, pauseScreen,
                dieSound, flapSound, hitSound, pointSound, rainbowSound,
                Tickrate, sound, args);
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
        if (!ui.tickrate.isRunning() && !gameState && !gameOver) {
            ui.tickrate.start(); // Timer starten
            ui.gameOver.setVisible(false);
            gameState = true;
            gamePaused = false;
        }

// Wenn das Spiel nicht läuft und beendet ist
        if (!ui.tickrate.isRunning() && !gameState && gameOver) {
            new UI(utils, movement, width, height, title, icon, resizable, backgroundImage, Tickrate, sound, args, ui.points); // Fenster erneut initialisieren
            ui.dispose(); // Aktuelles Fenster schließen
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

            if (ui.player.getY() >= height && gameOver && !gameState) ui.tickrate.stop(); // Stop the timer
            movement.movePlayer(utils, Tickrate); // Move the player


            if (gameState && !gameOver) {
                movement.moveObstacles(utils, percentage, verticalGap, obstacleTopImage, obstacleBottomImage, Tickrate); // Move the obstacles
                movement.moveBackground(utils, Tickrate); // Move the background

                ui.removeObstacles(); // Remove non-visible obstacles
                ui.checkCollision(utils, dieSound, hitSound, pointSound, rainbowSound, sound); // Check for collisions
                ui.checkRainbowMode(utils, playerImage, rainbowImage);
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

        ui.gameOver.setVisible(true);
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
        if (ui.player.getY() > 32) movement.xPosition = -Main.JumpHeight; // Spieler nach oben bewegen
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
        ui.points++;
        if (ui.points > 0 && ui.points % 5 == 0 && (int) (Math.random() * 3 + 1) == 2) handleRainbowMode(utils, rainbowSound, sound);
        ui.score.setText("Score: " + ui.points);
    }

    /**
     Behandelt das Spiel-Pause-Ereignis.
     */
    public void handleGamePause() {
        if (gamePaused) {
            ui.pauseScreen.setVisible(false);
            gamePaused = false;
        } else {
            if (!rainbowMode) {
                ui.pauseScreen.setVisible(true);
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