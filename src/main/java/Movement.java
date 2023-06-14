import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Movement {
    //TODO: change comments to java docs

    public int backgroundResetX = 0, xPosition = -Main.JumpHeight;
    private int obstacleMoveInt = 200;
    private short playerMoveInt = 0, backgroundCount = 0;

    // Hintergrund auf dem Bildschirm bewegen
    public void moveBackground(Utils utils, int Tickrate) {
        // Hintergrund bewegen
        if (backgroundCount >= (2 / (100 / Tickrate))) {
            backgroundResetX--;

            // Zurücksetzen der Hintergrundposition
            if (backgroundResetX <= -utils.getBackgroundWidth()) {
                backgroundResetX = 0;
            }
            GameUI.instance.mainPanel.repaint();
            backgroundCount = 0;
        }
        backgroundCount++;
    }

    // Spieler bewegen
    public void movePlayer(Utils utils, int Tickrate) {
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

    // Hindernisse auf dem Bildschirm bewegen
    public void moveObstacles(Utils utils, int percentage, int verticalGap, String obstacleTopImage, String obstacleBottomImage, int Tickrate) {
        for (JLabel component : GameUI.instance.obstacles) {
            if (component != null && component.getIcon() != null) {
                int x = component.getX();
                int newX = x - (100 / Tickrate);
                component.setLocation(newX, component.getY());
            }
        }

        moveRectangles(Tickrate, GameUI.instance.rObstacles, GameUI.instance.greenZones);
        obstacleMoveInt = obstacleMoveInt + 1;

        // Periodisch neue Hindernisse generieren
        if (obstacleMoveInt >= (200 / (100 / Tickrate))) {
            GameUI.instance.generateObstacles(utils, percentage, verticalGap, obstacleTopImage, obstacleBottomImage);
            obstacleMoveInt = 0;
        }
    }

    // Rechtecke auf dem Bildschirm bewegen
    @SafeVarargs
    private final void moveRectangles(int Tickrate, ArrayList<Rectangle>... rectangleList) {
        for (ArrayList<Rectangle> rectangles : rectangleList) {
            for (Rectangle component : rectangles) {
                if (component != null) {
                    component.getBounds();
                    int x = (int) component.getX();
                    int newX = x - (100 / Tickrate);
                    component.setLocation(newX, (int) component.getY());
                }
            }
        }
    }
}