package main;

import scenes.IntroScene;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame gameWindow = new JFrame("Cinema Corner");
            gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameWindow.setResizable(false);
            gameWindow.setSize(1280, 960);
            gameWindow.setLocationRelativeTo(null);

            gameWindow.setUndecorated(true);

            // Start with IntroScene
            IntroScene intro = new IntroScene(gameWindow, () -> {
                // Switch to MenuScene after intro
                scenes.MenuScene menu = new scenes.MenuScene(gameWindow, () -> {
                    // Switch to GameplayScene after menu
                    scenes.GameplayScene gameplay = new scenes.GameplayScene(gameWindow);
                    gameplay.showScene();
                });
                menu.showScene();
            });

            intro.showScene();
        });
    }
}
