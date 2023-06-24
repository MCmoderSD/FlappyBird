import org.json.JSONObject;

public class Main {
    // Attribute für die Spielkonfiguration
    private static final int JumpHeight = 7; // Die Sprunghöhe des Spielers
    private static final int percentage = 25; // Regelt die höhe der Hindernisse
    private static final int Gap = 200; // Der Abstand zwischen den Hindernissen
    private static final int TPS = 100; // Ticks pro Sekunde (aktualisierte Frames pro Sekunde) Maximum: 100
    private static final double osMultiplier = 0.936745818; // Multiplikator für die Tickrate, um die Tickrate auf dem Betriebssystem anzupassen
    // Attribute für Graphics
    private int WindowSizeX;
    private int WindowSizeY;
    private String Title;
    private  String Icon;
    private  boolean Resizeable;
    private  String Background;
    private  String Player;
    private  String Rainbow;
    private  String ObstacleTop;
    private  String ObstacleBottom;
    private  String GameOver;
    private  String Pause;

    // Attribute für Sound
    private  String dieSound;
    private  String flapSound;
    private  String hitSound;
    private  String pointSound;
    private  String RainbowSound;

    public Main(String[] args) {
        Utils utils = new Utils(osMultiplier);
        JSONObject config = utils.checkDate("LenaBeta");

        if (config != null) {
            // Settings
            WindowSizeX = config.getInt("WindowSizeX");
            WindowSizeY = config.getInt("WindowSizeY");
            Title = config.getString("Title");
            Icon = config.getString("Icon");
            Resizeable = config.getBoolean("Resizeable");

            // Graphics
            Background = config.getString("Background");
            Player = config.getString("Player");
            Rainbow = config.getString("Rainbow");
            ObstacleTop = config.getString("ObstacleTop");
            ObstacleBottom = config.getString("ObstacleBottom");
            GameOver = config.getString("GameOver");
            Pause = config.getString("Pause");

            // Sound
            dieSound = config.getString("dieSound");
            flapSound = config.getString("flapSound");
            hitSound = config.getString("hitSound");
            pointSound = config.getString("pointSound");
            RainbowSound = config.getString("rainbowSound");

            new Movement(utils, WindowSizeX, WindowSizeY, Title, Icon, Resizeable, Background, JumpHeight, TPS, true, args, -10);
        }
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