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
import java.time.LocalDate;
import java.util.Objects;
public class Utils {
    private int getWidthSave;
    private String getWidthPath, readerPath, createImageIconPath;
    private BufferedImage readerSave;
    private ImageIcon createImageIconSave;
    private long startTime = System.currentTimeMillis();
    private final double osMultiplier;

    public Utils(double osMultiplier) {
        this.osMultiplier = osMultiplier;
    }

    public int calculateGravity(int x) {
        return -2 * x + 4;
    }

    public BufferedImage reader(String resource) {
        if (!Objects.equals(readerPath, resource)) {
            try {
                if (resource.endsWith(".png")) {
                    readerPath = resource;
                    readerSave = ImageIO.read(Objects.requireNonNull(getClass().getResource(resource)));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return readerSave;
    }

    public ImageIcon createImageIcon(String resource) {
        if (!Objects.equals(createImageIconPath, resource)) {
            if (resource.endsWith(".png")) {
                createImageIconPath = resource;
                createImageIconSave = new ImageIcon(reader(resource));
            }
            if (resource.endsWith(".gif")) {
                URL imageUrl = getClass().getClassLoader().getResource(resource);
                createImageIconPath = resource;
                createImageIconSave = new ImageIcon(Objects.requireNonNull(imageUrl));
            }
        }
        return createImageIconSave;
    }

    public Point locatePoint(String image, int width, int height) {
        BufferedImage img = reader(image);
        return new Point((width -  img.getWidth()) / 2, (height - img.getHeight()) / 2);
    }

    public void audioPlayer(String audioFilePath, boolean sound) {
        if (sound && !Logic.instance.gamePaused) {
            try {
                ClassLoader classLoader = getClass().getClassLoader();
                InputStream audioFileInputStream = classLoader.getResourceAsStream(audioFilePath);

                // Überprüfen, ob die Audiodatei gefunden wurde
                if (audioFileInputStream == null) {
                    throw new IllegalArgumentException("Die Audiodatei wurde nicht gefunden: " + audioFilePath);
                }

                BufferedInputStream bufferedInputStream = new BufferedInputStream(audioFileInputStream);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);

                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);

                // Hinzufügen eines LineListeners, um die Ressourcen freizugeben, wenn die Wiedergabe beendet ist
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        try {
                            clip.close();
                            audioInputStream.close();
                            bufferedInputStream.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                clip.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getBackgroundWidth(String path) {
        if (!Objects.equals(path, getWidthPath)) {
            getWidthPath = path;
            getWidthSave = reader(path).getWidth();
        }
        return getWidthSave;
    }

    public void setPlaceholder(JTextField textField, String placeholder) {
        Font originalFont = textField.getFont();

        textField.setForeground(Color.GRAY);
        textField.setFont(originalFont);
        textField.setText(placeholder);

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
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

    public boolean checkSQLConnection(String ip, String port) {
        try (Socket socket = new Socket()) {
            // Verbindungsversuch zum Server
            socket.connect(new InetSocketAddress(ip, Integer.parseInt(port)), 1000); // Timeout von 1 Sekunde
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean checkUserName(String userName) {
        try (InputStream inputStream = getClass().getResourceAsStream("data/blockedTerms.txt")) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                int lineNumber = 1;
                while ((line = reader.readLine()) != null) {
                    if (userName.contains(line)) {
                        System.out.println("Match found in line: " + lineNumber);
                        return true;
                    }
                    lineNumber++;
                }
                System.out.println("No match found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Point centerFrame(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Point(((screenSize.width - frame.getWidth()) / 2), ((screenSize.height - frame.getHeight()) / 2));
    }

    public int xPlayerPosition(JPanel frame) {
        int x = frame.getWidth() / 4;
        return Math.min(x, 200);
    }

    public JsonNode checkDate(String Default) {
        JsonNode config;
        LocalDate date = LocalDate.now();
        if (date.getMonthValue() == 9 && date.getDayOfMonth() == 11 ) config = loadConfig("config/911.json");
        else config = loadConfig("config/" + Default + ".json");
        return config;
    }

    public JsonNode loadConfig(String path) {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(path)) {
            assert inputStream != null;
            return mapper.readTree(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public double calculateOSspecifcTickrate(double Tickrate) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) return Tickrate;
        if (os.contains("linux")) return (Tickrate * osMultiplier); // Windows Lag Compensation
        return Tickrate;
    }

    public long calculateSystemLatency() {
        long currentTime = System.currentTimeMillis();
        long latency = currentTime - startTime;
        startTime = currentTime;
        return latency;
    }
}
