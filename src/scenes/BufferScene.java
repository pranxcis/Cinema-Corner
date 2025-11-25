package scenes;

import core.*;
import utils.Constants;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BufferScene extends Scene {
    private BufferedImage bufferImage;
    private int alpha = 0;
    private boolean fadingIn = true;
    private long displayTimer = 0;
    private String nextScene;

    public BufferScene(SceneManager sceneManager) {
        super(sceneManager);
    }

    @Override
    public void init() {
        bufferImage = AssetLoader.loadImage("images/BufferScene.png");
        alpha = 0;
        fadingIn = true;
        displayTimer = 0;

        // Determine next scene based on PREVIOUS scene (before buffer)
        String previous = sceneManager.getPreviousSceneName();
        System.out.println("BufferScene - Previous scene was: " + previous);

        if (previous.equals(Constants.SCENE_LOBBY)) {
            nextScene = Constants.SCENE_GAMEPLAY;
            System.out.println("BufferScene -> Gameplay1 (Day 1)");
        } else if (previous.equals(Constants.SCENE_MENU)) {
            nextScene = Constants.SCENE_LOBBY;
            System.out.println("BufferScene -> Lobby");
        } else if (previous.equals(Constants.SCENE_GAMEPLAY)) {
            nextScene = "gameplay2";
            System.out.println("BufferScene -> Gameplay2 (Day 2)");
        } else if (previous.equals("gameplay2")) {
            nextScene = "gameplay3";
            System.out.println("BufferScene -> Gameplay3 (Day 3)");
        } else if (previous.equals("gameplay3")) {
            nextScene = "end";
            System.out.println("BufferScene -> EndScene");
        } else {
            // Default fallback
            nextScene = Constants.SCENE_GAMEPLAY;
            System.out.println("BufferScene -> Default to Gameplay1");
        }
    }

    @Override
    public void update(long deltaTime, InputHandler input) {
        if (fadingIn) {
            alpha += Constants.FADE_SPEED;
            if (alpha >= 255) {
                alpha = 255;
                fadingIn = false;
                displayTimer = 0;
            }
        } else if (displayTimer < 1500) { // 1.5 seconds display
            displayTimer += deltaTime;
        } else {
            // Start fading out
            alpha -= Constants.FADE_SPEED;
            if (alpha <= 0) {
                alpha = 0;
                sceneManager.switchScene(nextScene);
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        // Draw buffer image with alpha
        Composite oldComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255f));

        int x = (Constants.WINDOW_WIDTH - bufferImage.getWidth()) / 2;
        int y = (Constants.WINDOW_HEIGHT - bufferImage.getHeight()) / 2;
        g.drawImage(bufferImage, x, y, null);

        g.setComposite(oldComposite);

        // Draw loading text
        g.setColor(new Color(255, 255, 255, alpha));
        g.setFont(AssetLoader.getFont("default"));
        g.drawString("Loading...", Constants.WINDOW_WIDTH / 2 - 50, Constants.WINDOW_HEIGHT - 50);
    }
}