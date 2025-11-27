package entities;

import core.AssetLoader;
import utils.Constants;
import config.GameConfig;
import config.SpawnConfig;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Customer {
    private int x, y;
    private int targetX, targetY;
    private int width = 80;
    private int height = 80;
    private int speed = 2;

    private int customerType; // 1, 2, or 3
    private int state = Constants.CUSTOMER_WAITING;
    private List<Integer> order; // List of items needed (can be multiple)
    private List<Integer> receivedItems; // Track what player has given
    private long waitTimer = 0;
    private boolean isAngry = false;
    private boolean hasPlayedArrivalSound = false;
    private boolean isAtCounter = false; // Track if at assigned counter
    private boolean isAtCounter1 = false; // Track if at Counter 1 specifically

    private BufferedImage sprite;
    private String currentDirection = "idle";

    public Customer(int x, int y, int customerType) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        this.customerType = customerType;
        this.receivedItems = new ArrayList<>();

        generateRandomOrder();
        loadSprite();
    }

    private void generateRandomOrder() {
        Random rand = new Random();
        order = new ArrayList<>();
        int orderType = rand.nextInt(7); // 7 possible combinations

        switch (orderType) {
            case 0:
                order.add(Constants.ITEM_POPCORN);
                break;
            case 1:
                order.add(Constants.ITEM_DRINK);
                break;
            case 2:
                order.add(Constants.ITEM_TICKET);
                break;
            case 3:
                order.add(Constants.ITEM_POPCORN);
                order.add(Constants.ITEM_DRINK);
                break;
            case 4:
                order.add(Constants.ITEM_POPCORN);
                order.add(Constants.ITEM_TICKET);
                break;
            case 5:
                order.add(Constants.ITEM_DRINK);
                order.add(Constants.ITEM_TICKET);
                break;
            case 6:
                order.add(Constants.ITEM_POPCORN);
                order.add(Constants.ITEM_DRINK);
                order.add(Constants.ITEM_TICKET);
                break;
        }
    }

    private void loadSprite() {
        String path = "customer/Customer" + customerType + "/Idle.png";
        sprite = AssetLoader.loadImage(path);
    }

    public void update(long deltaTime) {
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

        // Check if reached counter
        if (hasReachedTarget() && !isAtCounter) {
            isAtCounter = true;

            // Check if at Counter 1 specifically
            Point counter1 = SpawnConfig.COUNTER_POS_1;
            if (targetX == counter1.x && targetY == counter1.y) {
                isAtCounter1 = true;
            }

            // Play arrival sound
            if (!hasPlayedArrivalSound) {
                systems.AudioSystem.getInstance().playCustomerArrive();
                hasPlayedArrivalSound = true;
            }
        }

        // Update satisfaction timer ONLY if at Counter 1
        if (state == Constants.CUSTOMER_WAITING && isAtCounter1) {
            waitTimer += deltaTime;

            if (waitTimer > GameConfig.CUSTOMER_PATIENCE_WARNING && !isAngry) {
                isAngry = true;
            }

            if (waitTimer > GameConfig.CUSTOMER_WAIT_TIME) {
                state = Constants.CUSTOMER_ANGRY;
            }
        }
    }

    public void render(Graphics2D g) {
        // Draw customer sprite
        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, null);
        } else {
            // Fallback if sprite fails to load
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

    // Give item to customer, returns true if item was needed
    public boolean giveItem(int playerItem) {
        // Check if this item is in the order and not yet received
        if (order.contains(playerItem) && !receivedItems.contains(playerItem)) {
            receivedItems.add(playerItem);
            return true;
        }
        return false;
    }

    // Check if all items in order have been received
    public boolean isOrderComplete() {
        // Check if received items contains all order items
        for (int item : order) {
            if (!receivedItems.contains(item)) {
                return false;
            }
        }
        return true;
    }

    public void setTarget(int x, int y) {
        this.targetX = x;
        this.targetY = y;
        this.isAtCounter = false; // Reset when given new target
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
    public List<Integer> getOrder() { return order; }
    public boolean isAngry() { return isAngry; }
    public int getCustomerType() { return customerType; }
    public boolean isAtCounter1() { return isAtCounter1; }
}