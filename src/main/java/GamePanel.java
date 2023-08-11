import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import static java.lang.Thread.sleep;

public class GamePanel extends JPanel implements Runnable {

    // Attributes
    private final JFrame frame;
    private final Config config;
    private final Utils utils;
    private final Player player;
    private final JLabel gameOverLabel, pauseLabel, pointsLabel, fpsLabel;
    private final int[] KONAMI_CODE = {KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_B, KeyEvent.VK_A};
    private final int FPS;

    // Lists
    private final ArrayList<Integer> userInput = new ArrayList<>();
    private final ArrayList<Double> keys = new ArrayList<>(), events = new ArrayList<>();
    private final ArrayList<Obstacle> obstacles = new ArrayList<>();
    private final ArrayList<Rectangle> greenZones = new ArrayList<>();
    // Variables
    public boolean gameOver = false, isPaused = true, developerMode = false;
    private double obstacleMoveSpeed = 1.01, obstacleGenerateSpeed = 1.01, backgroundMoveInt = 0, obstacleMoveInt = 0, obstacleGerateInt = 200;
    private boolean rainbowMode = false, gameStarted = false, backgroundAudioIsPlaying = false, hitbox = false, showFPS = false, cheatsEnabled = false, f3Pressed = false;
    private int xPosition, backgroundResetX, movePlayerInt = 0, fpsCount, points = 0, currentFPS;

    // Constructor
    public GamePanel(JFrame frame, Config config) {
        // Init Attributes
        this.frame = frame;
        this.config = config;
        this.utils = config.getUtils();

        xPosition = -config.getJumpHeight();

        FPS = Math.toIntExact(Math.round(360 / config.getFPS()));

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

        // Init FPS Label
        fpsLabel = new JLabel();
        fpsLabel.setSize(x, y);
        fpsLabel.setLocation(10, 10);
        fpsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        fpsLabel.setForeground(Color.GREEN);
        fpsLabel.setText("FPS: " + currentFPS);
        fpsLabel.setVisible(showFPS);
        add(fpsLabel);

        // Init KeyListener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                // Controls
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    isPaused = gameStarted && isPaused;
                    gameStarted = true;
                    jump();
                }

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (gameOver && player.getY() > getHeight()) jump();
                    else {
                        isPaused = !gameOver && !isPaused;
                        pauseLabel.setVisible(isPaused);
                    }
                }

                // Konami Code
                userInput.add(e.getKeyCode());

                // Limit the input to save memory
                if (userInput.size() > KONAMI_CODE.length)
                    userInput.remove(0);

                // Check if the Konami Code has been entered
                if (userInput.size() == KONAMI_CODE.length) {
                    boolean konamiCodeEntered = true;
                    for (int i = 0; i < KONAMI_CODE.length; i++) {
                        if (userInput.get(i) != KONAMI_CODE[i]) {
                            konamiCodeEntered = false;
                            break;
                        }
                    }

                    // If the Konami Code has been entered, toggle the developer mode
                    if (konamiCodeEntered) {
                        developerMode = !developerMode;
                        cheatsEnabled = true;
                        System.out.println("Developer Mode toggled: " + developerMode);
                        userInput.clear();
                    }
                }

                // F3 + ? key combination
                if (e.getKeyCode() == KeyEvent.VK_F3) f3Pressed = true;
                else if (f3Pressed && e.getKeyCode() == KeyEvent.VK_B) {

                    // F3 + B key combination
                    hitbox = !hitbox; // toggle hitbox
                    System.out.println("Hitbox: " + hitbox);
                    f3Pressed = false; // reset F3 status
                    repaint();

                    // F3 + F key combination
                } else if (f3Pressed && e.getKeyCode() == KeyEvent.VK_F) {
                    showFPS = !showFPS; // toggle FPS
                    fpsLabel.setVisible(showFPS);
                    System.out.println("Show FPS: " + showFPS);
                    f3Pressed = false; // reset F3 status
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                // Reset the F3 status when the key is released
                if (e.getKeyCode() == KeyEvent.VK_F3) f3Pressed = false;
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
            double tickrate = 2777778, delta = 0;
            long current, now = System.nanoTime(), timer = 0;
            int frames = 0;
            // Game Loop
            while (!isPaused) {
                current = System.nanoTime();
                delta += (current - now) / tickrate;
                timer += current - now;
                now = current;

                //Game Loop Start
                if (delta >= 1) {

                    // Move Player
                    if (movePlayerInt == 12) {
                        xPosition += 2;

                        // Main mode
                        if (config.getArgs().length < 2)
                            player.setLocation(player.getX(), player.getY() - utils.calculateGravity(xPosition));
                        else if (config.getArgs().length > 1) { // Reverse mode
                            int minY = 0, maxY = 0;
                            Obstacle obstacle = new Obstacle(config, true);
                            // Calculate the lowest Y value
                            for (Obstacle component : obstacles) {
                                if (minY >= component.getY()) minY = component.getY();
                                if (maxY <= component.getY()) maxY = component.getY();
                            }

                            int vertical = utils.calculateGravity(xPosition);

                            boolean minReached = vertical >= 0 && minY < -obstacle.getHeight() - config.getGap();
                            boolean maxReached = vertical <= 0 && maxY > obstacle.getHeight();
                            if (minReached || maxReached) vertical = 0;


                            // Move obstacles
                            for (Obstacle component : obstacles) {
                                component.setLocation(component.getX(), component.getY() - vertical);
                            }

                            // Move green zones
                            for (Rectangle rectangle : greenZones) {
                                rectangle.setLocation(rectangle.x, rectangle.y - vertical);
                            }
                        }

                        // Reset MovePlayerInt
                        movePlayerInt = 0;
                    } else movePlayerInt++;


                    if (!gameOver) {


                        // Obstacle movement
                        if (obstacleMoveInt >= 2 * obstacleMoveSpeed) {
                            // Move obstacles
                            for (Obstacle component : obstacles) {
                                component.setLocation(component.getX() - 1, component.getY());
                            }

                            // Move green zones
                            for (Rectangle rectangle : greenZones) {
                                rectangle.setLocation(rectangle.x - 1, rectangle.y);
                            }


                            // Reset obstacleMoveInt
                            obstacleMoveInt = 0;
                        } else obstacleMoveInt++;


                        // Generate Obstacles
                        if (obstacleGerateInt >= (720 * obstacleMoveSpeed) / (obstacleGenerateSpeed * obstacleGenerateSpeed)) {

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


                            // Reset obstacleGerateInt
                            obstacleGerateInt = 0;
                        } else obstacleGerateInt++;


                        // Move Background
                        if (backgroundMoveInt >= 3 * 2 * obstacleGenerateSpeed) {
                            backgroundResetX--;
                            if (backgroundResetX <= -utils.getBackgroundWidth(config.getBackground()))
                                backgroundResetX = 0;
                            backgroundMoveInt = 0;
                        } else backgroundMoveInt++;

                        double key = Math.random();
                        events.add(key);


                        // Check Collision
                        Iterator<Rectangle> iterator = greenZones.iterator();
                        while (iterator.hasNext()) {
                            Rectangle component = iterator.next();
                            if (player.getHitbox().intersects(component)) {
                                points++;
                                keys.add(key);

                                // Rainbow mode
                                if (!rainbowMode && points > 0 && points % 5 == 0 && (int) (Math.random() * 6 + 1) == 3) {
                                    new Thread(() -> {
                                        utils.audioPlayer(this, config.getRainbowSound(), config.isSound(), false);
                                        rainbowMode = true;
                                        try {
                                            sleep(7000);
                                        } catch (InterruptedException e) {
                                            System.err.println(e.getMessage());
                                        }
                                        rainbowMode = false;
                                    }).start();
                                }

                                pointsLabel.setText("Score: " + points);
                                utils.audioPlayer(this, config.getPointSound(), config.isSound(), false);
                                obstacleMoveSpeed += 0.0001;
                                obstacleGenerateSpeed += 0.0001;
                                iterator.remove();
                            }
                        }

                        if (!developerMode) {
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
                    }


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


                    // Frame rate
                    if (fpsCount == FPS) {
                        repaint(); // Update the screen
                        fpsCount = 0;
                    } else fpsCount++;


                    if (showFPS) fpsLabel.setText("FPS: " + currentFPS);

                    if (developerMode) utils.calculateSystemLatency(this);

                    // Game Loop End
                    delta--;
                    frames++;
                }

                if (timer >= 1000000000) {
                    if (developerMode || showFPS) currentFPS = frames / FPS;
                    frames = 0;
                    timer = 0;
                }
            }

            // Delay
            try {
                sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Graphics Engine
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;

        // Draw the background
        int firstX = backgroundResetX % utils.readImage(config.getBackground()).getWidth();
        if (firstX > 0) {
            g.drawImage(utils.readImage(config.getBackground()), firstX - utils.readImage(config.getBackground()).getWidth(), 0, this);
        }
        for (int x = firstX; x < getWidth() + utils.readImage(config.getBackground()).getWidth(); x += utils.readImage(config.getBackground()).getWidth()) {
            g.drawImage(utils.readImage(config.getBackground()), x, 0, this);
        }

        // Copy the obstacles list to avoid ConcurrentModificationException
        ArrayList<Obstacle> obstaclesCopy = new ArrayList<>(obstacles);
        for (Obstacle component : obstaclesCopy) {
            g.drawImage(component.getImage(), component.getX(), component.getY(), this);
        }

        // Draw the player
        if (!rainbowMode) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        } else {
            g.drawImage(player.getRainbow().getImage(), player.getX(), player.getY(), this);
        }

        // Debug Hitbox
        if (developerMode || hitbox) {
            ArrayList<Rectangle> greenZonesCopy = new ArrayList<>(greenZones);
            for (Rectangle component : greenZonesCopy) {
                g.setColor(Color.GREEN);
                g.fillRect(component.x, component.y, component.width, component.height);
            }

            for (Obstacle component : obstaclesCopy) {
                g.setColor(Color.RED);
                g.drawRect(component.getX(), component.getY(), component.getWidth(), component.getHeight());
            }

            g.setColor(Color.YELLOW);
            g.drawRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        }
    }


    // Handles Jump
    private void jump() {
        // Press to restart
        if ((gameOver && player.getY() > getHeight()) || (gameOver && config.getArgs().length >= 2)) {

            if (keys.size() != points || keys.size() >= events.size() || !utils.containsKey(keys, events)) {
                cheatsEnabled = true;
                if (!developerMode) {
                    JOptionPane.showMessageDialog(this, "Cheat Engine Detected", "Cheats Detected", JOptionPane.INFORMATION_MESSAGE);
                    utils.shutdown();
                }
            }

            if (!cheatsEnabled) config.setPoints(points);
            else config.setPoints(-10);
            config.getUi().initUI();
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

    // Handles game over
    private void gameOver() {
        utils.stopHeavyAudio();
        gameOver = true;
        isPaused = false;
        pauseLabel.setVisible(false);
        gameOverLabel.setVisible(true);
        utils.audioPlayer(this, config.getDieSound(), config.isSound(), false);
    }
}