package de.MCmoderSD.UI;

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
    private boolean space;
    private boolean f3Pressed;
    private int konamiIndex;

    // Constructor
    public InputHandler(Frame frame) {
        this.frame = frame;

        space = false;

        frame.addKeyListener(this);
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                space = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                space = false;
            }
        });

        // Request focus
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::requestFocusLoop, 0, 100, TimeUnit.MILLISECONDS);
    }

    // Methods
    private void requestFocusLoop() {
        if (frame.isFocusAllowed()) frame.requestFocusInWindow();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Game game = frame.getGame();

        // Exit
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Q) System.exit(0);
        if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_Q) System.exit(0);
        if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_F4) System.exit(0);

        // Pause
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) game.togglePause();
        if (e.getKeyCode() == KeyEvent.VK_P) game.togglePause();

        // Controls
        if (e.getKeyCode() == KeyEvent.VK_SPACE) space = true;
        if (e.getKeyCode() == KeyEvent.VK_UP) space = true;
        if (e.getKeyCode() == KeyEvent.VK_W) space = true;
        if (e.getKeyCode() == KeyEvent.VK_ENTER) space = true;

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

        // Controls
        if (e.getKeyCode() == KeyEvent.VK_SPACE) space = false;
        if (e.getKeyCode() == KeyEvent.VK_UP) space = false;
        if (e.getKeyCode() == KeyEvent.VK_W) space = false;
        if (e.getKeyCode() == KeyEvent.VK_ENTER) space = false;

        // Debug
        if (e.getKeyCode() == KeyEvent.VK_F3) f3Pressed = false;
    }

    // Getter
    public boolean isJump() {
        return space;
    }
}
