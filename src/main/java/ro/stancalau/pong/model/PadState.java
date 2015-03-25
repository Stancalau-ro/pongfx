package ro.stancalau.pong.model;

public class PadState {

    private double width;
    private double height;
    private double x;

    public PadState(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
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
