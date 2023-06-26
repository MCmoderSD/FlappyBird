public class Config {
    // Attribute für das Fenster
    public final String Title;
    public final int WindowSizeX;
    public final int WindowsSizeY;
    public final boolean Resizeable;

    // Attribute für die Assets
    public final String Background;
    public final String Player;
    public final String Rainbow;
    public final String ObstacleTop;
    public final String ObstacleBottom;
    public final String Icon;
    public final String GameOver;
    public final String Pause;
    public final String dieSound;
    public final String flapSound;
    public final String hitSound;
    public final String pointSound;
    public final String RainbowSound;
    public final Utils utils;
    public Config(Utils utils, String title, int windowSizeX, int windowsSizeY, boolean resizeable, String background, String player, String rainbow, String obstacleTop, String obstacleBottom, String icon, String gameOver, String pause, String dieSound, String flapSound, String hitSound, String pointSound, String rainbowSound) {
        this.utils = utils;
        Title = title;
        WindowSizeX = windowSizeX;
        WindowsSizeY = windowsSizeY;
        Resizeable = resizeable;
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
    }
}
