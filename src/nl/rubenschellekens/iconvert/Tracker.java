package nl.rubenschellekens.iconvert;

import java.util.function.Consumer;

/**
 * @author Ruben Schellekens
 */
public class Tracker {

    /**
     * Value between 0.0d and 1.0d and -1 for indeterminate.
     */
    private volatile double progress;

    /**
     * Gets called when progress gets updated.
     */
    private Consumer<Double> listener;

    /**
     * The start time in miliseconds.
     */
    private long start;

    public Tracker(double progress) {
        this.progress = progress;
    }

    public void start() {
        this.start = System.currentTimeMillis();
    }

    /**
     * Get how many miliseconds have passed since {@link Tracker#start} has been called.
     */
    public long getTime() {
        return System.currentTimeMillis() - start;
    }

    /**
     * {@link Tracker#getTime()} but then as a float number in seconds.
     */
    public float getSeconds() {
        return (float)getTime() / 1000f;
    }

    public void setListener(Consumer<Double> listener) {
        this.listener = listener;
        callListener();
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
        callListener();
    }

    public void addProgress(double toAdd) {
        this.progress += toAdd;
        callListener();
    }

    private void callListener() {
        if (listener != null) {
            listener.accept(progress);
        }
    }

}
