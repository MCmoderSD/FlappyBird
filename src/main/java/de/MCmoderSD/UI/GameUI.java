package de.MCmoderSD.UI;

import de.MCmoderSD.main.Config;
import de.MCmoderSD.objects.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GameUI extends JPanel {

    // Associations
    private final Frame frame;
    private final Config config;

    // Attributes
    private Background background;

    // Constructor
    public GameUI(Frame frame, Config config) {
        super();

        this.frame = frame;
        this.config = config;

        setPreferredSize(config.getSize());
        frame.add(this);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics;

        Player player = frame.getController().getPlayer();
        ArrayList<Obstacle> obstacles = frame.getController().getObstacles();
        ArrayList<SafeZone> safeZones = frame.getController().getSafeZones();
        ArrayList<Cloud> clouds = frame.getController().getClouds();

        // Draw Background

        // Draw Player
        g.setColor(player.getColor());
        //g.fill(player.getHitbox());
        g.drawImage(player.getImage(), player.getX(), player.getY(), null);

        // Draw Obstacles
        for (Obstacle obstacle : obstacles) {
            g.setColor(obstacle.getColor());
            //g.fill(obstacle.getHitbox());
            g.drawImage(obstacle.getImage(), obstacle.getX(), obstacle.getY(), null);
        }

        for (SafeZone safeZone : safeZones) {
            g.setColor(safeZone.getColor());
            g.draw(safeZone.getHitbox());
        }

        // Draw Clouds
        for (Cloud cloud : clouds) {
            g.setColor(cloud.getColor());
            //g.fill(cloud.getHitbox());
            g.drawImage(cloud.getImage(), cloud.getX(), cloud.getY(), null);
        }
    }
}
