/**
 * Diese Klasse enthält die Logik des Spiels.
 */
public class Logic {
    /**
     * Die Instanz der Logik-Klasse.
     */
    public static Logic instance;

    /**
     * Die Benutzeroberfläche des Spiels.
     */
    private final GameUI ui;

    /**
     * Gibt an, ob das Spiel pausiert ist.
     */
    public boolean gamePaused = false;

    /**
     * Gibt an, ob der Regenbogen-Modus aktiv ist.
     */
    public boolean rainbowMode = false;

    /**
     * Gibt an, ob der Regenbogen-Modus aktiv ist.
     */
    public boolean rainbowModeActive = false;

    /**
     * Gibt an, ob der Entwicklermodus aktiv ist.
     */
    public boolean developerMode = false;

    /**
     * Gibt an, ob Cheats aktiviert sind.
     */
    public boolean cheatsEnabled = false;

    /**
     * Der Spielzustand.
     */
    private boolean gameState = false;

    /**
     * Gibt an, ob das Spiel vorbei ist.
     */
    private boolean gameOver = false;

    /**
     * Konstruktor der Logic-Klasse.
     *
     * @param utils                Das Utils-Objekt.
     * @param movement             Das Movement-Objekt.
     * @param width                Die Breite.
     * @param height               Die Höhe.
     * @param title                Der Titel.
     * @param icon                 Das Icon.
     * @param resizable            Gibt an, ob das Fenster skalierbar ist.
     * @param backgroundImage     Das Hintergrundbild.
     * @param playerImage          Das Bild des Spielers.
     * @param rainbowImage         Das Bild des Regenbogens.
     * @param percentage           Der Prozentsatz.
     * @param verticalGap          Der vertikale Abstand.
     * @param obstacleTopImage     Das Bild des oberen Hindernisses.
     * @param obstacleBottomImage  Das Bild des unteren Hindernisses.
     * @param gameOverImage        Das Bild für das Spielende.
     * @param pauseScreen          Das Bild für den Pausenbildschirm.
     * @param dieSound             Der Sound beim Sterben.
     * @param flapSound            Der Sound beim Flattern.
     * @param hitSound             Der Sound beim Treffen.
     * @param pointSound           Der Sound beim Erzielen eines Punktes.
     * @param rainbowSound         Der Sound beim Aktivieren des Regenbogen-Modus.
     * @param Tickrate             Die Tickrate.
     * @param sound                Gibt an, ob Sound aktiviert ist.
     * @param args                 Die Argumente.
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
     * Verarbeitet die Leertaste-Eingabe.
     *
     * @param utils        Das Utils-Objekt.
     * @param movement     Das Movement-Objekt.
     * @param width        Die Breite.
     * @param height       Die Höhe.
     * @param title        Der Titel.
     * @param icon         Das Icon.
     * @param resizable    Gibt an, ob das Fenster skalierbar ist.
     * @param backgroundImage Das Hintergrundbild.
     * @param flapSound    Der Sound beim Flattern.
     * @param Tickrate     Die Tickrate.
     * @param sound        Gibt an, ob Sound aktiviert ist.
     * @param args         Die Argumente.
     */
    public void handleSpaceKeyPress(Utils utils, Movement movement, int width, int height, String title, String icon, boolean resizable, String backgroundImage, String flapSound, int Tickrate, boolean sound, String[] args) {
        // Wenn das Spiel noch nicht läuft und das Spiel nicht vorbei ist
        if (!ui.tickrate.isRunning() && !gameState && !gameOver) {
            ui.tickrate.start(); // Starte den Timer
            ui.gameOver.setVisible(false);
            gameState = true;
            gamePaused = false;
        }

        // Wenn das Spiel nicht läuft und das Spiel vorbei ist
        if (!ui.tickrate.isRunning() && !gameState && gameOver) {
            new UI(utils, movement, width, height, title, icon, resizable, backgroundImage, Tickrate, sound, args, ui.points); // Initialisiere das Fenster erneut
            ui.dispose(); // Schließe das aktuelle Fenster
        }

        // Wenn das Spiel nicht vorbei ist, führe den Bounce aus
        if (!gameOver) {
            handleBounce(utils, movement, flapSound, sound);
        }
    }

    /**
     * Verarbeitet den Timer-Tick.
     *
     * @param utils               Das Utils-Objekt.
     * @param movement            Das Movement-Objekt.
     * @param height              Die Höhe.
     * @param playerImage         Das Bild des Spielers.
     * @param rainbowImage        Das Bild des Regenbogens.
     * @param percentage          Der Prozentsatz.
     * @param verticalGap         Der vertikale Abstand.
     * @param obstacleTopImage    Das Bild des oberen Hindernisses.
     * @param obstacleBottomImage Das Bild des unteren Hindernisses.
     * @param dieSound            Der Sound beim Sterben.
     * @param hitSound            Der Sound beim Treffen.
     * @param pointSound          Der Sound beim Erzielen eines Punktes.
     * @param rainbowSound        Der Sound beim Aktivieren des Regenbogen-Modus.
     * @param Tickrate            Die Tickrate.
     * @param sound               Gibt an, ob Sound aktiviert ist.
     */
    public void handleTimerTick(Utils utils, Movement movement, int height, String playerImage, String rainbowImage,
                                int percentage, int verticalGap, String obstacleTopImage, String obstacleBottomImage, String dieSound,
                                String hitSound, String pointSound, String rainbowSound, int Tickrate, boolean sound) {

        if (!gamePaused) {
            if (ui.player.getY() >= height && gameOver && !gameState) ui.tickrate.stop(); // Stoppe den Timer
            movement.movePlayer(utils, Tickrate); // Bewege den Spieler

            if (gameState && !gameOver) {
                movement.moveObstacles(utils, percentage, verticalGap, obstacleTopImage, obstacleBottomImage, Tickrate); // Bewege die Hindernisse
                movement.moveBackground(utils, Tickrate); // Bewege den Hintergrund

                ui.removeObstacles(); // Entferne nicht sichtbare Hindernisse
                ui.checkCollision(utils, dieSound, hitSound, pointSound, rainbowSound, sound); // Überprüfe auf Kollisionen
                ui.checkRainbowMode(utils, playerImage, rainbowImage);
            }
        }
    }

    /**
     * Verarbeitet die Kollision.
     *
     * @param utils     Das Utils-Objekt.
     * @param dieSound  Der Sound beim Sterben.
     * @param sound     Gibt an, ob Sound aktiviert ist.
     */
    public void handleCollision(Utils utils, String dieSound, boolean sound) {
        System.out.println("Kollision");
        utils.audioPlayer(dieSound, sound);

        gameOver = true;
        gameState = false;

        ui.gameOver.setVisible(true);
    }

    /**
     * Verarbeitet den Bounce.
     *
     * @param utils      Das Utils-Objekt.
     * @param movement   Das Movement-Objekt.
     * @param flapSound  Der Sound beim Flattern.
     * @param sound      Gibt an, ob Sound aktiviert ist.
     */
    public void handleBounce(Utils utils, Movement movement, String flapSound, boolean sound) {
        utils.audioPlayer(flapSound, sound);
        if (ui.player.getY() > 32) movement.xPosition = -Main.JumpHeight; // Bewege den Spieler nach oben
    }

    /**
     * Verarbeitet den Punkt.
     *
     * @param utils       Das Utils-Objekt.
     * @param pointSound  Der Sound beim Erzielen eines Punktes.
     * @param rainbowSound  Der Sound beim Aktivieren des Regenbogen-Modus.
     * @param sound       Gibt an, ob Sound aktiviert ist.
     */
    public void handlePoint(Utils utils, String pointSound, String rainbowSound, boolean sound) {
        utils.audioPlayer(pointSound, sound);
        ui.points++;
        if (ui.points > 0 && ui.points % 5 == 0 && (int) (Math.random() * 3 + 1) == 2) handleRainbowMode(utils, rainbowSound, sound);
        ui.score.setText("Score: " + ui.points);
    }

    /**
     * Verarbeitet das Pausieren des Spiels.
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
     * Verarbeitet den Regenbogen-Modus.
     *
     * @param utils         Das Utils-Objekt.
     * @param rainbowSound  Der Sound beim Aktivieren des Regenbogen-Modus.
     * @param sound         Gibt an, ob Sound aktiviert ist.
     */
    private void handleRainbowMode(Utils utils, String rainbowSound, boolean sound) {
        new Thread(() -> {
            try {
                rainbowMode = true;
                utils.audioPlayer(rainbowSound, sound);
                Thread.sleep(3000);
                rainbowMode = false;
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}