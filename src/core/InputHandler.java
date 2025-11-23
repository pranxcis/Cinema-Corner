package core;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputHandler extends KeyAdapter {
    private boolean[] keys = new boolean[256];
    private boolean spacePressed = false;
    private boolean spaceJustPressed = false;

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code < keys.length) {
            if (code == KeyEvent.VK_SPACE && !keys[code]) {
                spaceJustPressed = true;
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
        boolean result = spaceJustPressed;
        spaceJustPressed = false;
        return result;
    }

    public void reset() {
        for (int i = 0; i < keys.length; i++) {
            keys[i] = false;
        }
        spaceJustPressed = false;
    }
}