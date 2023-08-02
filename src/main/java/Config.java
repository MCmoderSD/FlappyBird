import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Objects;

public class Config {
    // Alle Variablen und Assets für die Spielkonfiguration
    private final String Title; // Titel des Spiels
    private final String Background; // Dateipfad für den Hintergrund
    private final String Player ; // Dateipfad für das Spielerbild
    private final String Rainbow; // Dateipfad für das Regenbogenbild
    private final String ObstacleTop; // Dateipfad für das Hindernis von oben
    private final String ObstacleBottom; // Dateipfad für das Hindernis von unten
    private final String Icon; // Dateipfad für das Spielsymbol
    private final String GameOver; // Dateipfad für das Game Over-Bild
    private final String Pause; // Dateipfad für das Pause-Bild
    private final String dieSound; // Dateipfad für den Sterbesound
    private final String flapSound; // Dateipfad für den Flügelschlag-Sound
    private final String hitSound; // Dateipfad für den Aufprall-Sound
    private final String pointSound; // Dateipfad für den Punkte-Sound
    private final String RainbowSound; // Dateipfad für den Regenbogen-Sound
    private final String Music; // Dateipfad für die Hintergrundmusik
    private final String[] args; // Argumente, die beim Starten des Spiels übergeben wurden
    private final int width; // Fensterbreite
    private final int height; // Fensterhöhe
    private final boolean Resizeable; // Gibt an, ob das Fenster in der Größe verändert werden kann
    private final int Percentage; // Prozentzahl, die die Größe des Hindernisses von der Fensterhöhe ausmacht
    private final int Gap; // Vertikaler Abstand zwischen den Hindernissen
    private final int JumpHeight; // Die Sprunghöhe des Spielers

    // Klassenobjekte
    private final Utils utils;

    // Alle Variablen für die Spiellogik
    private double TPS; // Ticks pro Sekunde (aktualisierte Frames pro Sekunde) Maximum: 100
    private int points = -10; // Punkte
    private boolean sound = true; // Gibt an, ob Sounds abgespielt werden sollen

    // Konstruktor zum Initialisieren der Variablen
    public Config(Utils utils, String defaultConfig, int JumpHeight, int Percentage, int Gap, int TPS, String[] args) {

        this.utils = utils;
        this.Gap = Gap;
        this.Percentage = Percentage;
        this.JumpHeight = JumpHeight;
        this.TPS = TPS;
        this.args = args;

        JsonNode config;

        if (args.length < 1) config = utils.checkDate(defaultConfig);
        else if (args[0].toLowerCase().endsWith(".json") ) config = utils.readJson(args[0].toLowerCase());
        else config = utils.readJson(args[0].toLowerCase());

        Title = config.get("Title").asText();

        HashMap<Integer, String> nullCheck = new HashMap<>();
        nullCheck.put(0, config.get("Background").asText());
        nullCheck.put(nullCheck.size(),config.get("Player").asText());
        nullCheck.put(nullCheck.size(),config.get("Rainbow").asText());
        nullCheck.put(nullCheck.size(),config.get("ObstacleTop").asText());
        nullCheck.put(nullCheck.size(),config.get("ObstacleBottom").asText());
        nullCheck.put(nullCheck.size(),config.get("Icon").asText());
        nullCheck.put(nullCheck.size(),config.get("GameOver").asText());
        nullCheck.put(nullCheck.size(),config.get("Pause").asText());
        nullCheck.put(nullCheck.size(),config.get("dieSound").asText());
        nullCheck.put(nullCheck.size(),config.get("flapSound").asText());
        nullCheck.put(nullCheck.size(),config.get("hitSound").asText());
        nullCheck.put(nullCheck.size(),config.get("pointSound").asText());
        nullCheck.put(nullCheck.size(),config.get("rainbowSound").asText());
        nullCheck.put(nullCheck.size(),config.get("backgroundMusic").asText());

        for (int i = 0; i < nullCheck.size(); i++) {
            assert nullCheck.get(i) != null;
            if (Objects.equals(nullCheck.get(i), "") && i < 8) nullCheck.put(i, "error/empty.png");
            else if (Objects.equals(nullCheck.get(i), "") && i > 7) nullCheck.put(i, "error/empty.wav");
        }

        Background = nullCheck.get(0);
        Player = nullCheck.get(1);
        Rainbow = nullCheck.get(2);
        ObstacleTop = nullCheck.get(3);
        ObstacleBottom = nullCheck.get(4);
        Icon = nullCheck.get(5);
        GameOver = nullCheck.get(6);
        Pause = nullCheck.get(7);
        dieSound = nullCheck.get(8);
        flapSound = nullCheck.get(9);
        hitSound = nullCheck.get(10);
        pointSound = nullCheck.get(11);
        RainbowSound = nullCheck.get(12);
        Music = nullCheck.get(13);

        int[] dimension = utils.maxDimension(config.get("WindowSizeX").asInt(), config.get("WindowSizeY").asInt());
        width = dimension[0];
        height = dimension[1];

        Resizeable = config.get("Resizeable").asBoolean();

        // Starte die Bewegungslogik mit den angegebenen Parametern
        new UI(this, utils);
    }

    public int getJumpHeight() {
        return JumpHeight;
    }
    public int getPercentage() {
        return Percentage;
    }
    public int getGap() {
        return Gap;
    }
    public double getTPS() {
        return TPS;
    }
    public void setTPS(double TPS) {
        this.TPS = TPS;
    }
    public int getPoints() {
        return points;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    public String getTitle() {
        return Title;
    }
    public int getWindowSizeX() {
        return width;
    }
    public int getWindowSizeY() {
        return height;
    }
    public boolean isResizeable() {
        return Resizeable;
    }
    public String getBackground() {
        return Background;
    }
    public String getPlayer() {
        return Player;
    }
    public String getRainbow() {
        return Rainbow;
    }
    public String getObstacleTop() {
        return ObstacleTop;
    }
    public String getObstacleBottom() {
        return ObstacleBottom;
    }
    public String getIcon() {
        return Icon;
    }
    public String getGameOver() {
        return GameOver;
    }
    public String getPause() {
        return Pause;
    }
    public String getDieSound() {
        return dieSound;
    }
    public String getFlapSound() {
        return flapSound;
    }
    public String getHitSound() {
        return hitSound;
    }
    public String getPointSound() {
        return pointSound;
    }
    public String getRainbowSound() {
        return RainbowSound;
    }
    public String getMusic() {
        return Music;
    }
    public String[] getArgs() {
        return args;
    }
    public boolean isSound() {
        return sound;
    }
    public void setSound(boolean sound) {
        this.sound = sound;
    }
    public Utils getUtils() {
        return utils;
    }
}