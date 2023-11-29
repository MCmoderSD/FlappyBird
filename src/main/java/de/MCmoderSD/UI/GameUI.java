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
    private final JLabel scoreLabel;

    // Constructor
    public GameUI(Frame frame, Config config) {
        super();

        this.frame = frame;
        this.config = config;

        setPreferredSize(config.getSize());
        setLayout(null);
        frame.add(this);

        Font font = new Font("Roboto", Font.PLAIN, 18);

        // Init Score Label
        scoreLabel = new JLabel(config.getScorePrefix());
        scoreLabel.setFont(font);
        scoreLabel.setForeground(config.getScoreColor());
        scoreLabel.setSize((int) (config.getWidth() * 0.125), (int) (config.getHeight() * 0.05));
        scoreLabel.setLocation(config.getWidth() - scoreLabel.getWidth() - 10, 10);
        add(scoreLabel);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics;

        Player player = frame.getController().getPlayer();
        ArrayList<Obstacle> obstacles = frame.getController().getObstacles();
        ArrayList<SafeZone> safeZones = frame.getController().getSafeZones();
        ArrayList<Cloud> clouds = frame.getController().getClouds();
        ArrayList<Background> backgrounds = frame.getController().getBackgrounds();

        // Draw Background
        for (Background background : backgrounds) {
            g.setColor(background.getColor());
            g.fill(background.getHitbox());
            g.drawImage(background.getImage(), background.getX(), background.getY(), null);
        }

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

        scoreLabel.setText(config.getScorePrefix() + frame.getController().getScore());

        // Draw UI Elements
        paintComponents(g);
    }
}
