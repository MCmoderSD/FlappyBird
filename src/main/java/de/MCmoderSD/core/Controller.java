package de.MCmoderSD.core;

import de.MCmoderSD.UI.Frame;
import de.MCmoderSD.UI.Menu;
import de.MCmoderSD.main.Config;
import de.MCmoderSD.main.Main;
import de.MCmoderSD.utilities.database.MySQL;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Controller {

    // Associations
    private final Frame frame;
    private final MySQL mySQL;

    // Attributes
    private int score;

    // Constructor
    public Controller(Frame frame) {

        // Initialize Attributes
        this.frame = frame;
        Menu menu = frame.getMenu();

        // Initialize MySQL
        mySQL = new MySQL(Config.DATABASE, Config.IS_REVERSE);
        if (!Config.VALID_CONFIG && mySQL.isConnected()) mySQL.disconnect();

        menu.setScoreBoard(mySQL.isConnected());
        if (!mySQL.isConnected()) return;

        // Update Loop
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(() -> menu.getScoreBoard().setHashMap(mySQL.pullFromMySQL()), 0, 100, TimeUnit.MILLISECONDS);
    }

    // Checks if the username is valid
    private boolean isUsernameValid(String username) {
        for (String word : username.toLowerCase().split("\\W+"))
            if (Config.BLOCKED_TERMS.contains(word)) return false;
        return true;
    }

    // Toggles the reverse mode
    public void toggleReverse() {
        if (frame.getGameUI().isVisible()) return;
        Main.main(new String[]{Config.LANGUAGE, Config.CONFIGURATION, (Config.IS_REVERSE ? "" : "r")});
        frame.dispose();
    }

    // Asset Switch
    public void switchAsset() {
        if (frame.getGameUI().isVisible()) return;
        int i = 0;
        while (i < Main.CONFIGURATIONS.length) {
            if (Objects.equals(Main.CONFIGURATIONS[i], Config.CONFIGURATION)) break;
            i++;
        }

        if (i + 1 == Main.CONFIGURATIONS.length) i = 0;
        else i++;

        if (Config.ARGS.length == 3)
            Main.main(new String[]{Config.LANGUAGE, Main.CONFIGURATIONS[i], Config.ARGS[2]});
        else Main.main(new String[]{Config.LANGUAGE, Main.CONFIGURATIONS[i]});
        frame.dispose();
    }

    // Starts the game
    public void startGame() {
        Menu menu = frame.getMenu();

        frame.getGame().init(menu.getBackgroundPos());
        frame.getGame().initGameConstants(menu.isSound(), menu.getFps());

        // Set GameUI visible
        frame.getMenu().setVisible(false);
        frame.getGameUI().setVisible(true);
        frame.requestFocusInWindow();
    }

    // Restarts the game
    public void restart(boolean debug, boolean cheats, boolean sound, int score) {
        Menu menu = frame.getMenu();

        this.score = score;
        menu.setSound(sound);

        if (mySQL.isConnected() && !debug && !cheats && score > 0) {
            menu.setUsername(true);
            menu.setHeadline(Config.INSTRUCTION + score);
        }

        frame.getGameUI().setVisible(false);
        menu.setVisible(true);
    }

    // Uploads the score to the database
    public void uploadScore() {
        Menu menu = frame.getMenu();
        String username = menu.getUsername();

        if (username.isEmpty()) {
            menu.setUsername(false);
            return;
        }

        while (username.startsWith(" ")) username = username.substring(1);
        while (username.endsWith(" ")) username = username.substring(0, username.length() - 1);

        if (!isUsernameValid(username) || username.length() < 3 || username.length() > 32) {
            frame.showMessage(Config.INVALID_USERNAME, Config.INVALID_USERNAME_TITLE);
            return;
        } else menu.setUsername(false);

        if (score > mySQL.pullFromMySQL().getOrDefault(username, 0)) mySQL.pushToMySQL(username, score);
        menu.setUsername(false);
    }

    public void toggleSound() {
        frame.getMenu().setSound(!frame.getMenu().isSound());
    }
}