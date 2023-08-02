import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class GamePanel extends JPanel implements Runnable {
    // Cheat Control
    public static boolean developerMode = false, cheatsEnabled = false;

    // Attributes
    private final JFrame frame;
    private final Config config;
    private final Utils utils;
    private final Player player;
    private final JLabel gameOverLabel, pauseLabel, pointsLabel;
    private final int[] KONAMI_CODE = {KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_B, KeyEvent.VK_A};

    // Lists
    private final ArrayList<Integer> userInput = new ArrayList<>();
    private final ArrayList<Obstacle> obstacles = new ArrayList<>();
    private final ArrayList<Rectangle> greenZones = new ArrayList<>();

    // Variables
    public boolean gameOver = false, isPaused = true;
    private boolean rainbowMode = false, gameStarted = false, backgroundAudioIsPlaying = false;
    private int xPosition, movePlayerInt = 0, obstacleMoveInt = 200, backgroundResetX, backgroundMoveInt, fpsCount, points = 0;

    // Konstruktor
    public GamePanel(JFrame frame, Config config) {
        // Init Attributes
        this.frame = frame;
        this.config = config;
        this.utils = config.getUtils();

        xPosition = -config.getJumpHeight();

        // Init Panel
        setLayout(null);
        setBounds(0, 0, config.getWindowSizeX(), config.getWindowSizeY());
        setDoubleBuffered(true);
        setFocusable(true);

        // Init Player
        player = new Player(config);
        player.setLocation(utils.xPlayerPosition(this, player.getWidth()), config.getWindowSizeY() / 2 - player.getHeight() / 2);

        // Init Points Label
        pointsLabel = new JLabel();
        int y = getHeight() / 20, x = y * 3;
        pointsLabel.setSize(x, y);
        pointsLabel.setLocation(getWidth() - 10 - x, 10);
        pointsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        pointsLabel.setForeground(Color.YELLOW);
        pointsLabel.setText("Score: " + points);
        add(pointsLabel);

        // Init Game Over Label
        gameOverLabel = new JLabel();
        ImageIcon gameOverIcon = utils.createImageIcon(config.getPause());
        gameOverLabel.setSize(gameOverIcon.getIconWidth(), gameOverIcon.getIconHeight());
        gameOverLabel.setLocation(utils.locatePoint(config.getGameOver(), getWidth(), getHeight()));
        gameOverLabel.setIcon(utils.createImageIcon((config.getGameOver())));
        gameOverLabel.setVisible(false);
        add(gameOverLabel);

        // Init Pause Label
        pauseLabel = new JLabel();
        ImageIcon pauseScreenIcon = utils.createImageIcon(config.getPause());
        pauseLabel.setSize(pauseScreenIcon.getIconWidth(), pauseScreenIcon.getIconHeight());
        pauseLabel.setLocation(utils.locatePoint(config.getPause(), getWidth(), getHeight()));
        pauseLabel.setIcon(pauseScreenIcon);
        pauseLabel.setVisible(false);
        add(pauseLabel);


        // Init KeyListener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                // Steuerung
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    isPaused = gameStarted && isPaused;
                    gameStarted = true;
                    jump();
                }

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (gameOver && player.getY() > getHeight()) jump();
                    else pauseGame();
                }

                // Konami-Code
                userInput.add(e.getKeyCode());

                // Die Eingabe begrenzen, um Speicherplatz zu sparen
                if (userInput.size() > KONAMI_CODE.length)
                    userInput.remove(0); // Die Eingabe begrenzen, um Speicherplatz zu sparen

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

        // Init MouseListener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                isPaused = gameStarted && isPaused;
                gameStarted = true;
                jump();
            }
        });

        // Init Thread
        new Thread(this).start();
    }

    // Quick timer
    @Override
    public void run() {
        while (Main.isRunning) {
            // Timer
            double tickrate = 10000000, delta = 0;
            long current, now = System.nanoTime();

            // Game Loop
            while (!isPaused) {
                current = System.nanoTime();
                delta += (current - now) / tickrate;
                now = current;

                if (delta >= 1) {
                    update(); // Update the game
                    delta--;
                }
            }

            // Delay
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Graphics Engine
    public void paintComponent(Graphics graphics) {
        // Migrate to Graphics2D
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;

        // Draw Background
        int firstX = backgroundResetX % utils.reader(config.getBackground()).getWidth();

        if (firstX > 0) {
            g.drawImage(utils.reader(config.getBackground()), firstX - utils.reader(config.getBackground()).getWidth(), 0, this);
        }

        for (int x = firstX; x < getWidth() + utils.reader(config.getBackground()).getWidth(); x += utils.reader(config.getBackground()).getWidth()) {
            g.drawImage(utils.reader(config.getBackground()), x, 0, this);
        }


        // Draw Obstacles
        for (Obstacle component : obstacles) {
            g.drawImage(component.getImage(), component.getX(), component.getY(), this);
        }


        // Draw Player
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

    // Timer clock
    private void update() {

        // Move Player
        if (movePlayerInt == 3) {
            xPosition++;
            movePlayer();
            movePlayerInt = 0;
        } else movePlayerInt++;


        if (!gameOver) {


            moveObstacles();

            // Generate Obstacles
            if (obstacleMoveInt == 200) {
                generateObstacles();
                obstacleMoveInt = 0;
            } else obstacleMoveInt++;


            // Move Background
            if (backgroundMoveInt >= 2) {
                backgroundResetX--;
                if (backgroundResetX <= -utils.getBackgroundWidth(config.getBackground())) backgroundResetX = 0;
                backgroundMoveInt = 0;
            }
            backgroundMoveInt++;

            checkCollision();
        }

        removeObstacles();


        if (fpsCount == 100 / config.getFPS()) {
            repaint(); // Update the screen
            fpsCount = 0;
        } else fpsCount++;


        if (developerMode) utils.calculateSystemLatency();
    }

    // Generates new obstacles
    private void generateObstacles() {

        // Calculate the minimum and maximum Y value
        int minY = ((getHeight() * config.getPercentage()) / 100);
        int maxY = getHeight() - ((getHeight() * config.getPercentage()) / 100);

        // Create new obstacles
        Obstacle obstacleTop = new Obstacle(config, true);
        Obstacle obstacleBottom = new Obstacle(config, false);

        // Add obstacles to the list
        obstacles.add(obstacleTop);
        obstacles.add(obstacleBottom);

        // Calculate the Y value of the obstacles
        int yTop = (int) (Math.random() * (maxY - minY + 1) + minY) - obstacleTop.getHeight();
        int yBottom = yTop + config.getGap() + obstacleBottom.getHeight();

        // Set the location of the obstacles
        obstacleTop.setLocation(getWidth(), yTop);
        obstacleBottom.setLocation(getWidth(), yBottom);

        // Create the green zone
        Rectangle greenZone = new Rectangle(
                obstacleTop.getX(),
                obstacleTop.getY() + obstacleTop.getHeight(),
                Math.max(obstacleTop.getWidth(), obstacleBottom.getWidth()),
                config.getGap()
        );

        // Add the green zone to the list
        greenZones.add(greenZone);
    }

    // Component cleanup
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

    // Checks the collision
    private void checkCollision() {

        player.updateLocation();

        Iterator<Rectangle> iterator = greenZones.iterator();
        while (iterator.hasNext()) {
            Rectangle component = iterator.next();
            if (player.getHitbox().intersects(component)) {
                points++;
                initRainbowMode();
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

    // Moves player
    private void movePlayer() {
        xPosition += 1;

        // Main mode
        if (config.getArgs().length < 2)
            player.setLocation(player.getX(), player.getY() - utils.calculateGravity(xPosition));
        else if (config.getArgs().length > 1) { // Reverse mode
            int lastY = 0;

            // Calculate the lowest Y value
            for (Obstacle component : obstacles) {
                if (lastY <= component.getY()) lastY = component.getY();
            }

            int vertical = utils.calculateGravity(xPosition);

            if (lastY > 1000 && vertical <= 0) vertical = 0;

            // Move Obstacles
            for (Obstacle component : obstacles) {
                component.setLocation(component.getX(), component.getY() - vertical);
            }

            // Move green zones
            for (Rectangle rectangle : greenZones) {
                rectangle.setLocation(rectangle.x, rectangle.y - vertical);
            }
        }
    }

    // Moves obstacles
    private void moveObstacles() {

        // Move obstacles
        for (Obstacle component : obstacles) {
            component.setLocation(component.getX() - 1, component.getY());
        }

        // Move green zones
        for (Rectangle rectangle : greenZones) {
            rectangle.setLocation(rectangle.x - 1, rectangle.y);
        }
    }

    // Handles Jump
    private void jump() {

        // Press to restart
        if (gameOver && player.getY() > getHeight()) {
            config.setPoints(points);
            new UI(config, utils);
            frame.dispose();
            return;
        }

        if (!gameOver && !isPaused && player.getY() >= -player.getHeight()) {

            // Play background audio
            if (!backgroundAudioIsPlaying) {
                utils.audioPlayer(this, config.getMusic(), config.isSound(), true);
                backgroundAudioIsPlaying = true;
            }

            // Jump
            utils.audioPlayer(this, config.getFlapSound(), config.isSound(), false);
            xPosition = -config.getJumpHeight();
        }
    }

    // Handles game pause
    private void pauseGame() {
        isPaused = !gameOver && !isPaused;
        pauseLabel.setVisible(isPaused);
    }

    // Handles game over
    private void gameOver() {
        utils.stopHeavyAudio();
        gameOver = true;
        isPaused = false;
        pauseLabel.setVisible(false);
        gameOverLabel.setVisible(true);
        utils.audioPlayer(this, config.getDieSound(), config.isSound(), false);
    }

    // Handles rainbow mode
    private void initRainbowMode() {
        if (!rainbowMode && points > 0 && points % 5 == 0 && (int) (Math.random() * 6 + 1) == 3) {
            new Thread(() -> {
                utils.audioPlayer(this, config.getRainbowSound(), config.isSound(), false);
                rainbowMode = true;
                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
                rainbowMode = false;
            }).start();
        }
    }
}