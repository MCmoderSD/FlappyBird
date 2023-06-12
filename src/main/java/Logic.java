import java.nio.file.Path;

public class Logic {
    public static Logic instance;
    private static GameUI ui;
    public boolean gamePaused = false, rainbowMode = false, rainbowModeActive = false, developerMode = false;
    private boolean gameState = false, gameOver;

    // Konstruktor und Instanz
    public Logic(int width, int height, String title, String icon, boolean resizable, int playerPosition, int playerWidth, int playerHeight, String backgroundImage, String playerImage, String rainbowImage, int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage, String gameOverImage, String pauseScreen, String dieSound, String flapSound, String hitSound, String pointSound, String rainbowSound, int Tickrate, boolean sound, String[] args) {
        instance = this;
        gameOver = false;
        if (Tickrate >= 100)
            Tickrate = 100;
        ui = new GameUI(width, height, title, icon, resizable, playerPosition, playerWidth, playerHeight, backgroundImage, playerImage, rainbowImage, percentage, verticalGap, obstacleWidth, obstacleHeight, obstacleTopImage, obstacleBottomImage, gameOverImage, pauseScreen,dieSound, flapSound, hitSound, pointSound, rainbowSound, Tickrate, sound, args);
    }

    // Methode zum Verarbeiten der Leertaste-Eingabe
    public void handleSpaceKeyPress(int width, int height, String title, String icon, boolean resizable, String backgroundImage, String flapSound, int Tickrate, boolean sound, String[] args) {
        // Wenn das Spiel noch nicht läuft und das Spiel nicht vorbei ist
        if (!ui.tickrate.isRunning() && !gameState && !gameOver) {
            ui.tickrate.start(); // Starte den Timer
            ui.gameOver.setVisible(false);
            gameState = true;
            gamePaused = false;
        }

        // Wenn das Spiel nicht läuft und das Spiel vorbei ist
        if (!ui.tickrate.isRunning() && !gameState && gameOver) {
            new UI (width, height, title, icon, resizable, backgroundImage, Tickrate, args, ui.points); // Initialisiere das Fenster erneut
            ui.dispose(); // Schließe das aktuelle Fenster
        }

        // Wenn das Spiel nicht vorbei ist, führe den Bounce aus
        if (!gameOver) {
            handleBounce(flapSound, sound);
        }
    }

    // Methode zum Verarbeiten des Timer-Ticks
    public void handleTimerTick(int width, int height, String playerImage, String rainbowImage, int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage, String dieSound, String hitSound, String pointSound, String rainbowSound, int Tickrate, boolean sound) {
        if (!gamePaused) {
            if (ui.player.getY() >= height && gameOver && !gameState) {
                ui.tickrate.stop(); // Stoppe den Timer
            }
            Movement.instance.movePlayer(Tickrate); // Bewege den Spieler
            if (gameState && !gameOver) {
                Movement.instance.moveObstacles(width, height, percentage, verticalGap, obstacleWidth, obstacleHeight, obstacleTopImage, obstacleBottomImage, Tickrate); // Bewege die Hindernisse
                Movement.instance.moveBackground(Tickrate); // Bewege den Hintergrund
                ui.removeObstacles(); // Entferne nicht sichtbare Hindernisse
                ui.checkCollision(width, dieSound, hitSound, pointSound, rainbowSound, sound); // Überprüfe auf Kollisionen
                ui.checkRainbowMode(playerImage, rainbowImage);
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
    public void handleBounce(String flapSound, boolean sound) {
        Methods.instance.audioPlayer(flapSound, sound);
        if (ui.player.getY() > 32) {
            Movement.instance.xPosition = -Main.JumpHeight; // Bewege den Spieler nach oben
        }
    }

    // Methode zum Verarbeiten des Punktes
    public void handlePoint(String pointSound, String rainbowSound, boolean sound) {
        Methods.instance.audioPlayer(pointSound, sound);
        ui.points++;
        if (ui.points > 0 && ui.points % 5 == 0) {
            if ((int) (Math.random() * 3 + 1) == 2) {
                handleRainbowMode(rainbowSound, sound);
            }
        }
        ui.score.setText("Score: " + ui.points);
        System.out.println("Punkt! Du hast jetzt " + ui.points);

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
    private void handleRainbowMode(String rainbowSound, boolean sound) {
        new Thread(() -> {
            try {
                rainbowMode = true;
                Methods.instance.audioPlayer(rainbowSound, sound);
                Thread.sleep(7000);
                rainbowMode = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}