package de.MCmoderSD.utilities;

import de.MCmoderSD.main.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class Calculate {

    // Center JFrame
    public static Point centerOfJFrame(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Screen Size
        int x = ((screenSize.width - frame.getWidth()) / 2);
        int y = ((screenSize.height - frame.getHeight()) / 2);
        return new Point(x, y);
    }

    // Calculate average color in rectangle
    public static Color getAverageColorInRectangle(Rectangle rectangle, JPanel panel) {
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        panel.paint(image.getGraphics());

        int startX = rectangle.x;
        int startY = rectangle.y;
        int endX = rectangle.x + rectangle.width;
        int endY = rectangle.y + rectangle.height;

        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;
        int pixelCount = 0;

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                int pixel = image.getRGB(x, y);
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;

                totalRed += red;
                totalGreen += green;
                totalBlue += blue;
                pixelCount++;
            }
        }

        int averageRed = totalRed / pixelCount;
        int averageGreen = totalGreen / pixelCount;
        int averageBlue = totalBlue / pixelCount;

        return new Color(averageRed, averageGreen, averageBlue);
    }

    // Calculate foreground color
    public static Color calculateForegroundColor(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        if (r + g + b > 382) return Color.BLACK;
        else return Color.WHITE;
    }

    // File Checker
    public static boolean doesFileExist(String resourcePath) {
        InputStream inputStream = Main.class.getResourceAsStream(resourcePath);
        return inputStream != null;
    }

    // String to Color
    public static Color hexToColor(String hex) {
        return new Color(Integer.parseInt(hex.substring(1), 16));
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

    // Calculates the color of the text
    public static Color foregroundColor(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        if (r + g + b > 382) return Color.BLACK;
        else return Color.WHITE;
    }

    // Calculates the average color in a rectangle
    public Color averageColorInRectangle(Rectangle rectangle, JPanel panel) {
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        panel.paint(image.getGraphics());

        int startX = rectangle.x;
        int startY = rectangle.y;
        int endX = rectangle.x + rectangle.width;
        int endY = rectangle.y + rectangle.height;

        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;
        int pixelCount = 0;

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                int pixel = image.getRGB(x, y);
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;

                totalRed += red;
                totalGreen += green;
                totalBlue += blue;
                pixelCount++;
            }
        }

        int averageRed = totalRed / pixelCount;
        int averageGreen = totalGreen / pixelCount;
        int averageBlue = totalBlue / pixelCount;

        return new Color(averageRed, averageGreen, averageBlue);
    }

    // Checks if Two HashMaps are equal
    public static <K, V> boolean compareHashMaps(HashMap<K, V> map1, HashMap<K, V> map2) {
        if (map1 == null || map2 == null || map1.size() != map2.size()) return false;
        for (Map.Entry<K, V> entry : map1.entrySet())
            if (!map2.containsKey(entry.getKey()) || !map2.get(entry.getKey()).equals(entry.getValue())) return false;
        return true;
    }
}