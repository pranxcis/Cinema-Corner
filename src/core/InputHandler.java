package core;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputHandler extends KeyAdapter {
    private boolean[] keys = new boolean[256];
    private boolean eJustPressed = false;
    private boolean tJustPressed = false;
    private boolean escJustPressed = false;

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code < keys.length) {
            // Track "just pressed" for action keys
            if (code == KeyEvent.VK_E && !keys[code]) {
                eJustPressed = true;
            }
            if (code == KeyEvent.VK_T && !keys[code]) {
                tJustPressed = true;
            }
            if (code == KeyEvent.VK_ESCAPE && !keys[code]) {
                escJustPressed = true;
            }
            keys[code] = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code < keys.length) {
            keys[code] = false;
        }
    }

    public boolean isKeyPressed(int keyCode) {
        return keyCode < keys.length && keys[keyCode];
    }

    public boolean isWPressed() {
        return isKeyPressed(KeyEvent.VK_W);
    }

    public boolean isAPressed() {
        return isKeyPressed(KeyEvent.VK_A);
    }

    public boolean isSPressed() {
        return isKeyPressed(KeyEvent.VK_S);
    }

    public boolean isDPressed() {
        return isKeyPressed(KeyEvent.VK_D);
    }

    public boolean isSpacePressed() {
        return isKeyPressed(KeyEvent.VK_SPACE);
    }

    public boolean isSpaceJustPressed() {
        // For backward compatibility
        return isEJustPressed();
    }

    public boolean isEPressed() {
        return isKeyPressed(KeyEvent.VK_E);
    }

    public boolean isEJustPressed() {
        boolean result = eJustPressed;
        eJustPressed = false;
        return result;
    }

    public boolean isTPressed() {
        return isKeyPressed(KeyEvent.VK_T);
    }

    public boolean isTJustPressed() {
        boolean result = tJustPressed;
        tJustPressed = false;
        return result;
    }

    public boolean isEscPressed() {
        return isKeyPressed(KeyEvent.VK_ESCAPE);
    }

    public boolean isEscJustPressed() {
        boolean result = escJustPressed;
        escJustPressed = false;
        return result;
    }

    public void reset() {
        for (int i = 0; i < keys.length; i++) {
            keys[i] = false;
        }
        eJustPressed = false;
        tJustPressed = false;
        escJustPressed = false;
    }
}