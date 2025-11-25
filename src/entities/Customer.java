package entities;

import core.AssetLoader;
import utils.Constants;
import config.GameConfig;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Customer {
    private int x, y;
    private int targetX, targetY;
    private int width = 70;
    private int height = 70;
    private int speed = 2;

    private int customerType; // 1, 2, or 3
    private int state = Constants.CUSTOMER_WAITING;
    private int[] order; // Array of items needed
    private long waitTimer = 0;
    private boolean isAngry = false;
    private boolean hasPlayedArrivalSound = false; // Track if sound already played

    private BufferedImage sprite;
    private String currentDirection = "idle";

    public Customer(int x, int y, int customerType) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        this.customerType = customerType;

        generateRandomOrder();
        loadSprite();
    }

    private void generateRandomOrder() {
        Random rand = new Random();
        int orderType = rand.nextInt(7); // 7 possible combinations

        switch (orderType) {
            case 0: order = new int[]{Constants.ITEM_POPCORN}; break;
            case 1: order = new int[]{Constants.ITEM_DRINK}; break;
            case 2: order = new int[]{Constants.ITEM_TICKET}; break;
            case 3: order = new int[]{Constants.ITEM_POPCORN, Constants.ITEM_DRINK}; break;
            case 4: order = new int[]{Constants.ITEM_POPCORN, Constants.ITEM_TICKET}; break;
            case 5: order = new int[]{Constants.ITEM_DRINK, Constants.ITEM_TICKET}; break;
            case 6: order = new int[]{Constants.ITEM_POPCORN, Constants.ITEM_DRINK, Constants.ITEM_TICKET}; break;
        }
    }

    private void loadSprite() {
        String path = "customer/Customer" + customerType + "/Idle.png";
        sprite = AssetLoader.loadImage(path);
    }

    public void update(long deltaTime) {
        // Update wait timer
        if (state == Constants.CUSTOMER_WAITING) {
            waitTimer += deltaTime;

            if (waitTimer > GameConfig.CUSTOMER_PATIENCE_WARNING && !isAngry) {
                isAngry = true;
            }

            if (waitTimer > GameConfig.CUSTOMER_WAIT_TIME) {
                state = Constants.CUSTOMER_ANGRY;
            }
        }

        // Move toward target
        boolean wasMoving = (x != targetX || y != targetY);

        if (x < targetX) {
            x += speed;
            if (x > targetX) x = targetX;
        } else if (x > targetX) {
            x -= speed;
            if (x < targetX) x = targetX;
        }

        if (y < targetY) {
            y += speed;
            if (y > targetY) y = targetY;
        } else if (y > targetY) {
            y -= speed;
            if (y < targetY) y = targetY;
        }

        // Play sound when reaching counter for the first time
        if (wasMoving && hasReachedTarget() && state == Constants.CUSTOMER_WAITING && !hasPlayedArrivalSound) {
            systems.AudioSystem.getInstance().playCustomerArrive();
            hasPlayedArrivalSound = true;
        }
    }

    public void render(Graphics2D g) {
        // Draw customer sprite
        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, null);
        } else {
            // Fallback if sprite fails to load - draw colored rectangle
            Color customerColor = new Color(255, 180, 200);
            g.setColor(customerColor);
            g.fillRect(x, y, width, height);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height);
            g.drawString("C" + customerType, x + 15, y + 35);
        }

        // Draw order bubble
        drawOrderBubble(g);

        // Draw angry indicator
        if (isAngry) {
            g.setColor(Color.RED);
            g.fillOval(x + width - 15, y - 15, 15, 15);
            g.setColor(Color.WHITE);
            g.drawString("!", x + width - 10, y - 3);
        }
    }

    private void drawOrderBubble(Graphics2D g) {
        int bubbleX = x + width + 5;
        int bubbleY = y - 10;
        int bubbleSize = 40;

        g.setColor(Color.WHITE);
        g.fillOval(bubbleX, bubbleY, bubbleSize, bubbleSize);
        g.setColor(Color.BLACK);
        g.drawOval(bubbleX, bubbleY, bubbleSize, bubbleSize);

        // Draw order icons
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        String orderText = getOrderText();
        g.drawString(orderText, bubbleX + 5, bubbleY + 25);
    }

    private String getOrderText() {
        StringBuilder sb = new StringBuilder();
        for (int item : order) {
            switch (item) {
                case Constants.ITEM_POPCORN: sb.append("P "); break;
                case Constants.ITEM_DRINK: sb.append("D "); break;
                case Constants.ITEM_TICKET: sb.append("T "); break;
            }
        }
        return sb.toString().trim();
    }

    public boolean checkOrder(int playerItem) {
        // Check if player's item matches the order
        if (order.length == 1) {
            return order[0] == playerItem;
        } else if (order.length == 2) {
            // For 2 items, check if it's the BOTH state or individual matches
            if (order[0] == Constants.ITEM_POPCORN && order[1] == Constants.ITEM_DRINK) {
                return playerItem == Constants.ITEM_BOTH;
            }
            // Otherwise check individual items
            return playerItem == order[0] || playerItem == order[1];
        } else if (order.length == 3) {
            // All 3 items - player needs BOTH for popcorn+drink, then ticket separately
            return playerItem == Constants.ITEM_BOTH || playerItem == Constants.ITEM_TICKET;
        }
        return false;
    }

    public void setTarget(int x, int y) {
        this.targetX = x;
        this.targetY = y;
    }

    public boolean hasReachedTarget() {
        return x == targetX && y == targetY;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Getters and setters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getState() { return state; }
    public void setState(int state) { this.state = state; }
    public int[] getOrder() { return order; }
    public boolean isAngry() { return isAngry; }
    public int getCustomerType() { return customerType; }
}