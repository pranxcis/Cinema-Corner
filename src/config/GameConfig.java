package config;

public class GameConfig {
    // Day configurations
    public static final int TOTAL_DAYS = 3;

    // Money settings
    public static final int MONEY_PER_CORRECT_ORDER = 50;
    public static final int MONEY_PENALTY_WRONG = -20;
    public static final int MONEY_PENALTY_ANGRY = -30;
    public static final int MONEY_PENALTY_TRASH = -10;

    // Customer settings
    public static final int CUSTOMER_WAIT_TIME = 30000; // 30 seconds
    public static final int CUSTOMER_PATIENCE_WARNING = 20000; // 20 seconds

    // Spawn settings
    public static final long MIN_SPAWN_INTERVAL = 3000; // 3 seconds
    public static final long MAX_SPAWN_INTERVAL = 6000; // 6 seconds

    // Day goals
    public static final int DAY1_GOAL = 5; // Serve 5 customers
    public static final int DAY2_GOAL = 8; // Serve 8 customers
    public static final int DAY3_GOAL = 12; // Serve 12 customers

    // Failure thresholds
    public static final int MAX_ANGRY_CUSTOMERS = 3;
    public static final int MAX_WRONG_SERVES = 5;
}