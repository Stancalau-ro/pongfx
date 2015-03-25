package ro.stancalau.pong.model;

import java.awt.*;

public class MouseState {

    private Point lastPosition;

    public MouseState() {
        this.lastPosition = getMousePosition();
    }

    public int getCurrentDeltaX(){
        Point currentPosition = getMousePosition();
        int deltaX = currentPosition.x - lastPosition.x;
        lastPosition = currentPosition;
        return deltaX;
    }

    private Point getMousePosition() {
        return MouseInfo.getPointerInfo().getLocation();
    }
}
