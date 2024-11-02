package de.MCmoderSD.UI;

import de.MCmoderSD.main.Config;
import de.MCmoderSD.objects.Background;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.BorderUIResource;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static de.MCmoderSD.main.Config.*;

public class Menu extends JPanel {

    // Associations
    private final Frame frame;

    // Attributes
    private final ScoreBoard scoreBoard;
    private final JLabel headline;
    private final JButton startButton;
    private final JCheckBox soundCheckBox;
    private final JTextField usernameField;

    // Variables
    private ArrayList<Background> backgrounds;

    // Constructor
    public Menu(Frame frame) {

        super();
        setLayout(null);
        setPreferredSize(SIZE);
        setBackground(BACKGROUND_COLOR);
        setVisible(true);
        frame.add(this);

        this.frame = frame;

        Font font = new Font("Roboto", Font.PLAIN, 22);
        Font headerFont = new Font("Roboto", Font.BOLD, 24);

        // Init Headline
        headline = new JLabel(TITLE);
        headline.setSize(Config.WIDTH, Math.round(Config.HEIGHT * 0.1f));
        headline.setLocation((Config.WIDTH - headline.getWidth()) / 2, 0);
        headline.setHorizontalAlignment(SwingConstants.CENTER);
        headline.setForeground(FONT_COLOR);
        headline.setFont(headerFont);
        headline.setVisible(true);
        add(headline);

        // Init startButton
        startButton = new JButton(START);
        startButton.setSize(Math.round(Config.WIDTH * 0.25f), Math.round(Config.HEIGHT * 0.05f));
        startButton.setLocation(Math.round(Config.WIDTH * 0.5f) - startButton.getWidth() / 2, Math.round(Config.HEIGHT * 0.9f) - startButton.getHeight() / 2);
        startButton.addActionListener(e -> frame.getController().startGame());
        startButton.setToolTipText(START_TOOL_TIP);
        startButton.setFont(font);
        startButton.setVisible(true);
        add(startButton);

        // Init soundButton
        soundCheckBox = new JCheckBox(SOUND);
        soundCheckBox.setSize(Math.round(Config.WIDTH * 0.15f), Math.round(Config.HEIGHT * 0.05f));
        soundCheckBox.setLocation(Math.round(Config.WIDTH * 0.725f) - soundCheckBox.getWidth() / 2, Math.round(Config.HEIGHT * 0.9f) - soundCheckBox.getHeight() / 2);
        soundCheckBox.setToolTipText(SOUND_TOOL_TIP);
        soundCheckBox.setForeground(FONT_COLOR);
        soundCheckBox.setFont(font);
        soundCheckBox.setOpaque(false);
        soundCheckBox.setSelected(true);
        soundCheckBox.setVisible(true);
        add(soundCheckBox);

        // Init usernameField
        usernameField = new JTextField();
        usernameField.setSize(Math.round(Config.WIDTH * 0.5f), Math.round(Config.HEIGHT * 0.05f));
        usernameField.setLocation((Config.WIDTH - usernameField.getWidth()) / 2, Math.round(Config.HEIGHT * 0.9f));
        usernameField.setBorder(new BorderUIResource.BevelBorderUIResource(0, FONT_COLOR, FONT_COLOR, FONT_COLOR, FONT_COLOR));
        usernameField.setHorizontalAlignment(SwingConstants.CENTER);
        usernameField.setToolTipText(USERNAME_TOOL_TIP);
        usernameField.setForeground(FONT_COLOR);
        usernameField.setText(USERNAME);
        usernameField.setOpaque(false);
        usernameField.setFont(font);
        usernameField.setVisible(false);
        add(usernameField);

        // Init ScoreBoard
        scoreBoard = new ScoreBoard(this);
        scoreBoard.setSize(Math.round(Config.WIDTH * 0.9f), Math.round(Config.HEIGHT * 0.7f));
        scoreBoard.setLocation(Math.round(Config.WIDTH * 0.05f), Math.round(Config.HEIGHT * 0.1f));
        scoreBoard.setVisible(true);
    }

    // Draw Backgrounds
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics;
        backgrounds = frame.getGame().getBackgrounds();

        for (Background background : backgrounds) {
            g.setColor(BACKGROUND_COLOR);
            g.fill(background.getHitbox());
            g.drawImage(BACKGROUND_IMAGE, background.getX(), background.getY(), null);
        }
    }

    // Setter
    public void setUsername(boolean visible) {
        if (visible) {
            usernameField.setText("");
            startButton.setLocation(Math.round((Config.WIDTH * 0.5f)) - startButton.getWidth() / 2, Math.round((Config.HEIGHT * 0.85f)) - startButton.getHeight() / 2);
            soundCheckBox.setLocation(Math.round((Config.WIDTH * 0.7f)) - soundCheckBox.getWidth() / 2, Math.round((Config.HEIGHT * 0.85f)) - soundCheckBox.getHeight() / 2);
            startButton.setText(CONFIRM);
            startButton.setToolTipText(CONFIRM_TOOL_TIP);
            for (ActionListener actionListener : startButton.getActionListeners())
                startButton.removeActionListener(actionListener);
            startButton.addActionListener(e -> frame.getController().uploadScore());
            usernameField.setVisible(true);
        } else {
            usernameField.setText("");
            usernameField.setVisible(false);
            startButton.setLocation(Math.round(Config.WIDTH * 0.5f) - startButton.getWidth() / 2, Math.round((Config.HEIGHT * 0.9f)) - startButton.getHeight() / 2);
            soundCheckBox.setLocation(Math.round((Config.WIDTH * 0.7f)) - soundCheckBox.getWidth() / 2, Math.round((Config.HEIGHT * 0.9f)) - soundCheckBox.getHeight() / 2);
            headline.setText(TITLE);
            startButton.setText(START);
            startButton.setToolTipText(START_TOOL_TIP);
            for (ActionListener actionListener : startButton.getActionListeners())
                startButton.removeActionListener(actionListener);
            startButton.addActionListener(e -> frame.getController().startGame());
            usernameField.setVisible(false);
        }
    }

    public void setHeadline(String headline) {
        this.headline.setText(headline);
    }

    public void setSound(boolean sound) {
        soundCheckBox.setSelected(sound);
    }

    public void setScoreBoard(boolean visible) {
        scoreBoard.setVisible(visible);
    }

    // Getter
    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public int getBackgroundPos() {
        return backgrounds.getFirst().getX();
    }

    public boolean isSound() {
        return soundCheckBox.isSelected();
    }
}