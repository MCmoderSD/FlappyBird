import java.io.IOException;
import java.awt.Rectangle;

public class GameLogic {

    public static GameLogic instance;
    Rectangle rPlayer, rTubeTop1, rTubeBottom1,rTubeTop2, rTubeBottom2,rTubeTop3, rTubeBottom3;
    GameUI ui;
    private boolean gameState = true;
    private int debugTimerTick;

    public GameLogic() throws IOException {
        instance = this;
        ui = new GameUI();
        rPlayer =new Rectangle(GameUI.player.getBounds());
        rTubeTop1 = new Rectangle(GameUI.obstacles.get(0).getBounds());
        rTubeBottom1 = new Rectangle(GameUI.obstacles.get(1).getBounds());
        rTubeTop2 = new Rectangle(GameUI.obstacles.get(2).getBounds());
        rTubeBottom2 = new Rectangle(GameUI.obstacles.get(3).getBounds());
        rTubeTop3 = new Rectangle(GameUI.obstacles.get(4).getBounds());
        rTubeBottom3 = new Rectangle(GameUI.obstacles.get(5).getBounds());
    }

    public void handleSpaceKeyPress() {
        System.out.println("Space pressed");
        if (!GameUI.t.isRunning() && gameState) {
            GameUI.t.start();
            GameUI.gameOver.setVisible(false);
        }
        if (!GameUI.t.isRunning() && !gameState) {
            GameUI.t.restart();
        }

        GameUI.xPosition = -Main.JumpHeight;
    }


    public void handleTimerTick() throws IOException {
        debugTimerTick();
        ui.MovePlayer();
        ui.moveObstacles();
        ui.generateObstacles(0);
        ui.removeObstacles();
        // ui.checkCollision(player, obstacle);
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

        rPlayer.setLocation(GameUI.player.getX(), GameUI.player.getY());
        rTubeTop1.setLocation(GameUI.obstacles.get(0).getX(), GameUI.obstacles.get(0).getY());
        rTubeBottom1.setLocation(GameUI.obstacles.get(1).getX(), GameUI.obstacles.get(1).getY());
        rTubeTop2.setLocation(GameUI.obstacles.get(2).getX(), GameUI.obstacles.get(2).getY());
        rTubeBottom2.setLocation(GameUI.obstacles.get(3).getX(), GameUI.obstacles.get(3).getY());
        rTubeTop3.setLocation(GameUI.obstacles.get(4).getX(), GameUI.obstacles.get(4).getY());
        rTubeBottom3.setLocation(GameUI.obstacles.get(5).getX(), GameUI.obstacles.get(5).getY());
        if (rPlayer.intersects(rTubeTop1) || rPlayer.intersects(rTubeBottom1) || rPlayer.intersects(rTubeTop2) || rPlayer.intersects(rTubeBottom2) || rPlayer.intersects(rTubeTop3) || rPlayer.intersects(rTubeBottom3)) {
            System.out.println("Collision");
            GameUI.t.stop();
            GameUI.gameOver.setVisible(true);
            gameState = false;
            GameUI.gameOver.requestFocus();
        
        }
    }
}
