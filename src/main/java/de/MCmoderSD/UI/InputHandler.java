package de.MCmoderSD.UI;

import de.MCmoderSD.core.Controller;
import de.MCmoderSD.core.Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InputHandler implements KeyListener {

    // Associations
    private final Frame frame;

    // Constants
    private final int[] konamiCode = {KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_B, KeyEvent.VK_A};

    // Attributes
    private boolean f3Pressed;
    private int konamiIndex;

    // Constructor
    public InputHandler(Frame frame) {
        this.frame = frame;

        frame.addKeyListener(this);
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                frame.getGame().jump();
            }
        });

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
        Game game = frame.getGame();
        Controller controller = frame.getController();

        // Exit
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Q) System.exit(0);
        if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_Q) System.exit(0);
        if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_F4) System.exit(0);

        // Reverse Toggle
        if (f3Pressed && e.getKeyCode() == KeyEvent.VK_R) controller.toggleReverse();

        // Asset Switch
        if (f3Pressed && e.getKeyCode() == KeyEvent.VK_C) controller.switchAsset();

        // Sound Toggle
        if (e.getKeyCode() == KeyEvent.VK_S) {
            if (frame.getGameUI().isVisible()) game.toggleSound();
            else if (frame.getMenu().isVisible()) controller.toggleSound();
        }

        // Pause
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) game.togglePause();
        if (e.getKeyCode() == KeyEvent.VK_P) game.togglePause();

        // Controls
        if (e.getKeyCode() == KeyEvent.VK_SPACE) game.jump();
        if (e.getKeyCode() == KeyEvent.VK_UP) game.jump();
        if (e.getKeyCode() == KeyEvent.VK_W) game.jump();
        if (e.getKeyCode() == KeyEvent.VK_ENTER) game.jump();

        // Konami Code
        if (e.getKeyCode() == konamiCode[konamiIndex]) {
            konamiIndex++;
            if (konamiIndex == konamiCode.length) {
                game.toggleKonami();
                konamiIndex = 0;
            }
        } else konamiIndex = 0;

        // Debug
        if (e.getKeyCode() == KeyEvent.VK_F3) f3Pressed = true;
        if (f3Pressed && e.getKeyCode() == KeyEvent.VK_F) game.toggleFps();
        if (f3Pressed && e.getKeyCode() == KeyEvent.VK_B) game.toggleHitboxes();
    }

    @Override
    public void keyReleased(KeyEvent e) {

        // Debug
        if (e.getKeyCode() == KeyEvent.VK_F3) f3Pressed = false;
    }
}