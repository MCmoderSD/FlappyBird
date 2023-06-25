import com.fasterxml.jackson.databind.JsonNode;

public class Main {
    // Spielparameter
    private final int JumpHeight = 7; // Die Sprunghöhe des Spielers
    private final int TPS = 100; // Ticks pro Sekunde (aktualisierte Frames pro Sekunde) Maximum: 100
    private final double osMultiplier = 0.936745818; // Multiplikator für die Tickrate, um die Tickrate auf dem Betriebssystem anzupassen
    private final int percentage = 25; // Höhe der Lücke in Prozent
    private final int Gap = 200; // Höhe der Lücke in Pixeln

    // Grafikparameter
    private String Title; // Titel des Spiels
    private String Background; // Dateipfad für den Hintergrund
    private String Player; // Dateipfad für das Spielerbild
    private String Rainbow; // Dateipfad für das Regenbogenbild
    private String ObstacleTop; // Dateipfad für das Hindernis von oben
    private String ObstacleBottom; // Dateipfad für das Hindernis von unten
    private String Icon; // Dateipfad für das Spielsymbol
    private String GameOver; // Dateipfad für das Game Over-Bild
    private String Pause; // Dateipfad für das Pause-Bild
    private String dieSound; // Dateipfad für den Sterbesound
    private String flapSound; // Dateipfad für den Flügelschlag-Sound
    private String hitSound; // Dateipfad für den Aufprall-Sound
    private String pointSound; // Dateipfad für den Punkte-Sound
    private String RainbowSound; // Dateipfad für den Regenbogen-Sound
    private int WindowSizeX; // Fensterbreite
    private int WindowSizeY; // Fensterhöhe
    private boolean Resizeable; // Gibt an, ob das Fenster in der Größe verändert werden kann

    public Main(String[] args) {
        Utils utils = new Utils(osMultiplier);
        JsonNode config = utils.loadConfig("config/LenaBeta.json");

        Title = config.get("Title").asText();
        Background = config.get("Background").asText();
        Player = config.get("Player").asText();
        Rainbow = config.get("Rainbow").asText();
        ObstacleTop = config.get("ObstacleTop").asText();
        ObstacleBottom = config.get("ObstacleBottom").asText();
        Icon = config.get("Icon").asText();
        GameOver = config.get("GameOver").asText();
        Pause = config.get("Pause").asText();
        dieSound = config.get("dieSound").asText();
        flapSound = config.get("flapSound").asText();
        hitSound = config.get("hitSound").asText();
        pointSound = config.get("pointSound").asText();
        RainbowSound = config.get("rainbowSound").asText();
        WindowSizeX = config.get("WindowSizeX").asInt();
        WindowSizeY = config.get("WindowSizeY").asInt();
        Resizeable = config.get("Resizeable").asBoolean();

        new Movement(utils, WindowSizeX, WindowSizeY, Title, Icon, Resizeable, Background, JumpHeight, TPS, true, args, -10);
    }

    public static void main(String[] args) {
        new Main(args);
    }

    public void run(Utils utils, Movement movement, int JumpHeight, double Tickrate, boolean sound, String[] args) {


        // Starte die Spiellogik mit den angegebenen Parametern
        if (args.length == 0) {
            new GameUI(
                    utils,
                    movement,
                    WindowSizeX,
                    WindowSizeY,
                    Title,
                    Icon,
                    Resizeable,
                    Background,
                    Player,
                    Rainbow,
                    JumpHeight,
                    percentage,
                    Gap,
                    ObstacleTop,
                    ObstacleBottom,
                    GameOver,
                    Pause,
                    dieSound,
                    flapSound,
                    hitSound,
                    pointSound,
                    RainbowSound,
                    Tickrate,
                    sound,
                    args
            );
        }
    }
}