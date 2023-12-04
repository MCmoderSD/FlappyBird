package de.MCmoderSD.UI;

import de.MCmoderSD.main.Config;

import javax.swing.*;
import java.awt.*;

public class Menu extends JPanel {

    // Associations
    private final Frame frame;
    private final Config config;

    // Attributes
    private final JLabel headline;
    private final ScoreBoard scoreBoard;
    private final JSpinner fpsSpinner;
    private final JButton startButton;
    private final JCheckBox soundButton;
    private final JTextField usernameField;

    // Constants
    public Menu(Frame frame, Config config) {
        super();
        setLayout(null);
        setPreferredSize(new Dimension(config.getWidth(), config.getHeight()));
        setBackground(config.getBackgroundColor());
        setVisible(true);
        frame.add(this, BorderLayout.CENTER);

        this.frame = frame;
        this.config = config;

        Font font = new Font("Roboto", Font.PLAIN, 22);
        Font headerFont = new Font("Roboto", Font.BOLD, 22);

        // Init Headline
        headline = new JLabel(config.getTitle());
        headline.setSize(config.getWidth(), (int) (config.getHeight() * 0.1));
        headline.setLocation((config.getWidth() - headline.getWidth()) / 2, 0);
        headline.setHorizontalAlignment(SwingConstants.CENTER);
        headline.setFont(headerFont);
        headline.setVisible(true);
        add(headline);

        // Init fpsSpinner
        fpsSpinner = new JSpinner(new SpinnerNumberModel(config.getMaxFPS(), 1, config.getMaxFPS(), 1));
        fpsSpinner.setSize((int) (config.getWidth() * 0.075), (int) (config.getHeight() * 0.05));
        fpsSpinner.setLocation((int) (config.getWidth() * 0.3) - fpsSpinner.getWidth() / 2, (int) (config.getHeight() * 0.9) - fpsSpinner.getHeight() / 2);
        fpsSpinner.setFont(font);
        fpsSpinner.setToolTipText(config.getFpsToolTip());
        fpsSpinner.setVisible(true);
        add(fpsSpinner);

        // Init startButton
        startButton = new JButton(config.getStart());
        startButton.setSize((int) (config.getWidth() * 0.125), (int) (config.getHeight() * 0.05));
        startButton.setLocation((int) (config.getWidth() * 0.5) - startButton.getWidth() / 2, (int) (config.getHeight() * 0.9) - startButton.getHeight() / 2);
        startButton.addActionListener(e -> frame.getController().startGame());
        startButton.setFont(font);
        startButton.setToolTipText(config.getStartToolTip());
        startButton.setVisible(true);
        add(startButton);

        // Init soundButton
        soundButton = new JCheckBox(config.getSound());
        soundButton.setSize((int) (config.getWidth() * 0.125), (int) (config.getHeight() * 0.05));
        soundButton.setLocation((int) (config.getWidth() * 0.7) - soundButton.getWidth() / 2, (int) (config.getHeight() * 0.9) - soundButton.getHeight() / 2);
        soundButton.setFont(font);
        soundButton.setToolTipText(config.getSoundToolTip());
        soundButton.setOpaque(false);
        soundButton.setSelected(true);
        soundButton.setVisible(true);
        add(soundButton);

        // Init usernameField
        usernameField = new JTextField();
        usernameField.setSize((int) (config.getWidth() * 0.5), (int) (config.getHeight() * 0.05));
        usernameField.setLocation((config.getWidth() - usernameField.getWidth()) / 2, (int) (config.getHeight() * 0.90));
        usernameField.setToolTipText(config.getUsernameToolTip());
        usernameField.setHorizontalAlignment(SwingConstants.CENTER);
        usernameField.setOpaque(false);
        usernameField.setFont(font);
        usernameField.setVisible(false);
        add(usernameField);

        // Init ScoreBoard
        scoreBoard = new ScoreBoard(this, config);
        scoreBoard.setSize((int) (config.getWidth() * 0.9), (int) (config.getHeight() * 0.7));
        scoreBoard.setLocation((int) (config.getWidth() * 0.05), (int) (config.getHeight() * 0.1));
        scoreBoard.setVisible(true);
    }

    // Getter
    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    public int getScore(String user) {
        return scoreBoard.getScore(user);
    }

    public int getFps() {
        return (int) fpsSpinner.getValue();
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public boolean isSound() {
        return soundButton.isSelected();
    }

    public boolean canFocus() {
        return !fpsSpinner.hasFocus();
    }

    // Setter
    public void setUsername(boolean visible) {
        if (visible) {
            usernameField.setText("");
            fpsSpinner.setLocation((int) (config.getWidth() * 0.3) - fpsSpinner.getWidth() / 2, (int) (config.getHeight() * 0.85) - fpsSpinner.getHeight() / 2);
            startButton.setLocation((int) (config.getWidth() * 0.5) - startButton.getWidth() / 2, (int) (config.getHeight() * 0.85) - startButton.getHeight() / 2);
            soundButton.setLocation((int) (config.getWidth() * 0.7) - soundButton.getWidth() / 2, (int) (config.getHeight() * 0.85) - soundButton.getHeight() / 2);
            startButton.setText(config.getConfirm());
            startButton.setToolTipText(config.getConfirmToolTip());
            startButton.addActionListener(e -> frame.getController().uploadScore());
            usernameField.setVisible(true);
        } else {
            usernameField.setText("");
            usernameField.setVisible(false);
            fpsSpinner.setLocation((int) (config.getWidth() * 0.3) - fpsSpinner.getWidth() / 2, (int) (config.getHeight() * 0.9) - fpsSpinner.getHeight() / 2);
            startButton.setLocation((int) (config.getWidth() * 0.5) - startButton.getWidth() / 2, (int) (config.getHeight() * 0.9) - startButton.getHeight() / 2);
            soundButton.setLocation((int) (config.getWidth() * 0.7) - soundButton.getWidth() / 2, (int) (config.getHeight() * 0.9) - soundButton.getHeight() / 2);
            headline.setText(config.getTitle());
            startButton.setText(config.getStart());
            startButton.setToolTipText(config.getStartToolTip());
            startButton.addActionListener(e -> frame.getController().startGame());
            usernameField.setVisible(false);
        }
    }

    public void setHeadline(String headline) {
        this.headline.setText(headline);
    }
}
