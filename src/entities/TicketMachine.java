package entities;

import core.AssetLoader;
import utils.Constants;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TicketMachine {
    private int x, y;
    private int width = Constants.MACHINE_WIDTH;
    private int height = Constants.MACHINE_HEIGHT;
    private int state = Constants.MACHINE_FULL;

    private BufferedImage currentSprite;
    private long printTimer = 0;
    private boolean isPrinting = false;

    public TicketMachine(int x, int y) {
        this.x = x;
        this.y = y;
        updateSprite();
    }

    public void update(long deltaTime) {
        if (isPrinting) {
            printTimer += deltaTime;

            if (printTimer < 500) {
                state = Constants.MACHINE_REFILL_1; // Using refill as printing states
            } else if (printTimer < 1000) {
                state = Constants.MACHINE_REFILL_2;
            } else {
                state = Constants.MACHINE_FULL;
                isPrinting = false;
                printTimer = 0;
            }
            updateSprite();
        }
    }

    public void render(Graphics2D g) {
        if (currentSprite != null) {
            g.drawImage(currentSprite, x, y, width, height, null);
        }

        // Draw label
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("Tickets", x + 10, y - 5);

        // Debug bounds
        g.setColor(new Color(0, 255, 0, 100));
        g.drawRect(x, y, width, height);
    }

    private void updateSprite() {
        String basePath = "machines/Ticket/";
        String stateName = "";

        switch (state) {
            case Constants.MACHINE_EMPTY:
                stateName = "Ticket Machine Empty";
                break;
            case Constants.MACHINE_REFILL_1:
                stateName = "Ticket Machine Printing 1";
                break;
            case Constants.MACHINE_REFILL_2:
                stateName = "Ticket Machine Printing 2";
                break;
            case Constants.MACHINE_FULL:
            default:
                stateName = "Ticket Machine Ready";
                break;
        }

        currentSprite = AssetLoader.loadImage(basePath + stateName + ".png");
    }

    public boolean canDispense() {
        return state == Constants.MACHINE_FULL;
    }

    public void dispense() {
        if (state == Constants.MACHINE_FULL) {
            state = Constants.MACHINE_EMPTY;
        }
        updateSprite();
    }

    public void startPrinting() {
        if (state == Constants.MACHINE_EMPTY) {
            isPrinting = true;
            printTimer = 0;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        updateSprite();
    }
}