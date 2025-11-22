package core;

import javax.swing.JFrame;
import scenes.Scene; // We'll define an abstract Scene class

public class SceneManager {
    private static JFrame gameWindow;
    private static Scene currentScene;

    // Initialize with the game window
    public static void init(JFrame window) {
        gameWindow = window;
    }

    // Switch to a new scene
    public static void setScene(Scene newScene) {
        if (currentScene != null) {
            currentScene.hideScene(); // Cleanup current scene
        }
        currentScene = newScene;
        currentScene.showScene(gameWindow); // Show new scene
    }

    public static Scene getCurrentScene() {
        return currentScene;
    }
}
