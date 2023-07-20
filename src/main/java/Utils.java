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

// Klasse für alle Utensiien
@SuppressWarnings("BlockingMethodInNonBlockingContext")
public class Utils {
    private final double osMultiplier;
    private final HashMap<String, Clip> HeavyClipCache = new HashMap<>(); // Cache für AudioClips
    private final HashMap<String, BufferedImage> bufferedImageCache = new HashMap<>(); // Cache für BufferedImages
    private final HashMap<String, ImageIcon> imageIconCache = new HashMap<>(); // Cache für ImageIcons
    private final ArrayList<BufferedInputStream> HeavyBufferedInputStreamCache = new ArrayList<>(); // Cache für BufferedInputStreams
    private final ArrayList<AudioInputStream> HeavyAudioInputStreamCache = new ArrayList<>(); // Cache für AudioInputStreams
    private long startTime = System.currentTimeMillis();
    private boolean audioIsStopped, customConfig = false, smallScreen = false;

    // Konstruktor und Multiplikator für die Tickrate
    public Utils(double osMultiplier) { this.osMultiplier = osMultiplier; }

    // Berechnet die Flugbahn des Spielers
    public int calculateGravity(int x) { return -2 * x + 4; }

    // Läd Bilddateien
    public BufferedImage reader(String resource) {
        if (bufferedImageCache.containsKey(resource)) return bufferedImageCache.get(resource); // Überprüft, ob der Pfad bereits geladen wurde
        BufferedImage image = null;
        try {
            if (resource.endsWith(".png")) {
                if (customConfig & !resource.startsWith("error")) image = ImageIO.read(Files.newInputStream(Paths.get(resource)));
                else image = ImageIO.read(Objects.requireNonNull(getClass().getResource(resource)));
            } else throw new IllegalArgumentException("Das Bildformat wird nicht unterstützt: " + resource);

            bufferedImageCache.put(resource, image); // Fügt das Bild dem Cache hinzu

             } catch (IOException e) {
                 e.printStackTrace();
             }
        if (image == null) throw new IllegalArgumentException("Das Bild konnte nicht geladen werden: " + resource);
        return image;
    }

    // Erstellt ein ImageIcon aus Bildern
    public ImageIcon createImageIcon(String resource) {
        if (imageIconCache.containsKey(resource)) return imageIconCache.get(resource); // Überprüft, ob der Pfad bereits geladen wurde
        ImageIcon imageIcon;
            if (resource.endsWith(".png")) {
                imageIcon = new ImageIcon(reader(resource)); // Erstellt ein ImageIcon
            } else if (resource.endsWith(".Animations")) {
                URL imageUrl = getClass().getClassLoader().getResource(resource);
                imageIcon = new ImageIcon(Objects.requireNonNull(imageUrl));
            } else throw new IllegalArgumentException("Das Bildformat wird nicht unterstützt: " + resource);

        imageIconCache.put(resource, imageIcon); // Fügt das Bild dem Cache hinzu
        return imageIcon;
    }

    // Zentriert ein Bild mittig
    public Point locatePoint(String image, int width, int height) {
        BufferedImage img = reader(image);
        return new Point((width -  img.getWidth()) / 2, (height - img.getHeight()) / 2);
    }

    // Läd Musikdateien und spielt sie ab
    public void audioPlayer(String audioFilePath, boolean sound, boolean loop, Logic logic) {
        if (sound && !logic.gamePaused && !Objects.equals(audioFilePath, "error/empty.wav")) {
            if (!loop) audioIsStopped = false;
            CompletableFuture.runAsync(() -> {
                try {
                    if (HeavyClipCache.get(audioFilePath) != null) {
                        Clip clip = HeavyClipCache.get(audioFilePath);
                        clip.setFramePosition(0);
                        clip.start();
                        return;
                    }

                    ClassLoader classLoader = getClass().getClassLoader();
                    InputStream audioFileInputStream;

                    if (customConfig) audioFileInputStream = Files.newInputStream(Paths.get(audioFilePath));
                    else audioFileInputStream = classLoader.getResourceAsStream(audioFilePath);

                    // Überprüfen, ob die Audiodatei gefunden wurde
                    if (audioFileInputStream == null) throw new IllegalArgumentException("Die Audiodatei wurde nicht gefunden: " + audioFilePath);

                    BufferedInputStream bufferedInputStream = new BufferedInputStream(audioFileInputStream);
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);

                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);

                    // Lange Audiodateien werden in den Cache geladen, um die Ressourcen freizugeben
                    if (clip.getMicrosecondLength() > 1000000 || loop) {
                        HeavyBufferedInputStreamCache.add(bufferedInputStream);
                        HeavyAudioInputStreamCache.add(audioInputStream);
                        HeavyClipCache.put(audioFilePath, clip);
                    }

                    // Hinzufügen eines LineListeners, um die Ressourcen freizugeben, wenn die Wiedergabe beendet ist
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            try {
                                if (loop && logic.gameOver && !audioIsStopped) audioPlayer(audioFilePath, true, true ,logic);
                                if (!HeavyClipCache.containsKey(audioFilePath)) {
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
                    e.printStackTrace();
                }
            });
        }
    }

    // Stoppt die Musik
    public void stopHeavyAudio() {
        CompletableFuture.runAsync(() -> {
            try {
                audioIsStopped = true;
                for (Clip clip : HeavyClipCache.values()) clip.stop();
                for (AudioInputStream audioInputStream : HeavyAudioInputStreamCache) audioInputStream.close();
                for (BufferedInputStream bufferedInputStream : HeavyBufferedInputStreamCache) bufferedInputStream.close();

                HeavyBufferedInputStreamCache.clear();
                HeavyAudioInputStreamCache.clear();
                HeavyClipCache.clear();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Berechnet die Breite des Hintergrunds
    public int getBackgroundWidth(String path) {
        return bufferedImageCache.get(path).getWidth();
    }

    // Placeholder für Textfelder (den Username)
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

    // Überprüft die Internetverbindung zum SQL Server
    public boolean checkSQLConnection(String ip, String port) {
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            try (Socket socket = new Socket()) {
                // Verbindungsversuch zum Server
                socket.connect(new InetSocketAddress(ip, Integer.parseInt(port)), 1000); // Timeout von 1 Sekunde
                return true;
            } catch (IOException e) {
                return false;
            }
        });

        try {
            return future.get(); // Warten auf das Ergebnis des asynchronen Aufrufs
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // Überprüft, ob der Username blockierte Begriffe enthält
    public boolean checkUserName(String userName) {
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> { // Asynchroner Aufruf
            try (InputStream inputStream = getClass().getResourceAsStream("data/blockedTerms.txt")) { // Läd die blockierten Begriffe
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
                e.printStackTrace();
            }
            return false;
        });

        try {
            return future.get(); // Warten auf das Ergebnis des asynchronen Aufrufs
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // Zentriert das Fenster mittig auf dem Bildschirm
    public Point centerFrame(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Bildschirmgröße
        int x = ((screenSize.width - frame.getWidth()) / 2);
        int y = ((screenSize.height - frame.getHeight()) / 2);
        if (smallScreen) y = 0;
        return new Point(x, y);
    }

    // Berechent die Breite des Fensters
    public int xPlayerPosition(JPanel frame) {
        int x = frame.getWidth() / 4;
        return Math.min(x, 200);
    }

    // Berechnet die Tickrate je nach Betriebssystem
    public double calculateOSspecifcTickrate(double Tickrate) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) return Tickrate;
        if (os.contains("linux")) return (Tickrate * osMultiplier); // Windows Lag Compensation
        return Tickrate;
    }

    // Berechnet die Latenz des Systems
    public long calculateSystemLatency() {
        long currentTime = System.currentTimeMillis(); // Aktuelle Zeit
        long latency = currentTime - startTime; // Latenz
        startTime = currentTime;
        soutLogger("latency-log.txt", String.valueOf(latency));
        return latency;
    }

    // Schreibt Strings in eine Log-Datei
    public void soutLogger(String file, String message) {
        if (Logic.developerMode) {
            CompletableFuture.runAsync(() -> { // Asynchroner Aufruf
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                    writer.append(message);
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    // Liest eine JSON-Datei aus
    public JsonNode readJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream inputStream;
            if (json.endsWith(".json")) {
                inputStream = Files.newInputStream(Paths.get(json));
                customConfig = true;
            } else inputStream = getClass().getResourceAsStream("config/" + json + ".json");
            if (inputStream == null) return null;
            return mapper.readTree(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Überprüft ob der 11. September ist
    public JsonNode checkDate(String Default) {
        JsonNode config;
        LocalDate date = LocalDate.now();
        if (date.getMonthValue() == 9 && date.getDayOfMonth() == 11 ) config = readJson("911");
        else config = readJson(Default);
        return config;
    }

    // Checkt, ob die Auflösung zu groß ist
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

    // Berechnet die Größe des unteren Menüs
    public Rectangle getBottomMenuBounds(JPanel panel) {
        int width = panel.getWidth();
        int height = panel.getHeight();
        int part = height / 5;
        return new Rectangle(0, part * 4, width, height - (part * 4));
    }

    // Berechnet die durchschnittliche Farbe in einem Rechteck
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

    // Berechnet die Farbe des Textes
    public Color calculateForegroundColor(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        if (r + g + b > 382) return Color.BLACK;
        else return Color.WHITE;
    }
}