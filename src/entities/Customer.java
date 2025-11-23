package entities;

import core.AssetLoader;
import utils.Constants;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Customer {
    private int x, y;
    private int width = 48;
    private int height = 64;
    private int requestedItem = Constants.ITEM_NONE;
    private boolean served = false;

    private BufferedImage sprite;

    public Customer(int x, int y, int requestedItem) {
        this.x = x;
        this.y = y;
        this.requestedItem = requestedItem;
        sprite = AssetLoader.loadImage("customer/customer.png");
    }

    public void render(Graphics2D g) {
        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, null);
        }

        // Draw request bubble
        g.setColor(Color.WHITE);
        g.fillOval(x + width, y - 20, 40, 40);
        g.setColor(Color.BLACK);
        g.drawOval(x + width, y - 20, 40, 40);

        // Draw requested item icon
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        String itemText = getItemText();
        g.drawString(itemText, x + width + 5, y - 5);

        // Debug bounds
        g.setColor(new Color(255, 0, 255, 100));
        g.drawRect(x, y, width, height);
    }

    private String getItemText() {
        switch (requestedItem) {
            case Constants.ITEM_POPCORN: return "POP";
            case Constants.ITEM_DRINK: return "DRINK";
            case Constants.ITEM_BOTH: return "BOTH";
            case Constants.ITEM_TICKET: return "TIX";
            default: return "?";
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getRequestedItem() {
        return requestedItem;
    }

    public boolean isServed() {
        return served;
    }

    public void setServed(boolean served) {
        this.served = served;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}