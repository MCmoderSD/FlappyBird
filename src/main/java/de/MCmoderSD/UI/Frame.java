package de.MCmoderSD.UI;

import de.MCmoderSD.core.Controller;
import de.MCmoderSD.main.Config;
import de.MCmoderSD.utilities.Calculate;

import javax.swing.*;

public class Frame extends JFrame {

    // Associations
    private final Controller controller;

    // Constructor
    public Frame(Config config) {
        super(config.getTitle());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(config.isResizable());
        setLocationRelativeTo(null);

        // Add InputHandler
        InputHandler inputHandler = new InputHandler(this);

        // Create GameUI
        GameUI gameUI = new GameUI(this, config);

        // Create Controller
        controller = new Controller(this, inputHandler, gameUI, config);

        // Finalize and set visible
        pack();
        setLocation(Calculate.centerOfJFrame(this));
        setVisible(true);
    }

    // Getter
    public Controller getController() {
        return controller;
    }
}
