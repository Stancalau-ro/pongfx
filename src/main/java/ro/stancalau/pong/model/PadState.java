package ro.stancalau.pong.model;

public class PadState {

    private final double width;
    private final double height;
    private double x;

    public PadState(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void updatePosition(int delta, double fieldWidth) {
        x += delta;
        if (x < 0) x = 0;
        if (x > fieldWidth - width){
            x = fieldWidth - width;
        }
    }
}
