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

    public BufferScene(SceneManager sceneManager) {
        super(sceneManager);
    }

    @Override
    public void init() {
        bufferImage = AssetLoader.loadImage("images/BufferScene/loading.png");
        alpha = 0;
        fadingIn = true;
        displayTimer = 0;
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
                sceneManager.switchScene(Constants.SCENE_LOBBY);
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