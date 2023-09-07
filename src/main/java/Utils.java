import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

// Class for all utilities
public class Utils {
    private final HashMap<String, Clip> heavyClipCache = new HashMap<>(); // Cache for AudioClips
    private final HashMap<String, BufferedImage> bufferedImageCache = new HashMap<>(); // Cache for BufferedImages
    private final HashMap<String, ImageIcon> imageIconCache = new HashMap<>(); // Cache for ImageIcons
    private final ArrayList<BufferedInputStream> heavyBufferedInputStreamCache = new ArrayList<>(); // Cache for BufferedInputStreams
    private final ArrayList<AudioInputStream> heavyAudioInputStreamCache = new ArrayList<>(); // Cache for AudioInputStreams
    private long startTime = System.currentTimeMillis();
    private boolean audioIsStopped, customConfig = false, smallScreen = false;

    // Calculate the player's trajectory
    public int calculateGravity(int x) {
        return -1 * x + 4;
    }

    // Load image files
    public BufferedImage readImage(String resource) {
        if (bufferedImageCache.containsKey(resource))
            return bufferedImageCache.get(resource); // Check if the path has already been loaded
        BufferedImage image = null;
        try {
            if (resource.endsWith(".png")) {
                if (customConfig && !resource.startsWith("error"))
                    image = ImageIO.read(Files.newInputStream(Paths.get(resource)));
                else image = ImageIO.read(Objects.requireNonNull(getClass().getResource(resource)));
            } else throw new IllegalArgumentException("Unsupported image format: " + resource);
            bufferedImageCache.put(resource, image); // Add the image to the cache

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        if (image == null) throw new IllegalArgumentException("Could not load the image: " + resource);
        return image;
    }

    // Create an ImageIcon from images
    public ImageIcon createImageIcon(String resource) {
        if (imageIconCache.containsKey(resource))
            return imageIconCache.get(resource); // Check if the path has already been loaded
        ImageIcon imageIcon;
        if (resource.endsWith(".png")) {
            imageIcon = new ImageIcon(readImage(resource)); // Create an ImageIcon
        } else if (resource.endsWith(".gif")) {
            URL imageUrl = getClass().getClassLoader().getResource(resource);
            imageIcon = new ImageIcon(Objects.requireNonNull(imageUrl));
        } else throw new IllegalArgumentException("Unsupported image format: " + resource);

        imageIconCache.put(resource, imageIcon); // Add the image to the cache
        return imageIcon;
    }

    // Center an image in the middle
    public Point locatePoint(String image, int width, int height) {
        BufferedImage img = readImage(image);
        return new Point((width - img.getWidth()) / 2, (height - img.getHeight()) / 2);
    }

    // Load music files and play them
    public void audioPlayer(GamePanel gamePanel, String audioFilePath, boolean sound, boolean loop) {
        if (sound && !gamePanel.isPaused && !Objects.equals(audioFilePath, "error/empty.wav")) {
            if (!loop) audioIsStopped = false;
            CompletableFuture.runAsync(() -> {
                try {
                    if (heavyClipCache.get(audioFilePath) != null) {
                        Clip clip = heavyClipCache.get(audioFilePath);
                        clip.setFramePosition(0);
                        clip.start();
                        return;
                    }

                    ClassLoader classLoader = getClass().getClassLoader();
                    InputStream audioFileInputStream;

                    if (customConfig) audioFileInputStream = Files.newInputStream(Paths.get(audioFilePath));
                    else audioFileInputStream = classLoader.getResourceAsStream(audioFilePath);

                    // Check if the audio file is found
                    if (audioFileInputStream == null)
                        throw new IllegalArgumentException("The audio file was not found: " + audioFilePath);

                    BufferedInputStream bufferedInputStream = new BufferedInputStream(audioFileInputStream);
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);

                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);

                    // Load long audio files into the cache to free up resources
                    if (clip.getMicrosecondLength() > 1000000 || loop) {
                        heavyBufferedInputStreamCache.add(bufferedInputStream);
                        heavyAudioInputStreamCache.add(audioInputStream);
                        heavyClipCache.put(audioFilePath, clip);
                    }

                    // Add a LineListener to release resources when playback is finished
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            try {
                                if (loop && gamePanel.gameOver && !audioIsStopped)
                                    audioPlayer(gamePanel, audioFilePath, true, true);
                                else if (!heavyClipCache.containsKey(audioFilePath)) {
                                    clip.close();
                                    audioInputStream.close();
                                    bufferedInputStream.close();
                                }

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                    clip.start();

                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            });
        }
    }

    // Stop the music
    public void stopHeavyAudio() {
        CompletableFuture.runAsync(() -> {
            try {
                audioIsStopped = true;
                for (Clip clip : heavyClipCache.values()) clip.stop();
                for (AudioInputStream audioInputStream : heavyAudioInputStreamCache) audioInputStream.close();
                for (BufferedInputStream bufferedInputStream : heavyBufferedInputStreamCache)
                    bufferedInputStream.close();

                heavyBufferedInputStreamCache.clear();
                heavyAudioInputStreamCache.clear();
                heavyClipCache.clear();

            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        });
    }

    // Calculate the width of the background
    public int getBackgroundWidth(String path) {
        return bufferedImageCache.get(path).getWidth();
    }

    // Placeholder for text fields (the username)
    public void setPlaceholder(JTextField textField, String placeholder, JPanel panel) {

        Color foregroundColor = calculateForegroundColor(getAverageColorInRectangle(getBottomMenuBounds(panel), panel));
        Font originalFont = textField.getFont();

        textField.setForeground(Color.GRAY);
        textField.setFont(originalFont);
        textField.setText(placeholder);

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(foregroundColor);
                    textField.setFont(originalFont);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setFont(originalFont);
                    textField.setText(placeholder);
                }
            }
        });
    }

    // Checks the internet connection to the SQL Server
    public boolean checkSQLConnection(String ip, String port) {
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            try (Socket socket = new Socket()) {
                // Attempt to connect to the server
                socket.connect(new InetSocketAddress(ip, Integer.parseInt(port)), 1000); // Timeout of 1 second
                return true;
            } catch (IOException e) {
                return false;
            }
        });

        try {
            return future.get(); // Wait for the result of the asynchronous call
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // Checks if the username contains blocked terms
    public boolean checkUserName(String userName) {
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> { // Asynchronous call
            try (InputStream inputStream = getClass().getResourceAsStream("data/blockedTerms")) { // Load the blocked terms
                assert inputStream != null;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (userName.contains(line)) {
                            return true;
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            return false;
        });

        try {
            return future.get(); // Wait for the result of the asynchronous call
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // Centers the frame in the middle of the screen
    public Point centerFrame(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Screen size
        int x = ((screenSize.width - frame.getWidth()) / 2);
        int y = ((screenSize.height - frame.getHeight()) / 2);
        if (smallScreen) y = 0;
        return new Point(x, y);
    }

    // Calculates the window width
    public int xPlayerPosition(JPanel panel, int width) {
        int x = panel.getWidth() / 4 - width / 2;
        return Math.min(x, 200);
    }

    // Calculates the system latency
    @SuppressWarnings("UnusedReturnValue")
    public long calculateSystemLatency(GamePanel gamePanel) {
        long currentTime = System.currentTimeMillis(); // Current time
        long latency = currentTime - startTime; // Latency
        startTime = currentTime;
        soutLogger("latency-log.txt", String.valueOf(latency), gamePanel);
        return latency;
    }

    // Writes strings to a log file
    public void soutLogger(String file, String message, GamePanel gamePanel) {
        if (gamePanel.developerMode) {
            CompletableFuture.runAsync(() -> { // Asynchronous call
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                    writer.append(message);
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    // Reads a JSON file
    public JsonNode readJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream inputStream;
            if (json.endsWith("variantList.json")) inputStream = getClass().getResourceAsStream(json);
            else if (json.endsWith(".json")) {
                inputStream = Files.newInputStream(Paths.get(json));
                customConfig = true;
            } else inputStream = getClass().getResourceAsStream("config/" + json + ".json");

            if (inputStream == null) return null;
            return mapper.readTree(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Checks if it's September 11th
    public JsonNode checkDate(String Default) {
        JsonNode config;
        LocalDate date = LocalDate.now();
        if (date.getMonthValue() == 9 && date.getDayOfMonth() == 11) config = readJson("911");
        else config = readJson(Default);
        return config;
    }

    // Checks if the resolution is too large
    public int[] maxDimension(int x, int y) {
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        long height = Math.round(Toolkit.getDefaultToolkit().getScreenSize().height * 0.95);
        if (x > width) {
            x = width;
            smallScreen = true;
        }
        if (y > height) {
            y = (int) height;
            smallScreen = true;
        }
        return new int[]{x, y};
    }

    // Calculates the size of the bottom menu
    public Rectangle getBottomMenuBounds(JPanel panel) {
        int width = panel.getWidth();
        int height = panel.getHeight();
        int part = height / 5;
        return new Rectangle(0, part * 4, width, height - (part * 4));
    }

    // Calculates the average color in a rectangle
    public Color getAverageColorInRectangle(Rectangle rectangle, JPanel panel) {
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

    // Calculates the color of the text
    public Color calculateForegroundColor(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        if (r + g + b > 382) return Color.BLACK;
        else return Color.WHITE;
    }

    // Checks if a key is contained in ArrayList
    public boolean containsKey(ArrayList<Double> keyList, ArrayList<Double> eventList) {
        for (Double key : keyList) {
            if (!eventList.contains(key)) {
                return false; // If a key is not found in the eventList, return false
            }
        }
        return true; // If all keys are present in the eventList, return true
    }

    // Shutdown the computer
    public void shutdown(Config config) {
        try {
            String shutdownCommand;

            if (!config.isLinux()) shutdownCommand = "shutdown.exe -s -t 0";
            else shutdownCommand = "shutdown -h now";

            Runtime.getRuntime().exec(shutdownCommand);
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}