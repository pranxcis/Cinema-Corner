package config;

public class DayConfig {
    private int dayNumber;
    private int goalCustomers;
    private long spawnInterval;

    public DayConfig(int dayNumber) {
        this.dayNumber = dayNumber;

        switch (dayNumber) {
            case 1:
                goalCustomers = GameConfig.DAY1_GOAL;
                spawnInterval = 5000; // 5 seconds
                break;
            case 2:
                goalCustomers = GameConfig.DAY2_GOAL;
                spawnInterval = 4000; // 4 seconds
                break;
            case 3:
                goalCustomers = GameConfig.DAY3_GOAL;
                spawnInterval = 3000; // 3 seconds
                break;
            default:
                goalCustomers = 5;
                spawnInterval = 5000;
        }
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public int getGoalCustomers() {
        return goalCustomers;
    }

    public long getSpawnInterval() {
        return spawnInterval;
    }
}