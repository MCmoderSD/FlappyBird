import java.io.IOException;

public class GameLogic {


    public static GameLogic instance;
    private boolean gameStateRunning = false;
    GameUI ui;
    private int debugTimerTick;
    public GameLogic() throws IOException {
        instance = this;
        ui = new GameUI();
    }
    public void handleSpaceKeyPress() {
        System.out.println("Space pressed");
        // TODO: Add code to handle space key press
        if (!GameUI.t.isRunning()) GameUI.t.start();
        GameUI.xPosition = -5;
    }
    public void handleTimerTick() throws IOException {
        debugTimerTick();
        ui.MovePlayer();
        ui.moveObstacles();
        ui.generateObstacles();
        ui.removeObstacles();
        // ui.checkCollision(player, obstacle);
    }
    public void handleBounce() {
    }
    public void handleCollision() {

    }
    public int calculateGravity( int x) {
        return -3*x+4;
    }


    private void debugTimerTick() {
        debugTimerTick++;
        if (debugTimerTick == 50) {
            System.out.println("Timer tick 50");
            debugTimerTick = 0;
        }
    }
}
