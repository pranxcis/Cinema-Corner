package config;

import java.awt.Point;

public class SpawnConfig {
    // Customer spawn point (below screen, moves upward)
    public static final Point SPAWN_POINT = new Point(1090, 900);

    // Counter positions where customers wait (3 counters)
    public static final Point COUNTER_POS_1 = new Point(1090, 610);
    public static final Point COUNTER_POS_2 = new Point(1090, 720);
    public static final Point COUNTER_POS_3 = new Point(1090, 815);

    // Exit point (cinema entrance)
    public static final Point EXIT_POINT = new Point(1090, 900);

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