package scenes;

import core.*;
import utils.Constants;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EndScene extends Scene {
    private BufferedImage backgroundImage;
    private int finalMoney = 0;

    public EndScene(SceneManager sceneManager) {
        super(sceneManager);
    }

    @Override
    public void init() {
        backgroundImage = AssetLoader.loadImage("images/EndScene.png");
    }

    public void setFinalMoney(int money) {
        this.finalMoney = money;
    }

    @Override
    public void update(long deltaTime, InputHandler input) {
        // Press E to return to menu
        if (input.isEJustPressed()) {
            sceneManager.switchScene(Constants.SCENE_MENU);
        }
    }

    @Override
    public void render(Graphics2D g) {
        // Background
        g.setColor(new Color(30, 30, 50));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, null);
        }

        // Victory message
        g.setFont(AssetLoader.getFont("title"));
        g.setColor(Color.YELLOW);
        String title = "GAME COMPLETE!";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (Constants.WINDOW_WIDTH - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 200);

        // Final score
        g.setFont(AssetLoader.getFont("default"));
        g.setColor(Color.GREEN);
        String moneyText = "Total Money: $" + finalMoney;
        int moneyX = (Constants.WINDOW_WIDTH - g.getFontMetrics().stringWidth(moneyText)) / 2;
        g.drawString(moneyText, moneyX, 300);

        // Instructions
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(Color.WHITE);
        String instruction = "Press E to return to menu";
        int instX = (Constants.WINDOW_WIDTH - g.getFontMetrics().stringWidth(instruction)) / 2;
        g.drawString(instruction, instX, 400);
    }
}