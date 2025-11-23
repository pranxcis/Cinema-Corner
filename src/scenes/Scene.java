package scenes;

import core.InputHandler;
import core.SceneManager;
import java.awt.*;

public abstract class Scene {
    protected SceneManager sceneManager;

    public Scene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public abstract void init();
    public abstract void update(long deltaTime, InputHandler input);
    public abstract void render(Graphics2D g);
}