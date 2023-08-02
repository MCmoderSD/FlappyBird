import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class GamePanel extends JPanel implements Runnable {
    public static boolean developerMode = false, cheatsEnabled = false;
    private final JFrame frame;
    private final Config config;
    private final Utils utils;
    private final Player player;
    private final JLabel gameOverLabel, pauseLabel, pointsLabel;
    private final int[] KONAMI_CODE = { KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_B, KeyEvent.VK_A };
    private final ArrayList<Integer> userInput = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(Main.class);
    public ArrayList<Obstacle> obstacles = new ArrayList<>();
    public ArrayList<Rectangle> greenZones = new ArrayList<>();
    public int points = 0;
    public boolean gameOver = false, isPaused = false;
    private boolean isRunning = false, rainbowMode = false;
    private int xPosition, movePlayerInt = 0, obstacleMoveInt = 200, backgroundResetX, backgroundMoveInt;

    public GamePanel (JFrame frame, Config config) {
        this.frame = frame;
        this.config = config;
        this.utils = config.getUtils();

        xPosition = - config.getJumpHeight();

        setLayout(null);
        setBounds(0, 0, config.getWindowSizeX(), config.getWindowSizeY());
        setDoubleBuffered(true);

        player = new Player(config);
        player.setLocation(utils.xPlayerPosition(this, player.getWidth()), config.getWindowSizeY() / 2 - player.getHeight() / 2);

        pointsLabel = new JLabel();
        int y = getHeight() / 20, x = y * 3;
        pointsLabel.setSize(x, y);
        pointsLabel.setLocation(getWidth() - 10 - x, 10);
        pointsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        pointsLabel.setForeground(Color.YELLOW);
        pointsLabel.setText("Score: " + points);
        add(pointsLabel);

        gameOverLabel = new JLabel();
        ImageIcon gameOverIcon = utils.createImageIcon(config.getPause());
        gameOverLabel.setSize(gameOverIcon.getIconWidth(), gameOverIcon.getIconHeight());
        gameOverLabel.setLocation(utils.locatePoint(config.getGameOver(), getWidth(), getHeight()));
        gameOverLabel.setIcon(utils.createImageIcon((config.getGameOver())));
        gameOverLabel.setVisible(false);
        add(gameOverLabel);

        pauseLabel = new JLabel();
        ImageIcon pauseScreenIcon = utils.createImageIcon(config.getPause());
        pauseLabel.setSize(pauseScreenIcon.getIconWidth(), pauseScreenIcon.getIconHeight());
        pauseLabel.setLocation(utils.locatePoint(config.getPause(), getWidth(), getHeight()));
        pauseLabel.setIcon(pauseScreenIcon);
        pauseLabel.setVisible(false);
        add(pauseLabel);




        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                // Steuerung
                if (e.getKeyCode() == KeyEvent.VK_SPACE) jump();

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) pauseGame();

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
                        developerMode = !developerMode;
                        cheatsEnabled = true;
                        System.out.println("Developer-Modus umgeschaltet: " + developerMode);
                        userInput.clear();
                    }
                }

            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.out.println("Jump");
                jump();
            }
        });

        setFocusable(true);

        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        // ToDo Fix this
        do {
            double tickrate = 1000000000 / config.getTPS();
            double delta = 0;
            long now = System.nanoTime();
            long current;


            while (isRunning) {
                current = System.nanoTime();
                delta += (current - now) / tickrate;
                now = current;

                if (delta >= 1) {
                    update();
                    repaint();
                    delta--;
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } while (true); //while (!isRunning);
    }

    public void update() {
        if (movePlayerInt == 3) {
            xPosition++;
            movePlayer();
            movePlayerInt = 0;
        } else movePlayerInt++;

        if (!gameOver) {
            moveObstacles();

            if (obstacleMoveInt == 200) {
                generateObstacles();
                obstacleMoveInt = 0;
            } else obstacleMoveInt++;

            if (backgroundMoveInt >= 2) {
                backgroundResetX--;
                if (backgroundResetX <= -utils.getBackgroundWidth(config.getBackground())) backgroundResetX = 0;
                backgroundMoveInt = 0;
            }
            backgroundMoveInt++;

            initRainbowMode();
            checkCollision();
        }

        removeObstacles();

        if (developerMode) System.out.println(utils.calculateSystemLatency());
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;

        int firstX = backgroundResetX % utils.reader(config.getBackground()).getWidth();

        if (firstX > 0) {
            g.drawImage(utils.reader(config.getBackground()), firstX - utils.reader(config.getBackground()).getWidth(), 0, this);
        }

        for (int x = firstX; x < getWidth() + utils.reader(config.getBackground()).getWidth(); x += utils.reader(config.getBackground()).getWidth()) {
            g.drawImage(utils.reader(config.getBackground()), x, 0, this);
        }

        for (Obstacle component : obstacles) {
            g.drawImage(component.getImage(), component.getX(), component.getY(), this);
        }

        if (!rainbowMode) g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        else g.drawImage(player.getRainbow().getImage(), player.getX(), player.getY(), this);



        // Debug Hitbox
        if (developerMode) {
            for (Rectangle component : greenZones) {
                g.setColor(Color.GREEN);
                g.fillRect(component.x, component.y, component.width, component.height);
            }

            for (Obstacle component : obstacles) {
                g.setColor(Color.RED);
                g.drawRect(component.getX(), component.getY(), component.getWidth(), component.getHeight());
            }

            g.setColor(Color.YELLOW);
            g.drawRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        }
    }

    private void generateObstacles() {
        int minY = ((getHeight() * config.getPercentage()) / 100);
        int maxY = getHeight() - ((getHeight() * config.getPercentage()) / 100);

        Obstacle obstacleTop = new Obstacle(config, true);
        Obstacle obstacleBottom = new Obstacle(config, false);

        obstacles.add(obstacleTop);
        obstacles.add(obstacleBottom);

        int yTop = (int) (Math.random() * (maxY - minY + 1) + minY) - obstacleTop.getHeight();
        int yBottom = yTop + config.getGap() + obstacleBottom.getHeight();

        obstacleTop.setLocation(getWidth(), yTop);
        obstacleBottom.setLocation(getWidth(), yBottom);

        Rectangle greenZone = new Rectangle(
                obstacleTop.getX(),
                obstacleTop.getY() + obstacleTop.getHeight(),
                Math.max(obstacleTop.getWidth(), obstacleBottom.getWidth()),
                config.getGap()
        );

        greenZones.add(greenZone);
    }

    private void removeObstacles() {
    // Remove obstacles
    Iterator<Obstacle> obstacleIterator = obstacles.iterator();
    while (obstacleIterator.hasNext()) {
        Obstacle component = obstacleIterator.next();
        int x = component.getX();
        if (x < -64) {
            obstacleIterator.remove();
        }
    }

    // Remove green zones
    Iterator<Rectangle> greenZoneIterator = greenZones.iterator();
    while (greenZoneIterator.hasNext()) {
        Rectangle component = greenZoneIterator.next();
        int x = (int) component.getX();
        if (x < -64) {
            greenZoneIterator.remove();
        }
    }
    }

    private void checkCollision() {

        player.updateLocation();

        Iterator<Rectangle> iterator = greenZones.iterator();
        while (iterator.hasNext()) {
            Rectangle component = iterator.next();
            if (player.getHitbox().intersects(component)) {
                points++;
                pointsLabel.setText("Score: " + points);
                utils.audioPlayer(this, config.getPointSound(), config.isSound(), false);
                iterator.remove();
            }
        }

        if (developerMode) return;

        if (player.getY() > getHeight()) {
            utils.audioPlayer(this, config.getDieSound(), config.isSound(), false);
            gameOver();
        }

        for (Obstacle component : obstacles) {
            component.updateLocation();
            if (!rainbowMode && player.getHitbox().intersects(component.getHitbox())) {
                utils.audioPlayer(this, config.getHitSound(), config.isSound(), false);
                gameOver();
            }
        }
    }

    public void movePlayer() {
        xPosition += 1;

        if (config.getArgs().length < 2) player.setLocation(player.getX(), player.getY() - utils.calculateGravity(xPosition));
        else if (config.getArgs().length > 1) {
            int lastY = 0;

            for (Obstacle component : obstacles) {
                if (lastY <= component.getY()) lastY = component.getY();
            }

            int vertical = utils.calculateGravity(xPosition);

            if (lastY > 1000 && vertical <= 0) vertical = 0;

            for (Obstacle component : obstacles) {
                component.setLocation(component.getX(), component.getY() - vertical);
            }

            for (Rectangle rectangle : greenZones) {
                rectangle.setLocation(rectangle.x, rectangle.y - vertical);
            }
        }
    }

    public void moveObstacles() {
        for (Obstacle component : obstacles) {
            component.setLocation(component.getX() - 1, component.getY());
        }

        for (Rectangle rectangle : greenZones) {
            rectangle.setLocation(rectangle.x - 1, rectangle.y);
        }
    }
    private void jump() {
        if (!gameOver) {
            if (!isRunning) {
                if (!isPaused) isRunning = true;
            } else if (player.getY() >= -player.getHeight()) xPosition = -config.getJumpHeight();

            utils.audioPlayer(this, config.getFlapSound(), config.isSound(), false);
        } else if (gameOver) {
            config.setPoints(points);
            new UI(config, utils);
            frame.dispose();
        }
    }

    private void pauseGame() {
        if (isRunning && !isPaused) {
            isRunning = false;
            isPaused = true;
            pauseLabel.setVisible(true);
        } else if (!isRunning && isPaused) {
            isRunning = true;
            isPaused = false;
            pauseLabel.setVisible(false);
        }
    }

    private void gameOver() {
        gameOver = true;
        isPaused = false;
        pauseLabel.setVisible(false);
        gameOverLabel.setVisible(true);
        utils.audioPlayer(this, config.getDieSound(), config.isSound(), false);utils.audioPlayer(this, config.getRainbowSound(), config.isSound(), false);
    }

    private void initRainbowMode() {
        if (points > 0 && points % 2 == 0) { //if (points > 0 && points % 5 == 0 && (int) (Math.random() * 6 + 1) == 3) {
            new Thread(() -> {
                utils.audioPlayer(this, config.getRainbowSound(), config.isSound(), false);
                rainbowMode = true;
                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }

                rainbowMode = false;
            }).start();
        }
    }
}