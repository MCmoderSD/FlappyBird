public class Main {
    // Definiert die Höhe des Sprungs
    public static final int JumpHeight = 7;
    // Dateiname des Hintergrundbildes
    public static final String Background = "Images/Background.png";
    // Definiert den Titel des Spiels
    private static final String Title = "Flappy Bird";
    // Dateiname des Bildes für den Spieler
    private static final String Player = "BirdPlaceHolder.png";
    private static final int playerWidth = 32, playerHeight = 32;
    // Dateiname des Bildes für das Hindernis
    private static final String ObstacleTop = "ObstaclePlaceHolderGreen.png";
    private static final String ObstacleBottom = "ObstaclePlaceHolderRed.png";
    private static final int obstacleWidth = 32, obstacleHeight = 1024;
    // Dateiname des Icons
    private static final String Icon = "Images/Icon.png";

    // Dateiname des Game-Over-Bildes
    private static final String GameOver = "GameOver.png";

    // Dateinamen der Soundeffekte
    private static final String dieSound = "sounds/die.wav";
    private static final String flapSound = "sounds/flap.wav";
    private static final String hitSound = "sounds/hit.wav";
    private static final String pointSound = "sounds/point.wav";

    // Größe des Spielfensters in der horizontalen Richtung
    private static final int WindowSizeX = 800;

    // Größe des Spielfensters in der vertikalen Richtung
    private static final int WindowsSizeY = 800;

    // Legt fest, ob das Spielfenster in der Größe verändert werden kann
    private static final boolean Resizeable = false;

    // X-Position des Spielers
    private static final int PlayerPositionX = 250;

    // Anzahl der Aktualisierungen pro Sekunde (TPS)
    private static final int TPS = 100;
    private static final boolean sound = true;

    // Die main-Methode, die das Spiel startet
    public static void main(String[] args) {
        // Erstellt eine neue Instanz der GameLogic-Klasse, um das Spiel zu starten
        new GameLogic(WindowSizeX, WindowsSizeY, Title, Icon, Resizeable, PlayerPositionX, playerWidth, playerHeight, Background, Player,25, 200, obstacleWidth, obstacleHeight, ObstacleTop, ObstacleBottom, GameOver, dieSound, flapSound, hitSound, pointSound, getTPS(), sound);
    }

    // Gibt die Anzahl der Aktualisierungen pro Sekunde (TPS) zurück
    public static int getTPS() {
        return 1000 / TPS;
    }

    public void run(boolean sound) {
        new GameLogic(WindowSizeX, WindowsSizeY, Title, Icon, Resizeable, PlayerPositionX, playerWidth, playerHeight, Background, Player,25, 200, obstacleWidth, obstacleHeight, ObstacleTop, ObstacleBottom, GameOver, dieSound, flapSound, hitSound, pointSound, getTPS(), sound);
    }
}
