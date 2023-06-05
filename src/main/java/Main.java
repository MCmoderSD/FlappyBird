public class Main {
    public static final int JumpHeight = 7; // Die Sprunghöhe des Spielers
    private static final String Background = "Images/Background.png"; // Dateipfad für den Hintergrund
    private static final String Title = "Flappy Bird"; // Titel des Spiels
    private static final String Player = "tests/BirdPlaceHolder.png"; // Dateipfad für das Spielerbild
    private static final int playerWidth = 32, playerHeight = 32; // Breite und Höhe des Spielers
    private static final String ObstacleTop = "tests/ObstaclePlaceHolderGreen.png"; // Dateipfad für das Hindernis von oben
    private static final String ObstacleBottom = "tests/ObstaclePlaceHolderRed.png"; // Dateipfad für das Hindernis von unten
    private static final int obstacleWidth = 32, obstacleHeight = 1024; // Breite und Höhe der Hindernisse
    private static final String Icon = "Images/Icon.png"; // Dateipfad für das Spielsymbol
    private static final String GameOver = "tests/GameOver.png"; // Dateipfad für das Game Over-Bild
    private static final String Pause = "tests/Paused.png"; // Dateipfad für das Pause-Bild
    private static final String dieSound = "sounds/die.wav"; // Dateipfad für den Sterbesound
    private static final String flapSound = "sounds/flap.wav"; // Dateipfad für den Flügelschlag-Sound
    private static final String hitSound = "sounds/hit.wav"; // Dateipfad für den Aufprall-Sound
    private static final String pointSound = "sounds/point.wav"; // Dateipfad für den Punkte-Sound
    private static final int WindowSizeX = 800; // Fensterbreite
    private static final int WindowsSizeY = 800; // Fensterhöhe
    private static final boolean Resizeable = false; // Gibt an, ob das Fenster in der Größe verändert werden kann
    private static final int PlayerPositionX = 250; // Startposition des Spielers auf der x-Achse
    private static final int TPS = 100; // Ticks pro Sekunde (aktualisierte Frames pro Sekunde) Maximum: 100

    public static void main(String[] args) {
        new Methods(); // Erstelle die Methoden
        new Movement(); // Erstelle die Bewegung
        new UI(WindowSizeX, WindowsSizeY, Title, Icon, Resizeable, Background, TPS, args); // Erstelle die Benutzeroberfläche
    }

    public void run(int Tickrate, boolean sound, String[] args) {
        // Starte die Spiellogik mit den angegebenen Parametern
        if (args.length == 0) new Logic(
                WindowSizeX,
                WindowsSizeY,
                Title,
                Icon,
                Resizeable,
                PlayerPositionX,
                playerWidth,
                playerHeight,
                Background,
                Player,
                25,
                200,
                obstacleWidth,
                obstacleHeight,
                ObstacleTop,
                ObstacleBottom,
                GameOver,
                Pause,
                dieSound,
                flapSound,
                hitSound,
                pointSound,
                Tickrate,
                sound
        );
    }
}
