public class Logic {
    public static Logic instance;
    private final GameUI ui;
    public boolean gamePaused = false, rainbowMode = false, rainbowModeActive = false, developerMode = false, cheatsEnabled = false;
    private boolean gameState = false, gameOver = false;

    // Konstruktor und Instanz
    public Logic(Methods methods, Movement  movement, int width, int height, String title, String icon, boolean resizable,
                 String backgroundImage, String playerImage, String rainbowImage,
                 int percentage, int verticalGap,
                 String obstacleTopImage, String obstacleBottomImage, String gameOverImage, String pauseScreen,
                 String dieSound, String flapSound, String hitSound, String pointSound, String rainbowSound,
                 int Tickrate, boolean sound, String[] args) {


        instance = this;

        if (Tickrate >= 100) Tickrate = 100;
        ui = new GameUI(methods, movement, width, height, title, icon, resizable,
                backgroundImage, playerImage, rainbowImage,
                percentage, verticalGap,
                obstacleTopImage, obstacleBottomImage, gameOverImage, pauseScreen,
                dieSound, flapSound, hitSound, pointSound, rainbowSound,
                Tickrate, sound, args);
    }

    // Methode zum Verarbeiten der Leertaste-Eingabe
    public void handleSpaceKeyPress(Methods methods, Movement movement, int width, int height, String title, String icon, boolean resizable, String backgroundImage, String flapSound, int Tickrate, boolean sound, String[] args) {

        // Wenn das Spiel noch nicht läuft und das Spiel nicht vorbei ist
        if (!ui.tickrate.isRunning() && !gameState && !gameOver) {
            ui.tickrate.start(); // Starte den Timer
            ui.gameOver.setVisible(false);
            gameState = true;
            gamePaused = false;
        }

        // Wenn das Spiel nicht läuft und das Spiel vorbei ist
        if (!ui.tickrate.isRunning() && !gameState && gameOver) {
            new UI(methods, movement, width, height, title, icon, resizable, backgroundImage, Tickrate, sound, args, ui.points); // Initialisiere das Fenster erneut
            ui.dispose(); // Schließe das aktuelle Fenster
        }

        // Wenn das Spiel nicht vorbei ist, führe den Bounce aus
        if (!gameOver) {
            handleBounce(methods, movement, flapSound, sound);
        }
    }

    // Methode zum Verarbeiten des Timer-Ticks
    public void handleTimerTick(Methods methods, Movement movement, int height, String playerImage, String rainbowImage,
                                int percentage, int verticalGap, String obstacleTopImage, String obstacleBottomImage, String dieSound,
                                String hitSound, String pointSound, String rainbowSound, int Tickrate, boolean sound) {

        if (!gamePaused) {

            if (ui.player.getY() >= height && gameOver && !gameState) ui.tickrate.stop(); // Stoppe den Timer
            movement.movePlayer(methods, Tickrate); // Bewege den Spieler


            if (gameState && !gameOver) {
                movement.moveObstacles(methods, percentage, verticalGap, obstacleTopImage, obstacleBottomImage, Tickrate); // Bewege die Hindernisse
                movement.moveBackground(methods, Tickrate); // Bewege den Hintergrund

                ui.removeObstacles(); // Entferne nicht sichtbare Hindernisse
                ui.checkCollision(methods, dieSound, hitSound, pointSound, rainbowSound, sound); // Überprüfe auf Kollisionen
                ui.checkRainbowMode(methods, playerImage, rainbowImage);
            }
        }
    }

    // Methode zum Verarbeiten der Kollision
    public void handleCollision(Methods methods, String dieSound, boolean sound) {
        System.out.println("Kollision");
        methods.audioPlayer(dieSound, sound);

        gameOver = true;
        gameState = false;

        ui.gameOver.setVisible(true);
    }

    // Methode zum Verarbeiten des Bounces
    public void handleBounce(Methods methods, Movement movement, String flapSound, boolean sound) {
        methods.audioPlayer(flapSound, sound);
        if (ui.player.getY() > 32) movement.xPosition = -Main.JumpHeight; // Bewege den Spieler nach oben
    }

    // Methode zum Verarbeiten des Punktes
    public void handlePoint(Methods methods, String pointSound, String rainbowSound, boolean sound) {
        methods.audioPlayer(pointSound, sound);
        ui.points++;
        if (ui.points > 0 && ui.points % 5 == 0 && (int) (Math.random() * 3 + 1) == 2) handleRainbowMode(methods, rainbowSound, sound);
        ui.score.setText("Score: " + ui.points);
    }

    // Methode zum Verarbeiten des Pausierens
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

    // Methode zum Verarbeiten des Regenbogen-Modus
    private void handleRainbowMode(Methods methods, String rainbowSound, boolean sound) {
        new Thread(() -> {
            try {
                rainbowMode = true;
                methods.audioPlayer(rainbowSound, sound);
                Thread.sleep(7000);
                rainbowMode = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}