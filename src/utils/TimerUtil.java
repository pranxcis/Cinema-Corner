package utils;

public class TimerUtil {
    private long startTime;
    private long duration;
    private boolean running;

    public TimerUtil(long duration) {
        this.duration = duration;
        this.running = false;
    }

    public void start() {
        startTime = System.currentTimeMillis();
        running = true;
    }

    public void reset() {
        running = false;
        startTime = 0;
    }

    public boolean isFinished() {
        if (!running) return false;
        return (System.currentTimeMillis() - startTime) >= duration;
    }

    public long getElapsedTime() {
        if (!running) return 0;
        return System.currentTimeMillis() - startTime;
    }

    public float getProgress() {
        if (!running) return 0f;
        long elapsed = getElapsedTime();
        return Math.min(1f, (float) elapsed / duration);
    }

    public boolean isRunning() {
        return running;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}