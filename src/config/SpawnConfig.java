package config;

import java.awt.Point;

public class SpawnConfig {
    // Customer spawn point (entrance)
    public static final Point SPAWN_POINT = new Point(50, 500);

    // Counter positions where customers wait
    public static final Point COUNTER_POS_1 = new Point(300, 450);
    public static final Point COUNTER_POS_2 = new Point(400, 450);
    public static final Point COUNTER_POS_3 = new Point(500, 450);

    // Exit point (cinema entrance)
    public static final Point EXIT_POINT = new Point(700, 500);

    // Get counter position based on queue number
    public static Point getCounterPosition(int queueIndex) {
        switch (queueIndex) {
            case 0: return COUNTER_POS_1;
            case 1: return COUNTER_POS_2;
            case 2: return COUNTER_POS_3;
            default: return COUNTER_POS_1;
        }
    }
}