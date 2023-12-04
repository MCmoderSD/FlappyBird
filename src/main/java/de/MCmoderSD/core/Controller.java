package de.MCmoderSD.core;

import de.MCmoderSD.UI.Frame;
import de.MCmoderSD.UI.InputHandler;
import de.MCmoderSD.UI.Menu;
import de.MCmoderSD.UI.ScoreBoard;
import de.MCmoderSD.main.Config;
import de.MCmoderSD.utilities.database.MySQL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    // Checks if the username is valid
    public boolean isUsernameValid(String username, String blockListPath) {
        List<String> blockedTerms = new ArrayList<>();
        try {
            InputStream inputStream;
            if (blockListPath.startsWith("/"))
                inputStream = getClass().getResourceAsStream(blockListPath); // Relative path
            else inputStream = Files.newInputStream(Paths.get(blockListPath)); // Absolute path

            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));

            String line;
            while ((line = reader.readLine()) != null) blockedTerms.add(line);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        String[] usernameWords = username.toLowerCase().split("\\W+");

        for (String word : usernameWords) if (blockedTerms.contains(word)) return false;
        return true;
    }

    // Updates the ScoreBoard
    private void updateScoreBoard() {
        ScoreBoard scoreBoard = frame.getMenu().getScoreBoard();
        scoreBoard.setHashMap(mySQL.pullFromMySQL());
    }

    // Starts the game
    public void startGame() {
        Menu menu = frame.getMenu();

        frame.getGame().init();
        frame.getGame().initGameConstants(menu.isSound(), menu.getFps());

        // Set GameUI visible
        frame.getMenu().setVisible(false);
        frame.getGameUI().setVisible(true);
        frame.requestFocusInWindow();
    }

    // Restarts the game
    public void restart(boolean debug, boolean cheats, int score) {
        Menu menu = frame.getMenu();

        this.score = score;

        if (!debug && !cheats && score > 0) {
            menu.setUsername(true);
            menu.setHeadline(config.getInstruction() + score);
        }

        frame.getGameUI().setVisible(false);
        menu.setVisible(true);
    }

    // Uploads the score to the database
    public void uploadScore() {
        Menu menu = frame.getMenu();
        String username = menu.getUsername();

        if (score > menu.getScore(username)) {
            if (!isUsernameValid(username, config.getBlockedTermsPath())) return;
            mySQL.addScore(menu.getUsername(), score);
            menu.setUsername(false);
        } else frame.showMessage(config.getInvalidUsername(), config.getInvalidUsernameTitle());
    }
}