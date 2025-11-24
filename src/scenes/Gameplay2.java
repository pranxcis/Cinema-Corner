package scenes;

import core.*;

public class Gameplay2 extends Scene {
    private Gameplay1 gameplayBase;

    public Gameplay2(SceneManager sceneManager) {
        super(sceneManager);
        gameplayBase = new Gameplay1(sceneManager);
    }

    @Override
    public void init() {
        gameplayBase.init();
        // Set to Day 2 configuration - handled in dayConfig
    }

    @Override
    public void update(long deltaTime, InputHandler input) {
        gameplayBase.update(deltaTime, input);
    }

    @Override
    public void render(java.awt.Graphics2D g) {
        gameplayBase.render(g);
    }
}