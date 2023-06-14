import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Diese Klasse enthält alle Methoden zum Bewegen der Objekte.
 */
public class Movement {
    public int backgroundResetX = 0, xPosition = -Main.JumpHeight;
    private int obstacleMoveInt = 200;
    private short playerMoveInt = 0, backgroundCount = 0;

    /**
     * Move the background on the screen.
     *
     * @param utils     Utility class for various calculations.
     * @param tickRate  The tick rate of the game.
     */
    public void moveBackground(Utils utils, int tickRate) {
        // Move the background
        if (backgroundCount >= (2 / (100 / tickRate))) {
            backgroundResetX--;

            // Reset the background position
            if (backgroundResetX <= -utils.getBackgroundWidth()) {
                backgroundResetX = 0;
            }

            GameUI.instance.mainPanel.repaint();
            backgroundCount = 0;
        }
        backgroundCount++;
    }

    /**
     * Move the player.
     *
     * @param utils     Utility class for various calculations.
     * @param tickRate  The tick rate of the game.
     */
    public void movePlayer(Utils utils, int tickRate) {
        // Player movement
        if (playerMoveInt >= (3 / (100 / tickRate))) {
            xPosition = xPosition + 1;
            int yPosition = (GameUI.instance.player.getY() - utils.calculateGravity(xPosition));
            GameUI.instance.player.setLocation(utils.xPlayerPosition(GameUI.instance.mainPanel), yPosition);
            GameUI.instance.rPlayer.setLocation(GameUI.instance.player.getX(), GameUI.instance.player.getY());
            playerMoveInt = 0;
        }
        playerMoveInt++;
    }

    /**
     * Move the obstacles on the screen.
     *
     * @param utils             Utility class for various calculations.
     * @param percentage        Percentage of obstacles to generate.
     * @param verticalGap       Vertical gap between obstacles.
     * @param obstacleTopImage  Image for the top obstacle.
     * @param obstacleBottomImage Image for the bottom obstacle.
     * @param tickRate          The tick rate of the game.
     */
    public void moveObstacles(Utils utils, int percentage, int verticalGap, String obstacleTopImage, String obstacleBottomImage, int tickRate) {
        for (JLabel component : GameUI.instance.obstacles) {
            if (component != null && component.getIcon() != null) {
                int x = component.getX();
                int newX = x - (100 / tickRate);
                component.setLocation(newX, component.getY());
            }
        }

        moveRectangles(GameUI.instance.rObstacles, tickRate);
        moveRectangles(GameUI.instance.greenZones, tickRate);

        obstacleMoveInt = obstacleMoveInt + 1;

        // Periodically generate new obstacles
        if (obstacleMoveInt >= (200 / (100 / tickRate))) {
            GameUI.instance.generateObstacles(utils, percentage, verticalGap, obstacleTopImage, obstacleBottomImage);
            obstacleMoveInt = 0;
        }
    }

    /**
     * Move the rectangles on the screen.
     *
     * @param rectangles List of rectangles to move.
     * @param tickRate   The tick rate of the game.
     */
    private void moveRectangles(ArrayList<Rectangle> rectangles, int tickRate) {
        for (Rectangle component : rectangles) {
            if (component != null) {
                component.getBounds();
                int x = (int) component.getX();
                int newX = x - (100 / tickRate);
                component.setLocation(newX, (int) component.getY());
            }
        }
    }

}