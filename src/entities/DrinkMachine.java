package entities;

import core.AssetLoader;
import utils.Constants;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DrinkMachine {
    private int x, y;
    private int width = Constants.MACHINE_WIDTH;
    private int height = Constants.MACHINE_HEIGHT;
    private int state = Constants.MACHINE_FULL;

    private BufferedImage currentSprite;
    private long refillTimer = 0;
    private boolean isRefilling = false;

    public DrinkMachine(int x, int y) {
        this.x = x;
        this.y = y;
        updateSprite();
    }

    public void update(long deltaTime) {
        if (isRefilling) {
            refillTimer += deltaTime;

            if (refillTimer < 500) {
                state = Constants.MACHINE_REFILL_1;
            } else if (refillTimer < 1000) {
                state = Constants.MACHINE_REFILL_2;
            } else {
                state = Constants.MACHINE_FULL;
                isRefilling = false;
                refillTimer = 0;
            }
            updateSprite();
        }
    }

    public void render(Graphics2D g) {
        if (currentSprite != null) {
            g.drawImage(currentSprite, x, y, width, height, null);
        }

        // Draw label
        //g.setColor(Color.WHITE);
        //g.setFont(new Font("Arial", Font.PLAIN, 14));
        //g.drawString("Drinks", x + 10, y - 5);

        // Debug bounds
        //g.setColor(new Color(0, 255, 0, 100));
        //g.drawRect(x, y, width, height);
    }

    private void updateSprite() {
        String basePath = "machines/Drink/";
        String stateName = "";

        switch (state) {
            case Constants.MACHINE_EMPTY:
                stateName = "Drink Machine Empty";
                break;
            case Constants.MACHINE_REFILL_1:
                stateName = "Drink Machine Refill 1";
                break;
            case Constants.MACHINE_REFILL_2:
                stateName = "Drink Machine Refill 2";
                break;
            case Constants.MACHINE_FULL:
                stateName = "Drink Machine Full";
                break;
            case Constants.MACHINE_LESS_FULL:
                stateName = "Drink Machine LessFull";
                break;
        }

        currentSprite = AssetLoader.loadImage(basePath + stateName + ".png");
    }

    public boolean canDispense() {
        return state == Constants.MACHINE_FULL || state == Constants.MACHINE_LESS_FULL;
    }

    public void dispense() {
        if (state == Constants.MACHINE_FULL) {
            state = Constants.MACHINE_LESS_FULL;
        } else if (state == Constants.MACHINE_LESS_FULL) {
            state = Constants.MACHINE_EMPTY;
        }
        updateSprite();
    }

    public void startRefill() {
        if (state == Constants.MACHINE_EMPTY) {
            isRefilling = true;
            refillTimer = 0;
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