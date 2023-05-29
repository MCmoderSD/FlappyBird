public class Main {
    // Definiert den Titel des Spiels
    public static final String Title = ("Flappy Bird");
    // Dateiname des Bildes für den Spieler
    public static final String Player = ("BirdPlaceHolder.png");
    public static final int playerWidth = 32, playerHeight = 32;
    // Dateiname des Bildes für das Hindernis
    public static final String ObstacleTop = ("ObstaclePlaceHolderGreen.png"), ObstacleBottom = ("ObstaclePlaceHolderRed.png");
    public static final int obstacleWidth = 32, obstacleHeight = 1024;
    // Dateiname des Hintergrundbildes
    public static final String Background = ("Images/Background.png");

    public static final String Icon =("Images/Icon.png");
    // Dateiname des Game-Over-Bildes
    public static final String GameOver = ("GameOver.png");
    // Größe des Spielfensters in der horizontalen Richtung
    public static final int WindowSizeX = 800;
    // Größe des Spielfensters in der vertikalen Richtung
    public static final int WindowsSizeY = 800;

    public static final boolean Resizeable = false;
    // Höhe, um die der Spieler springt
    public static final int PlayerPositionX = 250;
    public static final int JumpHeight = 7;
    private static final int TPS = 100;

    // Die main-Methode, die das Spiel startet
    public static void main(String[] args) {
        // Erstellt eine neue Instanz der GameLogic-Klasse, um das Spiel zu starten
        new GameLogic(WindowSizeX, WindowsSizeY, Title, Icon, Resizeable,
                PlayerPositionX, playerWidth, playerHeight, Player,
                25, 200, obstacleWidth, obstacleHeight, ObstacleTop, ObstacleBottom,
                GameOver, getTPS());
    }

    // Gibt die Anzahl der Aktualisierungen pro Sekunde (TPS) zurück
    public static int getTPS() {
        return 1000 / TPS;
    }
}
