package ro.stancalau.pong.engine;

import java.util.Observable;

public class Metrics extends Observable {

    private double fps = 0;
    private double minFps = Double.MAX_VALUE;
    private double maxFps = Double.MIN_VALUE;

    public double getFps() {
        return fps;
    }

    void setFps(double fps) {
        if (!isValidFps(fps)){
            return;
        }
        this.fps = fps;

        if (fps < minFps) minFps = fps;
        if (fps > maxFps) maxFps = fps;

        setChanged();
        notifyObservers();
    }

    private boolean isValidFps(double fps) {
        return (this.fps != fps) && (fps >= 0) && (fps != Double.POSITIVE_INFINITY);
    }

    public double getMinFps() {
        return minFps;
    }

    public double getMaxFps() {
        return maxFps;
    }
}
