public class GameLogic {

    public static GameLogic instance;
    public boolean gameState = false, gameOver = false;
    GameUI ui;
    private int debugTimerTick;

    public GameLogic() {
        instance = this;
        ui = new GameUI();
    }

    public void handleSpaceKeyPress() {
        System.out.println("Space pressed");
        if (!ui.t.isRunning() && !gameState && !gameOver) {
            ui.t.start();
            ui.gameOver.setVisible(false);
            gameState = true;
        }
        if (!ui.t.isRunning() && !gameState && gameOver) {
            //ToDo ui.restart();
            System.exit(0);
            gameOver = false;
        }
        handleBounce();
    }

    public void handleTimerTick() {
        debugTimerTick();
        ui.movePlayer();
        ui.moveObstacles();
        ui.generateObstacles(0);
        ui.removeObstacles();
        ui.checkCollision();
    }

    public void handleCollision() {
        System.out.println("Collision");
        ui.t.stop();
        ui.gameOver.setVisible(true);
        gameState = false;
        gameOver = true;
    }

    public void handleBounce() {
        if (ui.player.getY() > 32) {
            ui.xPosition = -Main.JumpHeight;
        }
    }

    private void debugTimerTick() {
        debugTimerTick++;
        if (debugTimerTick == 50) {
            System.out.println("Timer tick 50");
            debugTimerTick = 0;
        }
    }
}