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

    // Objekte
    public static GameUI instance;
    // Klassenattribute
    public final ArrayList<JLabel> obstacles = new ArrayList<>();
    public final ArrayList<Rectangle> rObstacles = new ArrayList<>();
    public final ArrayList<Rectangle> greenZones = new ArrayList<>();
    public final Timer tickrate;
    public final JLabel player, score, gameOver, pauseScreen;
    public final Rectangle rPlayer;
    public final JPanel mainPanel;
    private final Config config;
    private final Utils utils;
    private final Movement movement;
    private final Logic logic;
    private final ArrayList<Integer> userInput = new ArrayList<>();
    private final int[] KONAMI_CODE = { KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_DOWN,
            KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_B, KeyEvent.VK_A };
    public int points;

    // Konstruktor
    public GameUI(Config config) {
        instance = this;

        this.config = config;
        this.utils = config.getUtils();
        this.movement = config.getMovement();

        logic = new Logic(config, this);


        if (config.getArgs().length > 0) if (config.getArgs()[0].toLowerCase().endsWith(".json")) logic.cheatsEnabled = true;

        movement.init();

        // Initialisiere das Fenster
        setTitle(config.getTitle());
        setSize(config.getWindowSizeX(), config.getWindowSizeY());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocation(utils.centerFrame(this));
        setResizable(config.isResizeable());
        setIconImage((utils.reader(config.getIcon())));
        setVisible(true);

        // Initialisiere das Config-Panel mit Hintergrund
        final BufferedImage background = utils.reader(config.getBackground());
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

                int firstX = movement.backgroundResetX % imageWidth;

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

        mainPanel.setSize(getWidth(), getHeight());
        mainPanel.setLayout(null);
        mainPanel.setOpaque(false);
        add(mainPanel);

        // Initialisiere den Spieler
        player = new JLabel();
        final ImageIcon playerIcon = utils.createImageIcon(config.getPlayer());
        player.setSize(playerIcon.getIconWidth(), playerIcon.getIconHeight());
        player.setLocation(utils.xPlayerPosition(mainPanel), getHeight() / 2);
        player.setIcon(playerIcon);
        player.setBounds(player.getX(), player.getY(), playerIcon.getIconWidth(), playerIcon.getIconHeight());
        rPlayer = new Rectangle(player.getBounds());
        mainPanel.add(player);

        // Initialisiere die Punkteanzeige
        score = new JLabel();
        int y = getHeight() / 20, x = y * 3;
        score.setSize(x, y);
        score.setLocation(getWidth() - 10 - x, 10);
        score.setFont(new Font("Arial", Font.BOLD, 18));
        score.setForeground(Color.YELLOW);
        score.setText("Score: " + points);
        mainPanel.add(score);

        // Initialisiere das Game Over Bild
        gameOver = new JLabel();
        gameOver.setSize(getWidth(), getHeight());
        gameOver.setLocation(utils.locatePoint(config.getGameOver(), getWidth(), getHeight()));
        gameOver.setIcon(utils.createImageIcon((config.getGameOver())));
        gameOver.setVisible(false);
        mainPanel.add(gameOver);

        // Initialisiere das Pause-Bild
        pauseScreen = new JLabel();
        pauseScreen.setVisible(false);
        ImageIcon pauseScreenIcon = utils.createImageIcon(config.getPause());
        pauseScreen.setSize(pauseScreenIcon.getIconWidth(), pauseScreenIcon.getIconHeight());
        pauseScreen.setLocation(utils.locatePoint(config.getPause(), getWidth(), getHeight()));
        pauseScreen.setIcon(pauseScreenIcon);
        mainPanel.add(pauseScreen);

        // Initialisiere den Timer
        tickrate = new Timer((int) Math.round(1000/config.getTPS()), e -> {
            if (System.getProperty("os.name").equals("linux")) Toolkit.getDefaultToolkit().sync();
            logic.handleTimerTick();
            if (logic.developerMode) System.out.println(utils.calculateSystemLatency());
        });

        // Initialisiere die Steuerung
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                // Steuerung
                if (e.getKeyCode() == KeyEvent.VK_SPACE) logic.handleSpaceKeyPress();
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) logic.handleGamePause();


                // Konami-Code
                userInput.add(e.getKeyCode());

                // Die Eingabe begrenzen, um Speicherplatz zu sparen
                if (userInput.size() > KONAMI_CODE.length) userInput.remove(0); // Die Eingabe begrenzen, um Speicherplatz zu sparen

                // Prüfen, ob der Konami-Code eingegeben wurde
                if (userInput.size() == KONAMI_CODE.length) {
                    boolean konamiCodeEntered = true;
                    for (int i = 0; i < KONAMI_CODE.length; i++) {
                        if (userInput.get(i) != KONAMI_CODE[i]) {
                            konamiCodeEntered = false;
                            break;
                        }
                    }

                    // Wenn der Konami-Code eingegeben wurde, den Entwickler-Modus umschalten
                    if (konamiCodeEntered) {
                        logic.developerMode = !logic.developerMode;
                        logic.cheatsEnabled = true;
                        System.out.println("Developer-Modus umgeschaltet: " + logic.developerMode);
                        userInput.clear();
                    }
                }
            }
        });

        // Initialisiere die Maussteuerung
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                logic.handleSpaceKeyPress();
            }

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

    }

    // Erzeugt Hindernisse basierend auf den übergebenen Parametern
    public void generateObstacles() {

        int minY = ((getHeight() * config.getPercentage()) / 100);
        int maxY = getHeight() - ((getHeight() * config.getPercentage()) / 100);

        JLabel obstacleTop = new JLabel(), obstacleBottom = new JLabel();
        mainPanel.add(obstacleTop);
        mainPanel.add(obstacleBottom);

        ImageIcon obstacleTopIcon = utils.createImageIcon((config.getObstacleTop()));
        ImageIcon obstacleBottomIcon = utils.createImageIcon((config.getObstacleBottom()));

        obstacleTop.setIcon(obstacleTopIcon);
        obstacleBottom.setIcon(obstacleBottomIcon);

        int obstacleWidth = obstacleTopIcon.getIconWidth(), obstacleHeight = obstacleTopIcon.getIconHeight();
        int yTop = (int) (Math.random() * (maxY - minY + 1) + minY) - obstacleHeight;
        int yBottom = yTop + config.getGap() + obstacleHeight;

        obstacleTop.setSize(obstacleWidth, obstacleHeight);
        obstacleTop.setBounds(getWidth(), yTop, obstacleWidth, obstacleHeight);
        obstacleTop.setLocation(getWidth(), yTop);

        obstacleBottom.setSize(obstacleWidth, obstacleHeight);
        obstacleBottom.setBounds(getWidth(), yBottom, obstacleWidth, obstacleHeight);
        obstacleBottom.setLocation(getWidth(), yBottom);

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
    public void checkCollision() {
        if (!logic.developerMode) {
            if (player.getY() > getWidth()) logic.handleCollision();

            for (Rectangle component : rObstacles) {
                if (component != null) {
                    if (rPlayer.intersects(component) && !logic.rainbowMode) {
                        utils.audioPlayer(config.getHitSound(), config.isSound(), false);
                        logic.handleCollision();
                    }
                }
            }
        }

        for (int i = 0; i < greenZones.size(); i++) {
            Rectangle component = greenZones.get(i);
            if (component != null && rPlayer.intersects(component)) {
                logic.handlePoint();
                greenZones.remove(i);
                i--;
            }
        }
    }

    // Überprüft, ob der Spieler sich im Regenbogen-Modus befindet
    public void checkRainbowMode() {
        if (logic.rainbowMode && !logic.rainbowModeActive) {
            player.setIcon(utils.createImageIcon((config.getRainbow())));
            logic.rainbowModeActive = true;
            } else if (!logic.rainbowMode && logic.rainbowModeActive){
                player.setIcon(utils.createImageIcon((config.getPlayer())));
                logic.rainbowModeActive = false;
        }
    }
}