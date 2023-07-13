import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Movement {

    // Klassenobjekte
    private final Config config;
    private final Utils utils;

    // Klassenvariablen
    public int backgroundResetX = 0, xPosition;
    private int obstacleMoveInt = 200;
    private byte playerMoveInt = 0, backgroundCount = 0;

    // Konstruktor und UI initialisieren
    public Movement(Config config) {
        this.config = config;
        this.utils = config.getUtils();
        
        xPosition = - config.getJumpHeight();
    }

    // Bewegt den Hintergrund;
    public void moveBackground(GameUI gameUI) {
        // Hintergrund bewegen
        if (backgroundCount >= (2 / (100 / config.getTPS()))) {
            backgroundResetX--;

            // Zurücksetzen der Hintergrundposition
            if (backgroundResetX <= -utils.getBackgroundWidth(config.getBackground())) {
                backgroundResetX = 0;
            }
            gameUI.mainPanel.repaint();
            backgroundCount = 0;
        }
        backgroundCount++;
    }

    // Bewegt den Spieler
    public void movePlayer(GameUI gameUI) {
        if (playerMoveInt >= (3 / (100 / config.getTPS()))) { // Zähler
            xPosition = xPosition + 1;
            if (config.getArgs().length < 2) {
                int yPosition = (gameUI.player.getY() - utils.calculateGravity(xPosition));
                gameUI.player.setLocation(utils.xPlayerPosition(gameUI.mainPanel), yPosition);
                gameUI.rPlayer.setLocation(gameUI.player.getX(), gameUI.player.getY()); // Rechteck aktualisieren
            }

            if (config.getArgs().length > 1) {
                int lastY = 0;

                for (JLabel component : gameUI.obstacles) {
                    if (component != null && component.getIcon() != null) {
                        if (lastY <= component.getY()) lastY = component.getY();
                    }
                }

                int vertical = utils.calculateGravity(xPosition);

                if (lastY > 1000 && vertical <= 0) vertical = 0;

                for (JLabel component : gameUI.obstacles) {
                    if (component != null && component.getIcon() != null) {
                        component.setLocation(component.getX(), component.getY() - vertical);
                    }
                }

                for (Rectangle rectangle : gameUI.rObstacles) {
                    rectangle.setLocation(rectangle.x, rectangle.y - vertical);
                }

                for (Rectangle rectangle : gameUI.greenZones) {
                    rectangle.setLocation(rectangle.x, rectangle.y - vertical);
                }
            }
            playerMoveInt = 0; // Zähler zurücksetzen
        }
        playerMoveInt++; // Zähler erhöhen
    }

    // Bewegt die Hindernisse
    public void moveObstacles(GameUI gameUI) {
        for (JLabel component : gameUI.obstacles) {
            if (component != null && component.getIcon() != null) {
                int x = component.getX();
                int newX = x - (int) Math.round(100 / config.getTPS() * (1 + ((double) config.getPoints() / 500)));
                component.setLocation(newX, component.getY());
            }
        }

        moveRectangles(gameUI.rObstacles, gameUI.greenZones);
        obstacleMoveInt = obstacleMoveInt + 1;

        // Periodisch neue Hindernisse generieren
        if (obstacleMoveInt >= ((200 / (100 / config.getTPS())* (1 - ((double) config.getPoints() / 500))))) {
            gameUI.generateObstacles();
            obstacleMoveInt = 0;
        }
    }

    @SafeVarargs
    private final void moveRectangles(ArrayList<Rectangle>... rectangleList) {
        for (ArrayList<Rectangle> rectangles : rectangleList) {
            for (Rectangle component : rectangles) {
                if (component != null) {
                    component.getBounds();
                    int x = (int) component.getX();
                    int newX = x - (int) Math.round(100 / config.getTPS() * (1 + ((double) config.getPoints() / 500)));
                    component.setLocation(newX, (int) component.getY());
                }
            }
        }
    }

    // Setzt alle Attribute zurück
    public void init() {
        backgroundResetX = 0;
        obstacleMoveInt = 200;
        playerMoveInt = 0;
        backgroundCount = 0;
    }
}