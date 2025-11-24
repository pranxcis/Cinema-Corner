package entities;

import core.AssetLoader;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Trash {
    private int x, y;
    private int width = 64;
    private int height = 64;
    private BufferedImage sprite;

    public Trash(int x, int y) {
        this.x = x;
        this.y = y;
        sprite = AssetLoader.createPlaceholder(width, height, "Trash");
    }

    public void render(Graphics2D g) {
        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, null);
        }

        // Draw label
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("TRASH", x + 10, y - 5);

        // Debug bounds
        g.setColor(new Color(150, 75, 0, 100));
        g.drawRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() { return x; }
    public int getY() { return y; }
}