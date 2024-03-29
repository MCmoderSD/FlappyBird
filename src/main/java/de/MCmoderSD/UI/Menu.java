package de.MCmoderSD.UI;

import de.MCmoderSD.main.Config;
import de.MCmoderSD.objects.Background;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.BorderUIResource;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

public class Menu extends JPanel {

    // Associations
    private final Frame frame;

    // Attributes
    private final ScoreBoard scoreBoard;
    private final JLabel headline;
    private final JSpinner fpsSpinner;
    private final JButton startButton;
    private final JCheckBox soundCheckBox;
    private final JTextField usernameField;

    // Variables
    private ArrayList<Background> backgrounds;
    private boolean canFocus;

    // Constructor
    public Menu(Frame frame) {
        super();
        setLayout(null);
        setPreferredSize(Config.SIZE);
        setBackground(Config.BACKGROUND_COLOR);
        setVisible(true);
        frame.add(this);

        this.frame = frame;

        Font font = new Font("Roboto", Font.PLAIN, 22);
        Font headerFont = new Font("Roboto", Font.BOLD, 24);

        // Init Headline
        headline = new JLabel(Config.TITLE);
        headline.setSize(Config.WIDTH, Math.toIntExact(Math.round(Config.HEIGHT * 0.1)));
        headline.setLocation((Config.WIDTH - headline.getWidth()) / 2, 0);
        headline.setHorizontalAlignment(SwingConstants.CENTER);
        headline.setForeground(Config.FONT_COLOR);
        headline.setFont(headerFont);
        headline.setVisible(true);
        add(headline);

        // Init fpsSpinner
        fpsSpinner = new JSpinner(new SpinnerNumberModel(Config.MAX_FPS, 1, Config.MAX_FPS, 1));
        fpsSpinner.setSize(Math.toIntExact(Math.round(Config.WIDTH * 0.075)), Math.toIntExact(Math.round(Config.HEIGHT * 0.05)));
        fpsSpinner.setLocation(Math.toIntExact(Math.round(Config.WIDTH * 0.3)) - fpsSpinner.getWidth() / 2, Math.toIntExact(Math.round(Config.HEIGHT * 0.9)) - fpsSpinner.getHeight() / 2);
        fpsSpinner.setToolTipText(Config.FPS_TOOL_TIP);
        fpsSpinner.setForeground(Config.FONT_COLOR);
        fpsSpinner.setOpaque(false);
        fpsSpinner.setFont(font);
        fpsSpinner.setVisible(true);
        add(fpsSpinner);

        // Init startButton
        startButton = new JButton(Config.START);
        startButton.setSize(Math.toIntExact(Math.round(Config.WIDTH * 0.25)), Math.toIntExact(Math.round(Config.HEIGHT * 0.05)));
        startButton.setLocation(Math.toIntExact(Math.round(Config.WIDTH * 0.5)) - startButton.getWidth() / 2, Math.toIntExact(Math.round(Config.HEIGHT * 0.9)) - startButton.getHeight() / 2);
        startButton.addActionListener(e -> frame.getController().startGame());
        startButton.setToolTipText(Config.START_TOOL_TIP);
        startButton.setFont(font);
        startButton.setVisible(true);
        add(startButton);

        // Init soundButton
        soundCheckBox = new JCheckBox(Config.SOUND);
        soundCheckBox.setSize(Math.toIntExact(Math.round(Config.WIDTH * 0.15)), Math.toIntExact(Math.round(Config.HEIGHT * 0.05)));
        soundCheckBox.setLocation(Math.toIntExact(Math.round(Config.WIDTH * 0.725)) - soundCheckBox.getWidth() / 2, Math.toIntExact(Math.round(Config.HEIGHT * 0.9) - soundCheckBox.getHeight() / 2));
        soundCheckBox.setToolTipText(Config.SOUND_TOOL_TIP);
        soundCheckBox.setForeground(Config.FONT_COLOR);
        soundCheckBox.setFont(font);
        soundCheckBox.setOpaque(false);
        soundCheckBox.setSelected(true);
        soundCheckBox.setVisible(true);
        add(soundCheckBox);

        // Init usernameField
        usernameField = new JTextField();
        usernameField.setSize(Math.toIntExact(Math.round(Config.WIDTH * 0.5)), Math.toIntExact(Math.round(Config.HEIGHT * 0.05)));
        usernameField.setLocation((Config.WIDTH - usernameField.getWidth()) / 2, Math.toIntExact(Math.round(Config.HEIGHT * 0.90)));
        usernameField.setBorder(new BorderUIResource.BevelBorderUIResource(0, Config.FONT_COLOR, Config.FONT_COLOR, Config.FONT_COLOR, Config.FONT_COLOR));
        usernameField.setHorizontalAlignment(SwingConstants.CENTER);
        usernameField.setToolTipText(Config.USERNAME_TOOL_TIP);
        usernameField.setForeground(Config.FONT_COLOR);
        usernameField.setText(Config.USERNAME);
        usernameField.setOpaque(false);
        usernameField.setFont(font);
        usernameField.setVisible(false);
        add(usernameField);

        // Init ScoreBoard
        scoreBoard = new ScoreBoard(this);
        scoreBoard.setSize(Math.toIntExact(Math.round(Config.WIDTH * 0.9)), Math.toIntExact(Math.round(Config.HEIGHT * 0.7)));
        scoreBoard.setLocation(Math.toIntExact(Math.round(Config.WIDTH * 0.05)), Math.toIntExact(Math.round(Config.HEIGHT * 0.1)));
        scoreBoard.setVisible(true);

        // Init Focus Listener
        canFocus = true;

        ((JSpinner.DefaultEditor) fpsSpinner.getEditor()).getTextField().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                canFocus = false;
            }

            @Override
            public void focusLost(FocusEvent e) {
                canFocus = true;
            }
        });

        usernameField.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent evt) {
                canFocus = false;
            }

            @Override
            public void focusLost(FocusEvent evt) {
                canFocus = true;
            }
        });
    }

    // Draw Backgrounds
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics;
        backgrounds = frame.getGame().getBackgrounds();

        for (Background background : backgrounds) {
            g.setColor(background.getColor());
            g.fill(background.getHitbox());
            g.drawImage(background.getImage(), background.getX(), background.getY(), null);
        }
    }

    // Setter
    public void setUsername(boolean visible) {
        if (visible) {
            usernameField.setText("");
            fpsSpinner.setLocation(Math.toIntExact(Math.round((Config.WIDTH * 0.3))) - fpsSpinner.getWidth() / 2, Math.toIntExact(Math.round((Config.HEIGHT * 0.85))) - fpsSpinner.getHeight() / 2);
            startButton.setLocation(Math.toIntExact(Math.round((Config.WIDTH * 0.5))) - startButton.getWidth() / 2, Math.toIntExact(Math.round((Config.HEIGHT * 0.85))) - startButton.getHeight() / 2);
            soundCheckBox.setLocation(Math.toIntExact(Math.round((Config.WIDTH * 0.7))) - soundCheckBox.getWidth() / 2, Math.toIntExact(Math.round((Config.HEIGHT * 0.85))) - soundCheckBox.getHeight() / 2);
            startButton.setText(Config.CONFIRM);
            startButton.setToolTipText(Config.CONFIRM_TOOL_TIP);
            for (ActionListener actionListener : startButton.getActionListeners())
                startButton.removeActionListener(actionListener);
            startButton.addActionListener(e -> frame.getController().uploadScore());
            usernameField.setVisible(true);
        } else {
            usernameField.setText("");
            usernameField.setVisible(false);
            fpsSpinner.setLocation(Math.toIntExact(Math.round((Config.WIDTH * 0.3))) - fpsSpinner.getWidth() / 2, Math.toIntExact(Math.round((Config.HEIGHT * 0.9))) - fpsSpinner.getHeight() / 2);
            startButton.setLocation(Math.toIntExact(Math.round((Config.WIDTH * 0.5))) - startButton.getWidth() / 2, Math.toIntExact(Math.round((Config.HEIGHT * 0.9))) - startButton.getHeight() / 2);
            soundCheckBox.setLocation(Math.toIntExact(Math.round((Config.WIDTH * 0.7))) - soundCheckBox.getWidth() / 2, Math.toIntExact(Math.round((Config.HEIGHT * 0.9))) - soundCheckBox.getHeight() / 2);
            headline.setText(Config.TITLE);
            startButton.setText(Config.START);
            startButton.setToolTipText(Config.START_TOOL_TIP);
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

    public int getFps() {
        return (int) fpsSpinner.getValue();
    }

    public int getBackgroundPos() {
        return backgrounds.get(0).getX();
    }

    public boolean isSound() {
        return soundCheckBox.isSelected();
    }

    public boolean canFocus() {
        return canFocus;
    }
}