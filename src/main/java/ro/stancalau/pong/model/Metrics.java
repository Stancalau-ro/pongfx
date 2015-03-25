package ro.stancalau.pong.model;

import java.util.Observable;

public class Metrics extends Observable{

	private double fps=0;
	private double minFps = Double.MAX_VALUE;
	private double maxFps = Double.MIN_VALUE;

	public double getFps() {
		return fps;
	}

	protected void setFps(double fps) {
		if (this.fps==fps || fps<0 || fps==Double.POSITIVE_INFINITY) return;
		this.fps = fps;

		if (fps<minFps) minFps = fps;
		if (fps>maxFps) maxFps = fps;

		setChanged();
		notifyObservers();
	}

	public double getMinFps() {
		return minFps;
	}

	public double getMaxFps() {
		return maxFps;
	}
}
