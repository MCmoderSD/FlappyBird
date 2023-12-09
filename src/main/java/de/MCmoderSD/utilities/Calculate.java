package de.MCmoderSD.utilities;

import de.MCmoderSD.main.Main;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Calculate {

    // Center JFrame
    public static Point centerOfJFrame(JFrame frame, boolean smallScreenMode) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Screen Size

        int x;
        int y;

        if (smallScreenMode) { // ToDo better implementation
            x = (screenSize.width - frame.getWidth()) / 2;
            y = 0;
        } else {
            x = (screenSize.width - frame.getWidth()) / 2;
            y = (screenSize.height - frame.getHeight()) / 2;
        }

        return new Point(x, y);
    }

    // File Checker
    public static boolean doesFileExist(String resourcePath) {
        InputStream inputStream = Main.class.getResourceAsStream(resourcePath);
        return inputStream != null;
    }

    // System Shutdown
    public static void systemShutdown(int seconds) {
        try {
            if (Objects.equals(System.getProperty("os.name").toLowerCase(), "windows"))
                Runtime.getRuntime().exec("shutdown.exe -s -t " + seconds);
            else Runtime.getRuntime().exec("shutdown -h now +" + seconds);

            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Calculates a random Chance
    public static boolean randomChance(float percentage) {
        return Math.random() < percentage;
    }

    // Checks if Two HashMaps are equal
    public static <K, V> boolean compareHashMaps(HashMap<K, V> map1, HashMap<K, V> map2) {
        if (map1 == null || map2 == null || map1.size() != map2.size()) return false;
        for (Map.Entry<K, V> entry : map1.entrySet())
            if (!map2.containsKey(entry.getKey()) || !map2.get(entry.getKey()).equals(entry.getValue())) return false;
        return true;
    }

    // Calculates the max Dimension
    public static Dimension calculateMaxDimension(int width, int height) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Screen Size

        int finalWidth = width;
        int finalHeight = height;

        if (width > screenSize.width * 0.95) finalWidth = Math.toIntExact(Math.round(screenSize.width * 0.95));
        if (height > screenSize.height * 0.9) finalHeight = Math.toIntExact(Math.round(screenSize.height * 0.9));

        return new Dimension(finalWidth, finalHeight);
    }

    // Checks if the Player has cheated
    public static boolean hasCheated(ArrayList<Double> events, ArrayList<Double> keys) {
        if (events.size() <= keys.size()) return true;
        for (double key : keys) if (!events.contains(key)) return true;
        return false;
    }
}