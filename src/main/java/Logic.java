public class Logic {
    public static Logic instance;
    private static GameUI ui;
    private boolean gameState = false, gameOver;

    public Logic(int width, int height, String title, String icon, boolean resizable, int playerPosition, int playerWidth, int playerHeight, String backgroundImage, String playerImage, int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage, String gameOverImage, String dieSound, String flapSound, String hitSound, String pointSound, int Tickrate, boolean sound) {
        instance = this;
        gameOver = false;
        ui = new GameUI(width, height, title, icon, resizable, playerPosition, playerWidth, playerHeight, backgroundImage, playerImage, percentage, verticalGap, obstacleWidth, obstacleHeight, obstacleTopImage, obstacleBottomImage, gameOverImage, dieSound, flapSound, hitSound, pointSound, Tickrate, sound);
    }

    // Methode zum Verarbeiten der Leertaste-Eingabe
    public void handleSpaceKeyPress(String flapSound, boolean sound) {

        // Wenn das Spiel noch nicht läuft und das Spiel nicht vorbei ist
        if (!ui.tickrate.isRunning() && !gameState && !gameOver) {
            ui.tickrate.start(); // Starte den Timer
            ui.gameOver.setVisible(false);
            gameState = true;
        }

        // Wenn das Spiel nicht läuft und das Spiel vorbei ist
        if (!ui.tickrate.isRunning() && !gameState && gameOver) {
            UI.instance.initFrame(ui.points); // Initialisiere das Fenster erneut
            ui.dispose(); // Schließe das aktuelle Fenster
        }

        // Wenn das Spiel nicht vorbei ist, führe den Bounce aus
        if (!gameOver) handleBounce(flapSound, sound);
    }

    // Methode zum Verarbeiten des Timer-Ticks
    public void handleTimerTick(int width, int height, int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage, String dieSound, String hitSound, String pointSound, boolean sound) {
        Movement.instance.movePlayer(); // Bewege den Spieler
        Movement.instance.moveObstacles(width, height, percentage, verticalGap, obstacleWidth, obstacleHeight, obstacleTopImage, obstacleBottomImage); // Bewege die Hindernisse
        ui.removeObstacles(); // Entferne nicht sichtbare Hindernisse
        ui.checkCollision(width, dieSound, hitSound, pointSound, sound); // Überprüfe auf Kollisionen
    }

    // Methode zum Verarbeiten der Kollision
    public void handleCollision(String dieSound, boolean sound) {
        System.out.println("Kollision");
        Methods.instance.audioPlayer(dieSound, sound);
        ui.tickrate.stop(); // Stoppe den Timer
        ui.gameOver.setVisible(true);
        gameState = false;
        gameOver = true;
    }

    // Methode zum Verarbeiten des Bounces
    public void handleBounce(String flapSound, boolean sound) {
        Methods.instance.audioPlayer(flapSound, sound);
        if (ui.player.getY() > 32) Movement.instance.xPosition = - Main.JumpHeight; // Bewege den Spieler nach oben
    }

    // Methode zum Verarbeiten des Punktes
    public void handlePoint(String pointSound, boolean sound) {
        Methods.instance.audioPlayer(pointSound, sound);
        ui.points++;
        ui.score.setText("Score: " + ui.points);
        System.out.println("Punkt! Du hast jetzt " + ui.points);
    }
}