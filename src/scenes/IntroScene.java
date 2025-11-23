package scenes;

import core.*;
import utils.Constants;
import java.awt.*;
import java.awt.image.BufferedImage;

public class IntroScene extends Scene {
    private BufferedImage introImage;
    private int alpha = 0;
    private boolean fadingIn = true;
    private long displayTimer = 0;
    private boolean skipRequested = false;

    public IntroScene(SceneManager sceneManager) {
        super(sceneManager);
    }

    @Override
    public void init() {
        introImage = AssetLoader.loadImage("images/Intro.png");
        alpha = 0;
        fadingIn = true;
        displayTimer = 0;
        skipRequested = false;
    }

    @Override
    public void update(long deltaTime, InputHandler input) {
        // Check for skip
        if (input.isSpaceJustPressed()) {
            skipRequested = true;
        }

        if (skipRequested) {
            sceneManager.switchScene(Constants.SCENE_MENU);
            return;
        }

        if (fadingIn) {
            alpha += Constants.FADE_SPEED;
            if (alpha >= 255) {
                alpha = 255;
                fadingIn = false;
                displayTimer = 0;
            }
        } else if (displayTimer < Constants.INTRO_DISPLAY_TIME) {
            displayTimer += deltaTime;
        } else {
            // Start fading out
            alpha -= Constants.FADE_SPEED;
            if (alpha <= 0) {
                alpha = 0;
                sceneManager.switchScene(Constants.SCENE_MENU);
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        // Draw intro image with alpha
        Composite oldComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255f));

        int x = (Constants.WINDOW_WIDTH - introImage.getWidth()) / 2;
        int y = (Constants.WINDOW_HEIGHT - introImage.getHeight()) / 2;
        g.drawImage(introImage, x, y, null);

        g.setComposite(oldComposite);

    }
}