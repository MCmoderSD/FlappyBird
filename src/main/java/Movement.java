import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 Diese Klasse enthält Methoden, um die Objekte des Spiels zu bewegen.
 */
public class Movement {
    /**
     * Zurücksetzen der X-Position des Hintergrunds auf 0.
     /
     public int backgroundResetX = 0;
     /*
     * Aktuelle X-Position des Spielers.
     */
    public int backgroundResetX = 0, xPosition = -Main.JumpHeight;
    private int obstacleMoveInt = 200;
    private short playerMoveInt = 0, backgroundCount = 0;
    private final UI ui;
    public Movement(Utils utils, int width, int height, String title, String icon, boolean resizable, String backgroundImage, int Tickrate, boolean sound , String[] args, int points) {
         ui = new UI(utils, this, width, height, title, icon, resizable, backgroundImage, Tickrate, sound, args, points);
    }

    /**
     * Bewegt den Hintergrund auf dem Bildschirm.
     *
     * @param utils     Das Hilfsobjekt für die Spiellogik.
     * @param tickRate  Die Taktgeschwindigkeit des Spiels.
     */
    public void moveBackground(Utils utils, int tickRate) {
        // Hintergrund bewegen
        if (backgroundCount >= (2 / (100 / tickRate))) {
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

    /**
     * Bewegt den Spieler.
     *
     * @param utils     Das Hilfsobjekt für die Spiellogik.
     * @param tickRate  Die Taktgeschwindigkeit des Spiels.
     */
    public void movePlayer(Utils utils, int tickRate) {
        // Spielerbewegung
        if (playerMoveInt >= (3 / (100 / tickRate))) { // Zähler
            xPosition = xPosition + 1;
            int yPosition = (GameUI.instance.player.getY() - utils.calculateGravity(xPosition));
            GameUI.instance.player.setLocation(utils.xPlayerPosition(GameUI.instance.mainPanel), yPosition);
            GameUI.instance.rPlayer.setLocation(GameUI.instance.player.getX(), GameUI.instance.player.getY()); // Rechteck aktualisieren
            playerMoveInt = 0; // Zähler zurücksetzen
        }
        playerMoveInt++; // Zähler erhöhen
    }

    /**
     * Bewegt die Hindernisse auf dem Bildschirm.
     *
     * @param utils             Das Hilfsobjekt für die Spiellogik.
     * @param percentage        Die Prozentsatzchance für das Auftreten von Hindernissen.
     * @param verticalGap       Der vertikale Abstand zwischen den Hindernissen.
     * @param obstacleTopImage  Der Dateiname des Bildes für das obere Hindernis.
     * @param obstacleBottomImage  Der Dateiname des Bildes für das untere Hindernis.
     * @param tickRate          Die Taktgeschwindigkeit des Spiels.
     */
    public void moveObstacles(Utils utils, int percentage, int verticalGap, String obstacleTopImage, String obstacleBottomImage, int tickRate) {
        for (JLabel component : GameUI.instance.obstacles) {
            if (component != null && component.getIcon() != null) {
                int x = component.getX();
                int newX = x - (100 / tickRate);
                component.setLocation(newX, component.getY());
            }
        }

        moveRectangles(tickRate, GameUI.instance.rObstacles, GameUI.instance.greenZones);
        obstacleMoveInt = obstacleMoveInt + 1;

        // Periodisch neue Hindernisse generieren
        if (obstacleMoveInt >= (200 / (100 / tickRate))) {
            GameUI.instance.generateObstacles(utils, percentage, verticalGap, obstacleTopImage, obstacleBottomImage);
            obstacleMoveInt = 0;
        }
    }

    /**
     * Bewegt die Rechtecke auf dem Bildschirm.
     *
     * @param tickRate         Die Taktgeschwindigkeit des Spiels.
     * @param rectangleList    Eine Liste von Rechteck-Arrays.
     */
    @SafeVarargs
    private final void moveRectangles(int tickRate, ArrayList<Rectangle>... rectangleList) {
        for (ArrayList<Rectangle> rectangles : rectangleList) {
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
}