public class Logic {
    public static Logic instance;
    public static boolean gamePaused = false;
    private static GameUI ui;
    private boolean gameState = false, gameOver;

    public Logic(int width, int height, String title, String icon, boolean resizable, int playerPosition, int playerWidth, int playerHeight, String backgroundImage, String playerImage, int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage, String gameOverImage, String pauseScreen, String dieSound, String flapSound, String hitSound, String pointSound, int Tickrate, boolean sound) {
        instance = this;
        gameOver = false;
        if (Tickrate >= 100) Tickrate = 100;
        ui = new GameUI(width, height, title, icon, resizable, playerPosition, playerWidth, playerHeight, backgroundImage, playerImage, percentage, verticalGap, obstacleWidth, obstacleHeight, obstacleTopImage, obstacleBottomImage, gameOverImage, pauseScreen,dieSound, flapSound, hitSound, pointSound, Tickrate, sound);
    }

    // Methode zum Verarbeiten der Leertaste-Eingabe
    public void handleSpaceKeyPress(int width, int height, String title, String icon, boolean resizable, String flapSound, int Tickrate, boolean sound) {

        // Wenn das Spiel noch nicht läuft und das Spiel nicht vorbei ist
        if (!ui.tickrate.isRunning() && !gameState && !gameOver) {
            ui.tickrate.start(); // Starte den Timer
            ui.gameOver.setVisible(false);
            gameState = true;
            gamePaused = false;
        }

        // Wenn das Spiel nicht läuft und das Spiel vorbei ist
        if (!ui.tickrate.isRunning() && !gameState && gameOver) {
            UI.instance.initFrame(width, height, title, icon, resizable, ui.points); // Initialisiere das Fenster erneut
            ui.dispose(); // Schließe das aktuelle Fenster
        }

        // Wenn das Spiel nicht vorbei ist, führe den Bounce aus
        if (!gameOver) handleBounce(flapSound, Tickrate, sound);
    }

    // Methode zum Verarbeiten des Timer-Ticks
    public void handleTimerTick(int width, int height, int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage, String dieSound, String hitSound, String pointSound, int Tickrate, boolean sound) {
        if (!gamePaused) {
            if (ui.player.getY() >= height && gameOver && !gameState) ui.tickrate.stop(); // Stoppe den Timer
            Movement.instance.movePlayer(Tickrate); // Bewege den Spieler
            if (gameState && !gameOver) {
                Movement.instance.moveObstacles(width, height, percentage, verticalGap, obstacleWidth, obstacleHeight, obstacleTopImage, obstacleBottomImage, Tickrate); // Bewege die Hindernisse
                // Movement.instance.moveBackground(); // Bewege den Hintergrund
                ui.removeObstacles(); // Entferne nicht sichtbare Hindernisse
                ui.checkCollision(width, dieSound, hitSound, pointSound, sound); // Überprüfe auf Kollisionen
            }
        }
    }

    // Methode zum Verarbeiten der Kollision
    public void handleCollision(String dieSound, boolean sound) {
        System.out.println("Kollision");
        Methods.instance.audioPlayer(dieSound, sound);
        gameOver = true;
        gameState = false;
        ui.gameOver.setVisible(true);
    }

    // Methode zum Verarbeiten des Bounces
    public void handleBounce(String flapSound, int Tickrate, boolean sound) {
        Methods.instance.audioPlayer(flapSound, sound);
        if (ui.player.getY() > 32) Movement.instance.xPosition = (int) (- Main.JumpHeight * (double) (100/Tickrate)); // Bewege den Spieler nach oben
    }

    // Methode zum Verarbeiten des Punktes
    public void handlePoint(String pointSound, boolean sound) {
        Methods.instance.audioPlayer(pointSound, sound);
        ui.points++;
        ui.score.setText("Score: " + ui.points);
        System.out.println("Punkt! Du hast jetzt " + ui.points);
    }

    // Methode zum Verarbeiten des Pausierens
    public void handleGamePause() {
        if (gamePaused) {
            ui.pauseScreen.setVisible(false);
            gamePaused = false;
        } else {
            ui.pauseScreen.setVisible(true);
            gamePaused = true;
        }
    }
}