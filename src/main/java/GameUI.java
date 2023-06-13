import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class GameUI extends JFrame {
    public static GameUI instance;
    public final ArrayList<JLabel> obstacles = new ArrayList<>();
    public final ArrayList<Rectangle> rObstacles = new ArrayList<>();
    public final ArrayList<Rectangle> greenZones = new ArrayList<>();
    public final Timer tickrate;
    private final ArrayList<Integer> userInput = new ArrayList<>();
    private final int[] KONAMI_CODE = { KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN,
            KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
            KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_B,
            KeyEvent.VK_A };
    public JLabel player, score, gameOver, pauseScreen;
    public int points;
    public Rectangle rPlayer;
    public JPanel mainPanel;

    // Konstruktor
    public GameUI(int width, int height, String title, String icon, boolean resizable, int playerPosition, int playerWidth, int playerHeight, String backgroundImage, String playerImage, String rainbowImage, int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage, String gameOverImage, String pauseScreenImage, String dieSound, String flapSound, String hitSound, String pointSound, String rainbowSound, int Tickrate, boolean sound, String[] args) {
        initFrame(width, height, title, icon, resizable);
        initMainPanel(width, height, backgroundImage);
        initPlayer(height, playerPosition, playerWidth, playerHeight, playerImage);
        initScore(width, height);
        initGameOver(width, height, gameOverImage);
        initPauseScreen(width, height, pauseScreenImage);
        instance = this;
        tickrate = new Timer(Methods.instance.getTPS(Tickrate), e -> {
            if (System.getProperty("os.name").equals("linux")) Toolkit.getDefaultToolkit().sync();
            Logic.instance.handleTimerTick(width, height, playerImage, rainbowImage, percentage, verticalGap, obstacleWidth, obstacleHeight, obstacleTopImage, obstacleBottomImage, dieSound, hitSound, pointSound, rainbowSound,Tickrate, sound);
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_SPACE) Logic.instance.handleSpaceKeyPress(width, height, title, icon, resizable, backgroundImage, flapSound, Tickrate, sound, args);
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) Logic.instance.handleGamePause();
                userInput.add(e.getKeyCode());
                initDevolperMode();
            }
        });

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Logic.instance.handleSpaceKeyPress(width, height, title, icon, resizable, backgroundImage, flapSound, Tickrate, sound, args);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    // Initialisiert das JFrame-Fenster
    private void initFrame(int width, int height, String title, String icon, boolean resizable) {
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setVisible(true);
        setResizable(resizable);
        setIconImage((Methods.instance.reader(icon)));
        Dimension frameDimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((frameDimension.width - width) / 2, (frameDimension.height - height) / 2);
    }

    // Initialisiert das Hauptpanel mit Hintergrundbild
    private void initMainPanel(int width, int height, String backgroundImage) {
        final BufferedImage background = Methods.instance.reader(backgroundImage);
        final BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        int imageWidth = background.getWidth();
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;

                // Hintergründe zeichnen auf den Buffer
                Graphics2D bufferGraphics = buffer.createGraphics();
                bufferGraphics.setBackground(new Color(0, 0, 0, 0)); // Transparenter Hintergrund
                bufferGraphics.clearRect(0, 0, getWidth(), getHeight());

                int firstX = Movement.instance.backgroundResetX % imageWidth;

                if (firstX > 0) {
                    bufferGraphics.drawImage(background, firstX - imageWidth, 0, this);
                }

                for (int x = firstX; x < getWidth() + imageWidth; x += imageWidth) {
                    bufferGraphics.drawImage(background, x, 0, this);
                }

                // Buffer auf das Panel zeichnen
                g2d.drawImage(buffer, 0, 0, this);
            }
        };
        mainPanel.setSize(width, height);
        mainPanel.setLayout(null);
        mainPanel.setOpaque(false);
        add(mainPanel);
    }

    // Initialisiert den Spieler
    private void initPlayer(int height, int playerPosition, int playerWidth, int playerHeight, String playerImage) {
        player = new JLabel();
        mainPanel.add(player);
        player.setSize(playerWidth, playerHeight);
        player.setBounds(playerPosition, player.getY(), playerWidth, playerHeight);
        rPlayer = new Rectangle(player.getBounds());
        player.setIcon(Methods.instance.createImageIcon((playerImage)));
        player.setLocation(playerPosition, height / 2);
    }

    // Initialisiert den Punktestand
    private void initScore(int width, int height) {
        int y = height / 20, x = y * 3;
        score = new JLabel();
        mainPanel.add(score);
        score.setSize(x, y);
        score.setLocation(width - 10 - x, 10);
        score.setFont(new Font("Arial", Font.BOLD, 18));
        score.setForeground(Color.YELLOW);
        score.setText("Score: " + points);
    }

    // Initialisiert das Game Over-Label
    private void initGameOver(int width, int height, String gameOverImage) {
        gameOver = new JLabel();
        mainPanel.add(gameOver);
        gameOver.setVisible(false);
        gameOver.setSize(width, height);
        gameOver.setIcon(Methods.instance.createImageIcon((gameOverImage)));
        gameOver.setLocation(Methods.instance.locatePoint(gameOverImage, width, height));
    }

    // Initialisiert das Pause-Bildschirm-Label
    private void initPauseScreen(int width, int height, String pauseImage) {
        pauseScreen = new JLabel();
        mainPanel.add(pauseScreen);
        pauseScreen.setVisible(false);
        pauseScreen.setSize(width, height);
        pauseScreen.setLocation(0, 0);
        pauseScreen.setIcon(Methods.instance.createImageIcon((pauseImage)));
        gameOver.setLocation(Methods.instance.locatePoint(pauseImage, width, height));
    }

    private void initDevolperMode() {


        if (userInput.size() > KONAMI_CODE.length) {
            userInput.remove(0); // Die Eingabe begrenzen, um Speicherplatz zu sparen
        }

        if (userInput.size() == KONAMI_CODE.length) {
            boolean konamiCodeEntered = true;
            for (int i = 0; i < KONAMI_CODE.length; i++) {
                if (userInput.get(i) != KONAMI_CODE[i]) {
                    konamiCodeEntered = false;
                    break;
                }
            }

            if (konamiCodeEntered) {
                Logic.instance.developerMode = !Logic.instance.developerMode;
                System.out.println("Developer-Modus umgeschaltet: " + Logic.instance.developerMode);
                userInput.clear();
            }
        }
    }

    // Erzeugt Hindernisse basierend auf den übergebenen Parametern
    public void generateObstacles(int width, int height, int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage) {
        int minY = ((height * percentage) / 100);
        int maxY = height - ((height * percentage) / 100);

        JLabel obstacleTop = new JLabel(), obstacleBottom = new JLabel();
        mainPanel.add(obstacleTop);
        mainPanel.add(obstacleBottom);
        obstacleTop.setIcon(Methods.instance.createImageIcon((obstacleTopImage)));
        obstacleBottom.setIcon(Methods.instance.createImageIcon((obstacleBottomImage)));

        int yTop = (int) (Math.random() * (maxY - minY + 1) + minY) - obstacleHeight;
        int yBottom = yTop + verticalGap + obstacleHeight;

        obstacleTop.setSize(obstacleWidth, obstacleHeight);
        obstacleTop.setBounds(width, yTop, obstacleWidth, obstacleHeight);
        obstacleTop.setLocation(width, yTop);

        obstacleBottom.setSize(obstacleWidth, obstacleHeight);
        obstacleBottom.setBounds(width, yBottom, obstacleWidth, obstacleHeight);
        obstacleBottom.setLocation(width, yBottom);

        obstacles.add(obstacleTop);
        obstacles.add(obstacleBottom);

        Rectangle rObstacleTop = new Rectangle(obstacleTop.getBounds());
        Rectangle rObstacleBottom = new Rectangle(obstacleBottom.getBounds());

        rObstacleTop.setBounds(obstacleTop.getBounds());
        rObstacleBottom.setBounds(obstacleBottom.getBounds());

        rObstacles.add(rObstacleTop);
        rObstacles.add(rObstacleBottom);

        Rectangle rectangleBetweenObstacles = new Rectangle(
                obstacleTop.getX() + obstacleWidth,
                obstacleTop.getY() + obstacleHeight,
                obstacleWidth,
                yBottom - (yTop + obstacleHeight)
        );

        greenZones.add(rectangleBetweenObstacles);
    }

    // Entfernt Hindernisse, die außerhalb des Sichtfelds liegen
    public void removeObstacles() {
        Iterator<JLabel> iteratorObstacles = obstacles.iterator();
        while (iteratorObstacles.hasNext()) {
            JLabel component = iteratorObstacles.next();
            int x = component.getX();
            if (x < -64) {
                mainPanel.remove(component);
                iteratorObstacles.remove();
            }
        }

        Iterator<Rectangle> iteratorRectangles = rObstacles.iterator();
        while (iteratorRectangles.hasNext()) {
            Rectangle component = iteratorRectangles.next();
            int x = (int) component.getX();
            if (x < -64) {
                iteratorRectangles.remove();
            }
        }
    }

    // Überprüft Kollisionen mit dem Spieler und anderen Objekten
    public void checkCollision(int width, String dieSound, String hitSound, String pointSound, String rainbowSound, boolean sound) {
        if (!Logic.instance.developerMode) {
            if (player.getY() > width) Logic.instance.handleCollision(dieSound, sound);

            for (Rectangle component : rObstacles) {
                if (component != null) {
                    if (rPlayer.intersects(component) && !Logic.instance.rainbowMode) {
                        Methods.instance.audioPlayer(hitSound, sound);
                        Logic.instance.handleCollision(dieSound, sound);
                    }
                }
            }
        }

        for (int i = 0; i < greenZones.size(); i++) {
            Rectangle component = greenZones.get(i);
            if (component != null && rPlayer.intersects(component)) {
                Logic.instance.handlePoint(pointSound, rainbowSound, sound);
                greenZones.remove(i);
                i--;
            }
        }
    }

    public void checkRainbowMode(String playerImage, String rainbowImage) {
        if (Logic.instance.rainbowMode && !Logic.instance.rainbowModeActive) {
            player.setIcon(Methods.instance.createImageIcon((rainbowImage)));
            Logic.instance.rainbowModeActive = true;
            System.out.println("Rainbow Mode Active");
            } else if (!Logic.instance.rainbowMode && Logic.instance.rainbowModeActive){
                player.setIcon(Methods.instance.createImageIcon((playerImage)));
        }
    }
}