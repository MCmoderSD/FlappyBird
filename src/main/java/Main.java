import com.fasterxml.jackson.databind.JsonNode;
public class Main {
    private final Config config;
    public Main() {
        double osMultiplier = 0.936745818;
        Utils utils = new Utils(osMultiplier);
        JsonNode json = utils.checkDate("LenaBeta");
        config = new Config(
                utils,
                // Attribute für das Fenster
                json.get("Title").asText(),
                json.get("WindowSizeX").asInt(),
                json.get("WindowSizeY").asInt(),
                json.get("Resizeable").asBoolean(),

                // Attribute für die Assets
                json.get("Background").asText(),
                json.get("Player").asText(),
                json.get("Rainbow").asText(),
                json.get("ObstacleTop").asText(),
                json.get("ObstacleBottom").asText(),
                json.get("Icon").asText(),
                json.get("GameOver").asText(),
                json.get("Pause").asText(),
                json.get("dieSound").asText(),
                json.get("flapSound").asText(),
                json.get("hitSound").asText(),
                json.get("pointSound").asText(),
                json.get("rainbowSound").asText()
        );
    }

    public static void main(String[] args) {
        new Main().init(args);
    }

    private void init(String[] args) {
        int jumpHeight = 7;
        int TPS = 100;

        new Movement(config.utils, config.WindowSizeX, config.WindowsSizeY, config.Title, config.Icon, config.Resizeable, config.Background, jumpHeight, TPS, true, args, -10);
    }

    public void run(Utils utils, Movement movement, int JumpHeight, double Tickrate, boolean sound, String[] args) {
        int Gap = 200;
        int Percentage = 25;

        if (args.length == 0) {
            new GameUI(
                    utils,
                    movement,
                    config.WindowSizeX,
                    config.WindowsSizeY,
                    config.Title,
                    config.Icon,
                    config.Resizeable,
                    config.Background,
                    config.Player,
                    config.Rainbow,
                    JumpHeight,
                    Percentage,
                    Gap,
                    config.ObstacleTop,
                    config.ObstacleBottom,
                    config.GameOver,
                    config.Pause,
                    config.dieSound,
                    config.flapSound,
                    config.hitSound,
                    config.pointSound,
                    config.RainbowSound,
                    Tickrate,
                    sound,
                    args
            );
        }
    }
}