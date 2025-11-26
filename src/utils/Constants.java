package utils;

public class Constants {
    // Window dimensions
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 960;

    // Game settings
    public static final int TARGET_FPS = 60;
    public static final long FRAME_TIME = 2000 / TARGET_FPS;

    // Player settings
    public static final int PLAYER_WIDTH = 110;
    public static final int PLAYER_HEIGHT = 110;
    public static final int PLAYER_SPEED = 10;

    // Machine settings
    public static final int MACHINE_WIDTH = 200;
    public static final int MACHINE_HEIGHT = 200;

    // Animation settings
    public static final int ANIMATION_SPEED = 100; // milliseconds per frame

    // Fade settings
    public static final int FADE_SPEED = 4;
    public static final int INTRO_DISPLAY_TIME = 2000; // 2 seconds

    // Scene types
    public static final String SCENE_INTRO = "intro";
    public static final String SCENE_MENU = "menu";
    public static final String SCENE_BUFFER = "buffer";
    public static final String SCENE_LOBBY = "lobby";
    public static final String SCENE_GAMEPLAY = "gameplay";
    public static final String SCENE_LEVEL_CLEAR = "levelclear";

    // Player item states
    public static final int ITEM_NONE = 0;
    public static final int ITEM_POPCORN = 1;
    public static final int ITEM_DRINK = 2;
    public static final int ITEM_BOTH = 3;
    public static final int ITEM_TICKET = 4;

    // Machine states
    public static final int MACHINE_EMPTY = 0;
    public static final int MACHINE_REFILL_1 = 1;
    public static final int MACHINE_REFILL_2 = 2;
    public static final int MACHINE_FULL = 3;
    public static final int MACHINE_LESS_FULL = 4;

    // Customer states
    public static final int CUSTOMER_WAITING = 0;
    public static final int CUSTOMER_BEING_SERVED = 1;
    public static final int CUSTOMER_SATISFIED = 2;
    public static final int CUSTOMER_ANGRY = 3;
    public static final int CUSTOMER_LEAVING = 4;
}
