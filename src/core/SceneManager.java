package core;

import scenes.*;
import utils.Constants;

public class SceneManager {
    private Scene currentScene;
    private String currentSceneName;
    private String previousSceneName; // Track previous scene

    public SceneManager() {
        previousSceneName = "";
        switchScene(Constants.SCENE_INTRO);
    }

    public void switchScene(String sceneName) {
        previousSceneName = currentSceneName != null ? currentSceneName : "";
        currentSceneName = sceneName;

        System.out.println("SceneManager switching from [" + previousSceneName + "] to [" + sceneName + "]");

        switch (sceneName) {
            case Constants.SCENE_INTRO:
                currentScene = new IntroScene(this);
                break;
            case Constants.SCENE_MENU:
                currentScene = new MenuScene(this);
                break;
            case Constants.SCENE_BUFFER:
                currentScene = new BufferScene(this);
                break;
            case Constants.SCENE_LOBBY:
                currentScene = new LobbyScene(this);
                break;
            case Constants.SCENE_GAMEPLAY:
                currentScene = new Gameplay1(this);
                break;
            case "gameplay2":
                currentScene = new Gameplay2(this);
                break;
            case "gameplay3":
                currentScene = new Gameplay3(this);
                break;
            case Constants.SCENE_LEVEL_CLEAR:
                currentScene = new EndScene(this);
                break;
            case "end":
                currentScene = new EndScene(this);
                break;
            default:
                currentScene = new MenuScene(this);
        }

        if (currentScene != null) {
            currentScene.init();
        }
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public String getCurrentSceneName() {
        return currentSceneName;
    }

    public String getPreviousSceneName() {
        return previousSceneName;
    }
}