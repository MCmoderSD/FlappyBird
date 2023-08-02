import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class GamePanel extends JPanel implements Runnable {
    private final JFrame frame;
    private final Config config;
    private final Utils utils;
    private final Player player;
    private final JLabel gameOverLabel, pauseLabel, pointsLabel;
    private final int[] KONAMI_CODE = { KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_B, KeyEvent.VK_A };
    private final ArrayList<Integer> userInput = new ArrayList<>();
    public ArrayList<Obstacle> obstacles = new ArrayList<>();
    public ArrayList<Rectangle> greenZones = new ArrayList<>();
    public boolean isRunning = false, gameOver = false, isPaused = false;
    public int points = 0;
    private int xPosition;
    private int movePlayerInt = 0;
    private int obstacleMoveInt = 200;
    private int backgroundResetX;
    private int backgroundMoveInt;

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
                        Logic.developerMode = !Logic.developerMode;
                        Logic.cheatsEnabled = true;
                        System.out.println("Developer-Modus umgeschaltet: " + Logic.developerMode);
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

        moveObstacles();

        if (obstacleMoveInt == 200) {
            generateObstacles();
            obstacleMoveInt = 0;
        } else obstacleMoveInt++;

        if (backgroundMoveInt >= 2) {
            backgroundResetX--;
            if (backgroundResetX <= - utils.getBackgroundWidth(config.getBackground())) backgroundResetX = 0;
            backgroundMoveInt = 0;
        }
        backgroundMoveInt++;

        checkCollision();
        removeObstacles();

        //System.out.println(utils.calculateSystemLatency());
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

        for (Rectangle component : greenZones) {
            g.setColor(Color.GREEN);
            g.fillRect(component.x, component.y, component.width, component.height);
        }

        for (Obstacle component : obstacles) {
            g.setColor(Color.RED);
            g.drawRect(component.getX(), component.getY(), component.getWidth(), component.getHeight());
        }

        g.drawImage(player.getImage(), player.getX(), player.getY(), this);

        g.setColor(Color.YELLOW);
        g.drawRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());

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

        if (player.getY() > getHeight()) {
            System.out.println("Fall out of the world");
            utils.audioPlayer(this, config.getDieSound(), config.isSound(), false);
            gameOver();
        }

        // ToDo Fix
        for (Obstacle component : obstacles) {
            if (player.getHitbox().intersects(component.getHitbox())) {
                utils.audioPlayer(this, config.getHitSound(), config.isSound(), false);
                System.out.println("Game Over");
                gameOver();
            }
        }

        // ToDo Fix
        for (Rectangle component : greenZones) {
            if (player.getHitbox().intersects(component)) {
                points++;
                utils.audioPlayer(this, config.getPointSound(), config.isSound(), false);
                System.out.println("Point");
                greenZones.remove(component);
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
            } else if (player.getY() >= -player.getHeight()) {
                xPosition = -config.getJumpHeight();
                utils.audioPlayer(this, config.getFlapSound(), config.isSound(), false);
            }
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
        isRunning = false;
        isPaused = false;
        pauseLabel.setVisible(false);
        gameOverLabel.setVisible(true);
    }
}
