package de.MCmoderSD.UI;

import de.MCmoderSD.core.Controller;
import de.MCmoderSD.core.Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InputHandler implements KeyListener {

    // Associations
    private final Frame frame;
    private final Game game;
    private final Controller controller;

    // KeyCodes
    private final int[] konamiCode = {
            KeyEvent.VK_UP,
            KeyEvent.VK_UP,
            KeyEvent.VK_DOWN,
            KeyEvent.VK_DOWN,
            KeyEvent.VK_LEFT,
            KeyEvent.VK_RIGHT,
            KeyEvent.VK_LEFT,
            KeyEvent.VK_RIGHT,
            KeyEvent.VK_B,
            KeyEvent.VK_A
    };

    private final ArrayList<Integer> jumpKeys;

    // Attributes
    private boolean f3Pressed;
    private int konamiIndex;

    // Constructor
    public InputHandler(Frame frame) {
        this.frame = frame;
        game = frame.getGame();
        controller = frame.getController();

        frame.addKeyListener(this);
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                frame.getGame().jump();
            }
        });

        // Init Lists
        jumpKeys = new ArrayList<>();

        // jumpKeys
        jumpKeys.add(KeyEvent.VK_SPACE);
        jumpKeys.add(KeyEvent.VK_UP);
        jumpKeys.add(KeyEvent.VK_W);
        jumpKeys.add(KeyEvent.VK_ENTER);

        // Request focus
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::requestFocusLoop, 0, 100, TimeUnit.MILLISECONDS);
    }

    // Request Focus
    private void requestFocusLoop() {
        if (frame.getMenu() != null && frame.getMenu().isVisible() && frame.getMenu().canFocus())
            frame.requestFocusInWindow();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        // Exit
        if (e.isControlDown() && key == KeyEvent.VK_Q) System.exit(0);
        if (e.isAltDown() && key == KeyEvent.VK_Q) System.exit(0);
        if (e.isAltDown() && key == KeyEvent.VK_F4) System.exit(0);

        // Reverse Toggle
        if (f3Pressed && key == KeyEvent.VK_R) controller.toggleReverse();

        // Asset Switch
        if (f3Pressed && key == KeyEvent.VK_C) controller.switchAsset();

        // Sound Toggle
        if (key == KeyEvent.VK_S) {
            if (frame.getGameUI().isVisible()) game.toggleSound();
            else if (frame.getMenu().isVisible()) controller.toggleSound();
        }

        // Pause
        if (key == KeyEvent.VK_ESCAPE) game.togglePause();
        if (key == KeyEvent.VK_P) game.togglePause();

        // Jump
        if (jumpKeys.contains(key)) game.jump();

        // Konami Code
        if (key == konamiCode[konamiIndex]) {
            konamiIndex++;
            if (konamiIndex == konamiCode.length) {
                game.toggleKonami();
                konamiIndex = 0;
            }
        } else konamiIndex = 0;

        // Debug
        if (key == KeyEvent.VK_F3) f3Pressed = true;
        if (f3Pressed && key == KeyEvent.VK_F) game.toggleFps();
        if (f3Pressed && key == KeyEvent.VK_B) game.toggleHitboxes();
    }

    @Override
    public void keyReleased(KeyEvent e) {

        // Debug
        if (e.getKeyCode() == KeyEvent.VK_F3) f3Pressed = false;
    }
}