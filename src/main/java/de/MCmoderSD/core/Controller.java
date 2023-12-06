package de.MCmoderSD.core;

import de.MCmoderSD.UI.Frame;
import de.MCmoderSD.UI.Menu;
import de.MCmoderSD.main.Config;
import de.MCmoderSD.main.Main;
import de.MCmoderSD.utilities.database.MySQL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Controller {

    // Associations
    private final Frame frame;
    private final Config config;
    private final MySQL mySQL;

    // Attributes
    private final ArrayList<String> blockedTerms;
    private int score;

    public Controller(Frame frame, Config config) {
        super();
        this.frame = frame;
        this.config = config;

        mySQL = new MySQL(config.getDatabase());

        blockedTerms = new ArrayList<>();

        try {
            InputStream inputStream;
            if (config.getBlockedTermsPath().startsWith("/"))
                inputStream = getClass().getResourceAsStream(config.getBlockedTermsPath()); // Relative path
            else inputStream = Files.newInputStream(Paths.get(config.getBlockedTermsPath())); // Absolute path

            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));

            String line;
            while ((line = reader.readLine()) != null) blockedTerms.add(line);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        // Update Loop
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(() -> frame.getMenu().getScoreBoard().setHashMap(mySQL.pullFromMySQL()), 0, 100, TimeUnit.MILLISECONDS);
    }

    // Check for Asset Switch
    public void switchAsset() {
        if (frame.getGameUI().isVisible()) return;

        int i = 0;
        while (i < Main.CONFIGURATIONS.length) {
            if (Objects.equals(Main.CONFIGURATIONS[i], config.getConfiguration())) break;
            i++;
        }

        if (i + 1 == Main.CONFIGURATIONS.length) i = 0;
        else i++;


        if (config.getArgs().length == 3)
            Main.main(new String[]{config.getLanguage(), Main.CONFIGURATIONS[i], config.getArgs()[2]});
        else Main.main(new String[]{config.getLanguage(), Main.CONFIGURATIONS[i]});
        frame.dispose();
    }

    // Checks if the username is valid
    private boolean isUsernameValid(String username) {
        for (String word : username.toLowerCase().split("\\W+")) if (blockedTerms.contains(word)) return false;
        return true;
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

        if (username.isEmpty()) {
            menu.setUsername(false);
            return;
        }

        while (username.startsWith(" ")) username = username.substring(1);
        while (username.endsWith(" ")) username = username.substring(0, username.length() - 1);

        if (!isUsernameValid(username) || username.length() < 3 || username.length() > 32) {
            frame.showMessage(config.getInvalidUsername(), config.getInvalidUsernameTitle());
            return;
        } else menu.setUsername(false);

        if (score > mySQL.pullFromMySQL().getOrDefault(username, 0)) mySQL.pushToMySQL(username, score);
        menu.setUsername(false);
    }

    public void toggleSound() {
        frame.getMenu().setSound(!frame.getMenu().isSound());
    }
}