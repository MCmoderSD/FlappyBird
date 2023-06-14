public class Main {
    //TODO: MOVE!!!!

    // Attribute für die Spielkonfiguration
    public static final int JumpHeight = 7; // Die Sprunghöhe des Spielers
    private static final String Title = "Flappy Bird"; // Titel des Spiels
    private static final String Background = "911/Skyline.png"; // Dateipfad für den Hintergrund
    private static final String Player = "911/Plane.png"; // Dateipfad für das Spielerbild
    private static final String Rainbow = "Lena/rainbowBird.gif"; // Dateipfad für das Regenbogenbild
    private static final String ObstacleTop = "911/TowerTop.png"; // Dateipfad für das Hindernis von oben
    private static final String ObstacleBottom = "911/TowerBottom.png"; // Dateipfad für das Hindernis von unten
    private static final String Icon = "Images/Icon.png"; // Dateipfad für das Spielsymbol
    private static final String GameOver = "tests/GameOver.png"; // Dateipfad für das Game Over-Bild
    private static final String Pause = "tests/Paused.png"; // Dateipfad für das Pause-Bild
    private static final String dieSound = "sounds/die.wav"; // Dateipfad für den Sterbesound
    private static final String flapSound = "sounds/flap.wav"; // Dateipfad für den Flügelschlag-Sound
    private static final String hitSound = "sounds/hit.wav"; // Dateipfad für den Aufprall-Sound
    private static final String pointSound = "sounds/point.wav"; // Dateipfad für den Punkte-Sound
    private static final String RainbowSound = "sounds/rainbow.wav"; // Dateipfad für den Regenbogen-Sound
    private static final int WindowSizeX = 800; // Fensterbreite
    private static final int WindowsSizeY = 800; // Fensterhöhe
    private static final boolean Resizeable = false; // Gibt an, ob das Fenster in der Größe verändert werden kann
    private static final int TPS = 100; // Ticks pro Sekunde (aktualisierte Frames pro Sekunde) Maximum: 100

    //TODO: change comments to java docs
    public static void main(String[] args) {
        // Erstelle die Methoden
        Utils utils = new Utils();

        // Erstelle die Bewegung
        Movement movement = new Movement();

        // Erstelle die Benutzeroberfläche
        new UI(utils, movement, WindowSizeX, WindowsSizeY, Title, Icon, Resizeable, Background, TPS, true, args, -10);
    }

    // Methode zum Starten des Spiels
    public void run(Utils utils, Movement movement, int Tickrate, boolean sound, String[] args) {

        // Starte die Spiellogik mit den angegebenen Parametern
        if (args.length == 0) {
            new Logic(
                    utils,
                    movement,
                    WindowSizeX,
                    WindowsSizeY,
                    Title,
                    Icon,
                    Resizeable,
                    Background,
                    Player,
                    Rainbow,
                    25,
                    200,
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