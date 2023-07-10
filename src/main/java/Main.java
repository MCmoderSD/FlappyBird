import com.fasterxml.jackson.databind.JsonNode;

public class Main {
    public static void main(String[] args) {
        new Main().run(args);
    }

    private void run(String[] args) {

        String defaultConfig = "lena"; // Standardkonfiguration
        double osMultiplier = 0.936745818; // Multiplikator für die Tickrate, um die Tickrate auf dem Betriebssystem anzupassen
        int JumpHeight = 7; // Die Sprunghöhe des Spielers
        int Percentage = 25; // Prozentzahl, die die Größe des Hindernisses von der Fensterhöhe ausmacht
        int Gap = 200; // Vertikaler Abstand zwischen den Hindernissen
        int TPS = 100; // Ticks pro Sekunde (aktualisierte Frames pro Sekunde) Maximum: 100

        Utils utils = new Utils(osMultiplier);

        JsonNode config;

        if (args.length < 1) config = utils.checkDate(defaultConfig);
        else if (args[0].toLowerCase().endsWith(".json") ) config = utils.readJson(args[0].toLowerCase());
        else config = utils.readJson(args[0].toLowerCase());

        new Config(
                utils,

                // Spiellogik
                JumpHeight,
                Percentage,
                Gap,
                TPS,

                // Fenster
                config.get("Title").asText(),
                config.get("WindowSizeX").asInt(),
                config.get("WindowSizeY").asInt(),
                config.get("Resizeable").asBoolean(),

                // Assets
                config.get("Background").asText(),
                config.get("Player").asText(),
                config.get("Rainbow").asText(),
                config.get("ObstacleTop").asText(),
                config.get("ObstacleBottom").asText(),
                config.get("Icon").asText(),
                config.get("GameOver").asText(),
                config.get("Pause").asText(),
                config.get("dieSound").asText(),
                config.get("flapSound").asText(),
                config.get("hitSound").asText(),
                config.get("pointSound").asText(),
                config.get("rainbowSound").asText(),
                config.get("backgroundMusic").asText(),
                args
        );
    }
}