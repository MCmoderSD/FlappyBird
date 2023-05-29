public class GameLogic {

    public static GameLogic instance;
    private static GameUI ui;
    private boolean gameState = false, gameOver = false;
    private int debugTimerTick;

    public GameLogic(int width, int height, String title, String icon, boolean resizable, int playerPosition, int playerWidth, int playerHeight, String playerImage, int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage, String gameOverImage, int Tickrate) {
        instance = this;
        ui = new GameUI(width, height, title, icon, resizable, playerPosition, playerWidth, playerHeight, playerImage, percentage, verticalGap, obstacleWidth, obstacleHeight, obstacleTopImage, obstacleBottomImage, gameOverImage,Tickrate);
    }

    // Behandelt das Drücken der Leertaste
    public void handleSpaceKeyPress() {
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
        handleBounce();
    }

    // Behandelt das Timer-Tick-Ereignis
    public void handleTimerTick(int width, int height , int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage) {
        debugTimerTick();
        ui.movePlayer();
        ui.moveObstacles(width, height, percentage, verticalGap, obstacleWidth, obstacleHeight, obstacleTopImage, obstacleBottomImage);
        ui.removeObstacles();
        ui.checkCollision(width);
    }

    // Behandelt die Kollision
    public void handleCollision() {
        System.out.println("Collision");
        ui.tickrate.stop();
        ui.gameOver.setVisible(true);
        gameState = false;
        gameOver = true;
    }

    // Behandelt das Abprallen des Spielers
    public void handleBounce() {
        if (ui.player.getY() > 32) {
            ui.xPosition = -Main.JumpHeight;
        }
    }

    // Hilfsmethode zur Debug-Ausgabe des Timer-Ticks
    private void debugTimerTick() {
        debugTimerTick++;
        if (debugTimerTick == 50) {
            System.out.println("Timer tick 50");
            debugTimerTick = 0;
        }
    }
}