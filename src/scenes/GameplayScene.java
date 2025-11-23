package scenes;

import core.*;
import entities.Player;
import utils.Constants;
import java.awt.*;

public class GameplayScene extends Scene {
    private Player player;

    public GameplayScene(SceneManager sceneManager) {
        super(sceneManager);
    }

    @Override
    public void init() {
        player = new Player(Constants.WINDOW_WIDTH / 2, Constants.WINDOW_HEIGHT / 2);
    }

    @Override
    public void update(long deltaTime, InputHandler input) {
        player.update(deltaTime, input);
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(new Color(50, 50, 70));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        player.render(g);

        g.setColor(Color.WHITE);
        g.setFont(AssetLoader.getFont("default"));
        g.drawString("Gameplay Scene - Coming Soon", 20, 30);
    }
}