package de.MCmoderSD.UI;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InputHandler implements KeyListener {

    // Attributes
    private boolean space;

    // Constructor
    public InputHandler(Frame frame) {
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
    }


    // Methods

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        // Exit
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Q) System.exit(0);
        if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_Q) System.exit(0);
        if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_F4) System.exit(0);

        // Controls
        if (e.getKeyCode() == KeyEvent.VK_SPACE) space = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {

        // Controls
        if (e.getKeyCode() == KeyEvent.VK_SPACE) space = false;
    }

    // Getter
    public boolean spacePressed() {
        return space;
    }
}
