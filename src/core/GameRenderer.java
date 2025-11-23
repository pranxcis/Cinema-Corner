package core;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GameRenderer extends JPanel {
    private BufferedImage backBuffer;
    private Graphics2D backGraphics;
    private int width, height;

    public GameRenderer(int width, int height) {
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
        setDoubleBuffered(true);

        backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        backGraphics = backBuffer.createGraphics();
        backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public Graphics2D getBackGraphics() {
        return backGraphics;
    }

    public void clearScreen(Color color) {
        backGraphics.setColor(color);
        backGraphics.fillRect(0, 0, width, height);
    }

    public void swapBuffers() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backBuffer, 0, 0, null);
    }
}