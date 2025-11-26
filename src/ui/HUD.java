package ui;

import utils.Constants;
import java.awt.*;

public class HUD {
    private int currentDay;
    private int money;
    private int heldItem;
    private boolean nearInteractable;
    private boolean isPaused;
    private String interactMessage;

    private Font smallFont;
    private Font mediumFont;
    private Font largeFont;

    public HUD() {
        smallFont = new Font("Arial", Font.PLAIN, 14);
        mediumFont = new Font("Arial", Font.BOLD, 18);
        largeFont = new Font("Arial", Font.BOLD, 32);

        currentDay = 1;
        money = 0;
        heldItem = Constants.ITEM_NONE;
        nearInteractable = false;
        isPaused = false;
        interactMessage = "";
    }

    public void render(Graphics2D g) {
        // Draw controls (bottom-left)
        g.setFont(smallFont);
        g.setColor(Color.WHITE);

        // Draw day and money (top-left)
        g.setFont(mediumFont);
        g.setColor(Color.YELLOW);
        g.drawString("Day " + currentDay, 20, 30);
        g.setColor(Color.GREEN);
        g.drawString("Money: $" + money, 20, 55);

        // Draw held item (top-right)
        if (heldItem != Constants.ITEM_NONE) {
            g.setFont(mediumFont);
            g.setColor(Color.CYAN);
            String itemName = getItemName(heldItem);
            int textWidth = g.getFontMetrics().stringWidth("Holding: " + itemName);
            g.drawString("Holding: " + itemName, Constants.WINDOW_WIDTH - textWidth - 20, 30);
        }

        // Draw interact prompt (center-bottom)
        if (nearInteractable && !interactMessage.isEmpty()) {
            g.setFont(mediumFont);
            g.setColor(Color.WHITE);

            // Draw background
            int textWidth = g.getFontMetrics().stringWidth(interactMessage);
            int bgX = (Constants.WINDOW_WIDTH - textWidth) / 2 - 10;
            int bgY = Constants.WINDOW_HEIGHT - 120;
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(bgX, bgY, textWidth + 20, 35);

            // Draw text
            g.setColor(Color.YELLOW);
            g.drawString(interactMessage, bgX + 10, bgY + 23);
        }

        // Draw pause overlay
        if (isPaused) {
            drawPauseOverlay(g);
        }
    }

    private void drawPauseOverlay(Graphics2D g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        // Pause text
        g.setFont(largeFont);
        g.setColor(Color.WHITE);
        String pauseText = "PAUSED";
        FontMetrics fm = g.getFontMetrics();
        int textX = (Constants.WINDOW_WIDTH - fm.stringWidth(pauseText)) / 2;
        int textY = Constants.WINDOW_HEIGHT / 2;
        g.drawString(pauseText, textX, textY);

        // Instructions
        g.setFont(mediumFont);
        String instruction = "Press ESC to resume";
        int instX = (Constants.WINDOW_WIDTH - g.getFontMetrics().stringWidth(instruction)) / 2;
        g.drawString(instruction, instX, textY + 50);
    }

    private String getItemName(int item) {
        switch (item) {
            case Constants.ITEM_POPCORN: return "Popcorn";
            case Constants.ITEM_DRINK: return "Drink";
            case Constants.ITEM_BOTH: return "Popcorn + Drink";
            case Constants.ITEM_TICKET: return "Ticket";
            default: return "None";
        }
    }

    // Setters
    public void setDay(int day) { this.currentDay = day; }
    public void setMoney(int money) { this.money = money; }
    public void addMoney(int amount) { this.money += amount; }
    public void setHeldItem(int item) { this.heldItem = item; }
    public void setNearInteractable(boolean near) { this.nearInteractable = near; }
    public void setInteractMessage(String message) { this.interactMessage = message; }
    public void setPaused(boolean paused) { this.isPaused = paused; }

    // Getters
    public int getMoney() { return money; }
    public boolean isPaused() { return isPaused; }
}