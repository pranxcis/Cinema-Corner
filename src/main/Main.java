package main;

import core.*;
import javax.swing.*;

public class Main extends JFrame {
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 960;

    private GameLoop gameLoop;
    private SceneManager sceneManager;
    private InputHandler inputHandler;
    private GameRenderer renderer;

    public Main() {
        setTitle("Cinema Corner");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setUndecorated(true);

        // Initialize core components
        AssetLoader.initialize();
        sceneManager = new SceneManager();
        inputHandler = new InputHandler();
        renderer = new GameRenderer(WINDOW_WIDTH, WINDOW_HEIGHT);

        // Add renderer panel and input handler
        add(renderer);
        addKeyListener(inputHandler);

        // Initialize game loop
        gameLoop = new GameLoop(sceneManager, inputHandler, renderer);

        setVisible(true);

        // Start the game
        gameLoop.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}