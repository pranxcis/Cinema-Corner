package core;

import scenes.*;
import utils.Constants;

public class SceneManager {
    private Scene currentScene;
    private String currentSceneName;

    public SceneManager() {
        switchScene(Constants.SCENE_INTRO);
    }

    public void switchScene(String sceneName) {
        currentSceneName = sceneName;

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
                currentScene = new GameplayScene(this);
                break;
            case Constants.SCENE_LEVEL_CLEAR:
                currentScene = new LevelClearScene(this);
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
}