package main;

import core.SceneManager;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create the main game window
            JFrame gameWindow = new JFrame("Cinema Corner");
            gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameWindow.setResizable(false);
            gameWindow.setSize(1280, 960);
            gameWindow.setLocationRelativeTo(null);
            gameWindow.setUndecorated(true);

            // Initialize SceneManager with the game window
            SceneManager.init(gameWindow);

            // Show the IntroScene as the first scene
            SceneManager.setScene(new scenes.IntroScene());
        });
    }
}
