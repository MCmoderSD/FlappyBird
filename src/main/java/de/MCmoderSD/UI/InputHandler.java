package de.MCmoderSD.UI;

import de.MCmoderSD.core.Controller;
import de.MCmoderSD.core.Game;
import de.MCmoderSD.executor.NanoLoop;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static java.awt.event.KeyEvent.*;

public class InputHandler implements KeyListener {

    // Associations
    private final Frame frame;

    // KeyCodes
    private final int[] konamiCode = {
            VK_UP,
            VK_UP,
            VK_DOWN,
            VK_DOWN,
            VK_LEFT,
            VK_RIGHT,
            VK_LEFT,
            VK_RIGHT,
            VK_B,
            VK_A
    };

    private final ArrayList<Integer> jumpKeys;

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

        // Init Lists
        jumpKeys = new ArrayList<>();

        // jumpKeys
        jumpKeys.add(VK_SPACE);
        jumpKeys.add(VK_UP);
        jumpKeys.add(VK_W);
        jumpKeys.add(VK_ENTER);

        // Request focus
        new NanoLoop(frame::requestFocusInWindow, 1).start();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {

        // Variables
        Controller controller = frame.getController();
        Game game = frame.getGame();
        var key = e.getKeyCode();

        // Exit
        if ((e.isControlDown() && (key == VK_C || key == VK_Q)) || (e.isAltDown() && (key == VK_F4 || key == VK_Q))) System.exit(0);

        // Asset Switch
        if (f3Pressed && key == VK_C) controller.switchAsset();

        // Sound Toggle
        if (key == VK_S) {
            if (frame.getGameUI().isVisible()) game.toggleSound();
            else if (frame.getMenu().isVisible()) controller.toggleSound();
        }

        // Pause
        if (key == VK_ESCAPE) game.togglePause();
        if (key == VK_P) game.togglePause();

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
        if (key == VK_F3) f3Pressed = true;

        if (!f3Pressed) return;

        if (key == VK_F) game.toggleFps();
        if (key == VK_T) game.toggleTps();
        if (key == VK_B) game.toggleHitboxes();
    }

    @Override
    public void keyReleased(KeyEvent e) {

        // Debug
        if (e.getKeyCode() == VK_F3) f3Pressed = false;
    }
}