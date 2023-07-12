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
    private final Movement movement;

    // Alle Variablen für die Spiellogik
    private double TPS; // Ticks pro Sekunde (aktualisierte Frames pro Sekunde) Maximum: 100
    private int points = -10; // Punkte
    private boolean sound = true; // Gibt an, ob Sounds abgespielt werden sollen

    // Konstruktor zum Initialisieren der Variablen
    public Config(Utils utils, int JumpHeight, int Percentage, int Gap, int TPS, String title, int width, int height, boolean resizeable, String background, String player, String rainbow, String obstacleTop, String obstacleBottom, String icon, String gameOver, String pause, String dieSound, String flapSound, String hitSound, String pointSound, String rainbowSound, String music, String[] args) {

        this.utils = utils;
        this.Gap = Gap;
        this.Percentage = Percentage;
        Title = title;
        this.width = width;
        this.height = height;
        this.JumpHeight = JumpHeight;
        this.TPS = TPS;
        Resizeable = resizeable;
        this.args = args;

        if (Objects.equals(background, "")) background = "error/empty.png";
        if (Objects.equals(player, "")) player = "error/empty.png";
        if (Objects.equals(rainbow, "")) rainbow = player;
        if (Objects.equals(obstacleTop, "")) obstacleTop = "error/empty.png";
        if (Objects.equals(obstacleBottom, "")) obstacleBottom = "error/empty.png";
        if (Objects.equals(icon, "")) icon = "error/empty.png";
        if (Objects.equals(gameOver, "")) gameOver = "error/empty.png";
        if (Objects.equals(pause, "")) pause = "error/empty.png";
        if (Objects.equals(dieSound, "")) dieSound = "error/empty.wav";
        if (Objects.equals(flapSound, "")) flapSound = "error/empty.wav";
        if (Objects.equals(hitSound, "")) hitSound = "error/empty.wav";
        if (Objects.equals(pointSound, "")) pointSound = "error/empty.wav";
        if (Objects.equals(rainbowSound, "")) rainbowSound = "error/empty.wav";
        if (Objects.equals(music, "")) music = "error/empty.wav";

        Background = background;
        Player = player;
        Rainbow = rainbow;
        ObstacleTop = obstacleTop;
        ObstacleBottom = obstacleBottom;
        Icon = icon;
        GameOver = gameOver;
        Pause = pause;
        this.dieSound = dieSound;
        this.flapSound = flapSound;
        this.hitSound = hitSound;
        this.pointSound = pointSound;
        RainbowSound = rainbowSound;
        Music = music;

        // Starte die Bewegungslogik mit den angegebenen Parametern
        movement = new  Movement(this);
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
    public Movement getMovement() {
        return movement;
    }
}