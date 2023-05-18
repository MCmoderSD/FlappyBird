public class GameLogic {
    public static void handleSpaceKeyPress() {
        System.out.println("Space pressed");
        // TODO: Add code to handle space key press
        if (!GameUI.t.isRunning()) GameUI.t.start();
    }
    public static void handleTimerTick() {
        System.out.println("Timer tick");
    }
    public void handleBounce() {

    }
    public void handleCollision() {

    }
    private int calculateGravity( int x) {
        return -3*x+4;
    }
}
