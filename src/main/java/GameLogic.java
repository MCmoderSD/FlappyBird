public class GameLogic {

    public static GameLogic instance;
    private static GameUI ui;
    private boolean gameState = false, gameOver = false;
    private int debugTimerTick;

    public GameLogic(int width, int height, String title, String icon, boolean resizable, int playerPosition, int playerWidth, int playerHeight, String playerImage, int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage, String gameOverImage, String dieSound, String flapSound, String hitSound, String pointSound, int Tickrate) {
        instance = this;
        ui = new GameUI(width, height, title, icon, resizable, playerPosition, playerWidth, playerHeight, playerImage, percentage, verticalGap, obstacleWidth, obstacleHeight, obstacleTopImage, obstacleBottomImage, gameOverImage, dieSound, flapSound, hitSound, pointSound, Tickrate);
    }
    // Behandelt das Drücken der Leertaste
    public void handleSpaceKeyPress(String flapSound) {
        System.out.println("Space pressed");
        if (!ui.tickrate.isRunning() && !gameState && !gameOver) {
            ui.tickrate.start();
            ui.gameOver.setVisible(false);
            gameState = true;
        }
        if (!ui.tickrate.isRunning() && !gameState && gameOver) {
            //ToDo ui.restart();
            System.exit(0);
            gameOver = false;
        }
        handleBounce(flapSound);
    }
    // Behandelt das Timer-Tick-Ereignis
    public void handleTimerTick(int width, int height , int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage, String dieSound, String flapSound, String hitSound, String pointSound) {
        debugTimerTick();
        ui.movePlayer();
        ui.moveObstacles(width, height, percentage, verticalGap, obstacleWidth, obstacleHeight, obstacleTopImage, obstacleBottomImage);
        ui.removeObstacles();
        ui.checkCollision(width, dieSound, flapSound, hitSound, pointSound);
    }
    // Behandelt die Kollision
    public void handleCollision(String dieSound) {
        System.out.println("Collision");
        ui.tickrate.stop();
        ui.gameOver.setVisible(true);
        gameState = false;
        gameOver = true;
        ui.audioPlayer(dieSound);
    }
    // Behandelt das Abprallen des Spielers
    public void handleBounce(String flapSound) {
        ui.audioPlayer(flapSound);
        if (ui.player.getY() > 32) {
            ui.xPosition = -Main.JumpHeight;
        }
    }
    // Hilfsmethode zur Debug-Ausgabe des Timer-Ticks
    private void debugTimerTick() {
        debugTimerTick++;
        if (debugTimerTick == 100) {
            System.out.println("Timer tick 100");
            debugTimerTick = 0;
        }
    }
}