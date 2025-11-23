package core;

import scenes.Scene;
import utils.Constants;
import java.awt.*;

public class GameLoop implements Runnable {
    private Thread gameThread;
    private boolean running = false;

    private SceneManager sceneManager;
    private InputHandler inputHandler;
    private GameRenderer renderer;

    public GameLoop(SceneManager sceneManager, InputHandler inputHandler, GameRenderer renderer) {
        this.sceneManager = sceneManager;
        this.inputHandler = inputHandler;
        this.renderer = renderer;
    }

    public void start() {
        if (running) return;

        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stop() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long lastTime = System.currentTimeMillis();

        while (running) {
            long currentTime = System.currentTimeMillis();
            long deltaTime = currentTime - lastTime;

            update(deltaTime);
            render();

            lastTime = currentTime;

            // Sleep to maintain target FPS
            try {
                long sleepTime = Constants.FRAME_TIME - deltaTime;
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update(long deltaTime) {
        Scene currentScene = sceneManager.getCurrentScene();
        if (currentScene != null) {
            currentScene.update(deltaTime, inputHandler);
        }
    }

    private void render() {
        renderer.clearScreen(Color.BLACK);

        Scene currentScene = sceneManager.getCurrentScene();
        if (currentScene != null) {
            currentScene.render(renderer.getBackGraphics());
        }

        renderer.swapBuffers();
    }
}