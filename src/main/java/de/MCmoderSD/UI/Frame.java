package de.MCmoderSD.UI;

import de.MCmoderSD.core.Controller;
import de.MCmoderSD.core.Game;
import de.MCmoderSD.main.Config;
import de.MCmoderSD.utilities.Calculate;

import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;

public class Frame extends JFrame {

    // Associations
    private final Controller controller;
    private final Game game;
    private final GameUI gameUI;
    private final Menu menu;

    // Constructor
    public Frame(Config config) {
        super(Config.TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(Config.RESIZABLE);
        setLocationRelativeTo(null);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
        setIconImage(Config.ICON);

        // Add InputHandler
        new InputHandler(this);

        // Create Menu
        menu = new Menu(this);

        // Create GameUI
        gameUI = new GameUI(this);

        // Create Game
        game = new Game(this, config);

        // Create Controller
        controller = new Controller(this);

        // Finalize and set visible
        pack();
        setLocation(Calculate.centerOfJFrame(this, Config.SMALL_SCREEN_MODE));
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
}