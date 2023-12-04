package de.MCmoderSD.UI;

import de.MCmoderSD.core.Controller;
import de.MCmoderSD.core.Game;
import de.MCmoderSD.main.Config;
import de.MCmoderSD.utilities.Calculate;

import javax.swing.*;

public class Frame extends JFrame {

    // Associations
    private Controller controller;
    private Game game;
    private GameUI gameUI;
    private Menu menu;

    // Constructor
    public Frame(Config config) {
        super(config.getTitle());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(config.isResizable());
        setLocationRelativeTo(null);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

        // Add InputHandler
        InputHandler inputHandler = new InputHandler(this);

        // Create Menu
        menu = new Menu(this, config);

        // Create GameUI
        gameUI = new GameUI(this, config);

        // Create Game
        game = new Game(this, inputHandler, config);

        // Create Controller
        controller = new Controller(this, config);

        // Finalize and set visible
        pack();
        setLocation(Calculate.centerOfJFrame(this));
        setVisible(true);
    }

    // Setter
    public void showMessage(String message, String title) {
        new Thread(() -> JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE)).start();
    }

    // Getter
    public Controller getController() {
        return controller;
    }

    public Game getGame() {
        return game;
    }

    public GameUI getGameUI() {
        return gameUI;
    }

    public Menu getMenu() {
        return menu;
    }

    public boolean isFocusAllowed() {
        boolean isFocusAllowed = true;

        if (menu.canFocus()) isFocusAllowed = false;
        if (hasFocus()) isFocusAllowed = false;

        return isFocusAllowed;
    }
}
