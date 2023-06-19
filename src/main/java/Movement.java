import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Movement {
    public int backgroundResetX = 0, xPosition;
    private int obstacleMoveInt = 200;
    private short playerMoveInt = 0, backgroundCount = 0;
    private final String backgroundImage;

    public Movement(Utils utils, int width, int height, String title, String icon, boolean resizable, String backgroundImage, int JumpHeight, int Tickrate, boolean sound , String[] args, int points) {
        this.backgroundImage = backgroundImage;
        xPosition = - JumpHeight;
        new UI(utils, this, width, height, title, icon, resizable, backgroundImage, JumpHeight, Tickrate, sound, args, points);
    }

    public void moveBackground(Utils utils, double Tickrate) {
        // Hintergrund bewegen
        if (backgroundCount >= (2 / (100 / Tickrate))) {
            backgroundResetX--;

            // Zurücksetzen der Hintergrundposition
            if (backgroundResetX <= -utils.getBackgroundWidth(backgroundImage)) {
                backgroundResetX = 0;
            }
            GameUI.instance.mainPanel.repaint();
            backgroundCount = 0;
        }
        backgroundCount++;
    }

    public void movePlayer(Utils utils, double Tickrate) {
        // Spielerbewegung
        if (playerMoveInt >= (3 / (100 / Tickrate))) { // Zähler
            xPosition = xPosition + 1;
            int yPosition = (GameUI.instance.player.getY() - utils.calculateGravity(xPosition));
            GameUI.instance.player.setLocation(utils.xPlayerPosition(GameUI.instance.mainPanel), yPosition);
            GameUI.instance.rPlayer.setLocation(GameUI.instance.player.getX(), GameUI.instance.player.getY()); // Rechteck aktualisieren
            playerMoveInt = 0; // Zähler zurücksetzen
        }
        playerMoveInt++; // Zähler erhöhen
    }

    public void moveObstacles(Utils utils, int percentage, int verticalGap, String obstacleTopImage, String obstacleBottomImage, double Tickrate, int points) {
        for (JLabel component : GameUI.instance.obstacles) {
            if (component != null && component.getIcon() != null) {
                int x = component.getX();
                int newX = x - (int) Math.round(100 / Tickrate * (1 + ((double) points / 500)));
                component.setLocation(newX, component.getY());
            }
        }

        moveRectangles(Tickrate, points, GameUI.instance.rObstacles, GameUI.instance.greenZones);
        obstacleMoveInt = obstacleMoveInt + 1;

        // Periodisch neue Hindernisse generieren
        if (obstacleMoveInt >= ((200 / (100 / Tickrate)* (1 - ((double) points / 500))))) {
            GameUI.instance.generateObstacles(utils, percentage, verticalGap, obstacleTopImage, obstacleBottomImage);
            obstacleMoveInt = 0;
        }
    }
    @SafeVarargs
    private final void moveRectangles(double Tickrate, int points, ArrayList<Rectangle>... rectangleList) {
        for (ArrayList<Rectangle> rectangles : rectangleList) {
            for (Rectangle component : rectangles) {
                if (component != null) {
                    component.getBounds();
                    int x = (int) component.getX();
                    int newX = x - (int) Math.round(100 / Tickrate * (1 + ((double) points / 500)));
                    component.setLocation(newX, (int) component.getY());
                }
            }
        }
    }
}