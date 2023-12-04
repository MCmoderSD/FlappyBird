package de.MCmoderSD.core;

import de.MCmoderSD.UI.Frame;
import de.MCmoderSD.UI.InputHandler;
import de.MCmoderSD.UI.Menu;
import de.MCmoderSD.UI.ScoreBoard;
import de.MCmoderSD.main.Config;
import de.MCmoderSD.utilities.Calculate;
import de.MCmoderSD.utilities.database.MySQL;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Controller {

    // Associations
    private final Frame frame;
    private final InputHandler inputHandler;
    private final Config config;
    private final MySQL mySQL;

    // Attributes
    private int score;

    public Controller(Frame frame, InputHandler inputHandler, Config config) {
        super();
        this.frame = frame;
        this.inputHandler = inputHandler;
        this.config = config;

        mySQL = new MySQL(config.getDatabase());

        // Update Loop
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::updateScoreBoard, 0, 100, TimeUnit.MILLISECONDS);
    }

    // Updates the ScoreBoard
    private void updateScoreBoard() {
        ScoreBoard scoreBoard = frame.getMenu().getScoreBoard();
        scoreBoard.setHashMap(mySQL.pullFromMySQL());
    }

    public void startGame() {
        Menu menu = frame.getMenu();

        frame.getGame().initGameConstants(menu.isSound(), menu.getFps());

        // Set GameUI visible
        frame.getMenu().setVisible(false);
        frame.getGameUI().setVisible(true);
        frame.requestFocusInWindow();
    }

    public void restart(boolean debug, boolean cheats, int score) {
        Menu menu = frame.getMenu();

        this.score = score;

        if (!debug || !cheats) {
            menu.setUsername(true);
            menu.setHeadline(config.getInstruction() + score);
        }
    }

    public void uploadScore() {
        Menu menu = frame.getMenu();

        if (Calculate.isUsernameValid(menu.getUsername())) {
            mySQL.addScore(menu.getUsername(), score);
            menu.setUsername(false);
            menu.setUsername(false);
        }
    }
}