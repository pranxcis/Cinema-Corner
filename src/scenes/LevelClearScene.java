package scenes;

import core.*;
import utils.Constants;
import java.awt.*;

public class LevelClearScene extends Scene {

    public LevelClearScene(SceneManager sceneManager) {
        super(sceneManager);
    }

    @Override
    public void init() {
        // Initialize level clear scene
    }

    @Override
    public void update(long deltaTime, InputHandler input) {
        if (input.isSpaceJustPressed()) {
            sceneManager.switchScene(Constants.SCENE_MENU);
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(new Color(30, 30, 50));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        g.setFont(AssetLoader.getFont("title"));
        g.setColor(Color.YELLOW);
        g.drawString("LEVEL CLEAR!", Constants.WINDOW_WIDTH / 2 - 150, Constants.WINDOW_HEIGHT / 2);

        g.setFont(AssetLoader.getFont("default"));
        g.setColor(Color.WHITE);
        g.drawString("Press SPACE to return to menu", Constants.WINDOW_WIDTH / 2 - 150, Constants.WINDOW_HEIGHT / 2 + 50);
    }
}