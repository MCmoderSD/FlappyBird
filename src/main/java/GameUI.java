import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;

import static java.lang.Thread.sleep;

public class GameUI extends JFrame {

    // Klassenattribute
    public final ArrayList<JLabel> obstacles = new ArrayList<>();
    public final ArrayList<Rectangle> rObstacles = new ArrayList<>(), greenZones = new ArrayList<>();
    public final JLabel player, score, gameOver, pauseScreen;
    public final Rectangle rPlayer;
    public final JPanel mainPanel;
    public final Thread quickTimer;
    private final Config config;
    private final Utils utils;
    private final Logic logic;


    public int points;
    public boolean TimerIsRunning = false;
    private boolean rainbowModeActive = false;
    private long startTime;

    // Konstruktor
    public GameUI(Config config) {

        this.config = config;
        this.utils = config.getUtils();

        logic = new Logic(config, this);


        if (config.getArgs().length > 0) if (config.getArgs()[0].toLowerCase().endsWith(".json")) Logic.cheatsEnabled = true;

        // Initialisiere das Fenster
        setTitle(config.getTitle());
        setSize(config.getWindowSizeX(), config.getWindowSizeY());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocation(utils.centerFrame(this));
        setResizable(config.isResizeable());
        setIconImage((utils.reader(config.getIcon())));
        setVisible(true);

        mainPanel = new JPanel();
        mainPanel.setSize(getWidth(), getHeight());
        mainPanel.setLayout(null);
        mainPanel.setOpaque(false);
        add(mainPanel);

        // Initialisiere den Spieler
        player = new JLabel();
        final ImageIcon playerIcon = utils.createImageIcon(config.getPlayer());
        player.setSize(playerIcon.getIconWidth(), playerIcon.getIconHeight());
        //player.setLocation(utils.xPlayerPosition(mainPanel), getHeight() / 2);
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
        ImageIcon pauseScreenIcon = utils.createImageIcon(config.getPause());
        pauseScreen.setSize(pauseScreenIcon.getIconWidth(), pauseScreenIcon.getIconHeight());
        pauseScreen.setLocation(utils.locatePoint(config.getPause(), getWidth(), getHeight()));
        pauseScreen.setIcon(pauseScreenIcon);
        pauseScreen.setVisible(false);
        mainPanel.add(pauseScreen);

        // Initialisiere den Timer
        quickTimer = new Thread(() -> {
            long delay = (long) (1000/config.getTPS());
            while (true) {
                try {
                    if (TimerIsRunning) {
                        if (startTime <= 0) startTime = System.currentTimeMillis();

                        // Timer Body
                        if (System.getProperty("os.name").equals("linux")) Toolkit.getDefaultToolkit().sync();
                        logic.handleTimerTick();
                        if (Logic.developerMode) System.out.println(utils.calculateSystemLatency());

                        // Timer Delay
                        if ((delay - (System.currentTimeMillis() - startTime)) > 0) sleep(delay - (System.currentTimeMillis() - startTime));
                        startTime = System.currentTimeMillis();
                    } else sleep(delay);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        quickTimer.start();

        // Initialisiere die Steuerung
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                // Steuerung
                if (e.getKeyCode() == KeyEvent.VK_SPACE) logic.handleSpaceKeyPress();
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) logic.handleGamePause();

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
        if (!Logic.developerMode) {
            if (player.getY() > getWidth()) logic.handleCollision();

            for (Rectangle component : rObstacles) {
                if (component != null) {
                    if (rPlayer.intersects(component) && !logic.rainbowMode) {
                        //utils.audioPlayer(config.getHitSound(), config.isSound(), false, logic);
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
        if (logic.rainbowMode && !rainbowModeActive) {
            player.setIcon(utils.createImageIcon((config.getRainbow())));
            rainbowModeActive = true;
            } else if (!logic.rainbowMode && rainbowModeActive){
                player.setIcon(utils.createImageIcon((config.getPlayer())));
                rainbowModeActive = false;
        }
    }
}