package entities;

import core.*;
import utils.Constants;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player {
    private int x, y;
    private int width = Constants.PLAYER_WIDTH;
    private int height = Constants.PLAYER_HEIGHT;
    private int speed = Constants.PLAYER_SPEED;
    private int itemState = Constants.ITEM_NONE;

    private BufferedImage currentSprite;
    private String currentDirection = "idle";

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        updateSprite();
    }

    public void update(long deltaTime, InputHandler input) {
        boolean moved = false;
        String newDirection = "idle"; // Default to idle

        // Check for interact (Space key held)
        if (input.isSpacePressed()) {
            newDirection = "interact";
        }
        // Check for movement (only if not interacting)
        else if (input.isWPressed()) {
            y -= speed;
            newDirection = "up";
            moved = true;
        }
        else if (input.isSPressed()) {
            y += speed;
            newDirection = "down";
            moved = true;
        }
        else if (input.isAPressed()) {
            x -= speed;
            newDirection = "left";
            moved = true;
        }
        else if (input.isDPressed()) {
            x += speed;
            newDirection = "right";
            moved = true;
        }

        // Update sprite if direction changed
        if (!newDirection.equals(currentDirection)) {
            currentDirection = newDirection;
            updateSprite();
        }
    }

    private void updateSprite() {
        String basePath = "player/";
        String itemFolder = "";

        switch (itemState) {
            case Constants.ITEM_NONE:
                itemFolder = "Bare/";
                break;
            case Constants.ITEM_POPCORN:
                itemFolder = "Popcorn/";
                break;
            case Constants.ITEM_DRINK:
                itemFolder = "Drink/";
                break;
            case Constants.ITEM_BOTH:
                itemFolder = "Snack/";
                break;
            case Constants.ITEM_TICKET:
                itemFolder = "Ticket/";
                break;
        }

        String directionName = "";
        switch (currentDirection) {
            case "idle": directionName = "Idle"; break;
            case "up": directionName = "Up"; break;
            case "down": directionName = "Down"; break;
            case "left": directionName = "Left"; break;
            case "right": directionName = "Right"; break;
            case "interact": directionName = "Interact"; break;
        }

        String spritePath = basePath + itemFolder + directionName;
        currentSprite = AssetLoader.loadImage(spritePath + ".png");
    }

    public void render(Graphics2D g) {
        if (currentSprite != null) {
            g.drawImage(currentSprite, x, y, width, height, null);
        }

        // Debug: draw bounds
        g.setColor(new Color(255, 0, 0, 100));
        g.drawRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Getters and setters
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public int getItemState() { return itemState; }
    public void setItemState(int itemState) {
        this.itemState = itemState;
        updateSprite();
    }
}