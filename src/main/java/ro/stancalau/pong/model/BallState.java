package ro.stancalau.pong.model;

import ro.stancalau.pong.engine.IllegalBallPositionException;

public class BallState {

    private static final int DEFLECTING_SCALE = 3;

    private final PlayGroundState playGround;

    private final double radius;
    private double deltaX;
    private double deltaY;
    private double xPosition;
    private double yPosition;

    public BallState(PlayGroundState playGround, double radius) {
        this.playGround = playGround;
        this.radius = radius;
        reset();
    }

    public void updatePositions(PadState padState, double vector) throws IllegalBallPositionException {
        xPosition += vector * deltaX;
        yPosition += vector * deltaY;

        updateDeltaX(playGround);
        normalizeXPosition(playGround.getWidth());

        updateDeltaY(playGround, padState);
        normalizeYPosition(playGround.getHeight());
    }

    private void updateDeltaY(PlayGroundState playGround, PadState padState) throws IllegalBallPositionException {
        if (yPosition <= radius) {
            deltaY = Math.abs(deltaY);
        } else if (yPosition >= playGround.getHeight() - radius) {
            double deflection = collisionTest(padState.getWidth(), padState.getX());
            if (Math.abs(deflection) > 1) {
                throw new IllegalBallPositionException("Ball slipped below the floor.");
            } else {
                deltaX = deflection * DEFLECTING_SCALE;
                deltaY = -Math.abs(deltaY);
            }
        }
    }

    private void normalizeXPosition(double width) {
        xPosition = getNormalizedPosition(width, radius, xPosition);
    }

    private void normalizeYPosition(double height) {
        yPosition = getNormalizedPosition(height, radius, yPosition);
    }

    private static double getNormalizedPosition(final double dimension, final double radius, final double position) {
        if (position <= radius) {
            return position + 2 * (radius - position);
        } else if (position >= dimension - radius) {
            return position - 2 * (position - (dimension - radius));
        } else {
            return position;
        }
    }

    private void updateDeltaX(PlayGroundState playGround) {
        if (xPosition <= radius) {
            deltaX = Math.abs(deltaX);
        } else if (xPosition >= playGround.getWidth() - radius) {
            deltaX = -Math.abs(deltaX);
        }
    }

    /**
     * @return Percentage of hit point deviation from center. Perfect center hit returns 0.
     * Full right returns 1.
     * Full left returns -1.
     * Also returns a value whose ABS is larger than 1 if the collision did not happen.
     */
    private double collisionTest(double padWidth, double padX) {
        return (xPosition - (padX + padWidth / 2)) / (padWidth / 2);
    }

    public void reset() {
        xPosition = radius;
        yPosition = radius;
        deltaX = 1;
        deltaY = 1;
    }

    public double getXPosition() {
        return xPosition;
    }

    public double getYPosition() {
        return yPosition;
    }

    public double getDeltaY() {
        return deltaY;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public double getRadius() {
        return radius;
    }
}
