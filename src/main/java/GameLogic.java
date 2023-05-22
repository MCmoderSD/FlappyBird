import java.util.concurrent.ForkJoinPool;

public class GameLogic {
    public static GameLogic instance;
    private final boolean gameStateRunnig = false;
    GameUI ui;
    private int debugTimerTick;
    public GameLogic() {
        instance = this;
        ui = new GameUI();
    }
    public void handleSpaceKeyPress() {
        System.out.println("Space pressed");
        // TODO: Add code to handle space key press
        if (!GameUI.t.isRunning()) GameUI.t.start();
    }
    public void handleTimerTick() {
        debugTimerTick();
        ui.moveObstacles();
        ui.generateObstacles();
        ui.removeObstacles();
        // ui.checkCollision(player, obstacle);
    }
    public void handleBounce() {
    }
    public void handleCollision() {

    }
    private int calculateGravity( int x) {
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
