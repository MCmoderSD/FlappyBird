import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Movement {
    public static Movement instance;
    public final int bgX = 0;
    public int xPosition = - Main.JumpHeight;
    private int obstacleMoveInt = 200, playerMoveInt = 0;
    // --Commented out by Inspection (01.06.2023 10:52):private boolean bgShouldMove;

    // Konstruktor und Instanz
    public Movement() {
        instance = this;
    }

    // Hindernisse auf dem Bildschirm bewegen
    public void moveObstacles(int width, int height, int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage, int Tickrate) {
        for (JLabel component : GameUI.instance.obstacles) {
            if (component != null && component.getIcon() != null) {
                int x = component.getX();
                int newX = x - (100/Tickrate);
                component.setLocation(newX, component.getY());
            }
        }

        moveRectangles(GameUI.instance.rObstacles, Tickrate);
        moveRectangles(GameUI.instance.greenZones, Tickrate);

        obstacleMoveInt = obstacleMoveInt + 1;

        // Periodisch neue Hindernisse generieren
        if (obstacleMoveInt >= (200 / (100/Tickrate))) {
            GameUI.instance.generateObstacles(width, height, percentage, verticalGap, obstacleWidth, obstacleHeight, obstacleTopImage, obstacleBottomImage);
            obstacleMoveInt = 0;
        }
    }

// --Commented out by Inspection START (31.05.2023 14:27):
//    // Hintergrund bewegen
//    public void moveBackground() {
//
//        if (bgShouldMove) {
//            bgX -= 1;
//            bgShouldMove = false;
//        } else {
//            bgShouldMove = true;
//        }
//    }
// --Commented out by Inspection STOP (31.05.2023 14:27)

    // Rechtecke auf dem Bildschirm bewegen
    private void moveRectangles(ArrayList<Rectangle> rectangles, int Tickrate) {
        for (Rectangle component : rectangles) {
            if (component != null) {
                component.getBounds();
                int x = (int) component.getX();
                int newX = x - (100/Tickrate);
                component.setLocation(newX, (int) component.getY());
            }
        }
    }

    // Spieler bewegen
    public void movePlayer(int Tickrate) {
        if (playerMoveInt >= (3 / (100/Tickrate))) { // Zähler
            xPosition = xPosition + 1;
            int yPosition = (GameUI.instance.player.getY() - Methods.instance.calculateGravity(xPosition));
            GameUI.instance.player.setLocation(250, yPosition);
            GameUI.instance.rPlayer.setLocation(GameUI.instance.player.getX(), GameUI.instance.player.getY()); // Rechteck aktualisieren
            playerMoveInt = 0; // Zähler zurücksetzen
        }
        playerMoveInt++; // Zähler erhöhen
    }
}
