package ro.stancalau.pong.model;

public class BallState {

    private double deltaX;
    private double deltaY;
    private double xPosition;
    private double yPosition;
    private double radius;

    public BallState(double radius) {
        this.radius = radius;
        reset();
    }

    public void updatePositions(PlayGroundState playGround, double vector, PadState padState, double speed) throws IllegalStateException {
        xPosition += vector * deltaX;
        yPosition += vector * deltaY;

        if (xPosition <= radius) {
            deltaX = Math.abs(deltaX);
        }
        if (xPosition >= playGround.getWidth() - radius) {
            deltaX = -Math.abs(deltaX);
        }
        if (yPosition <= radius) {
            deltaY = Math.abs(deltaY);
        }
        if (yPosition >= playGround.getHeight() - radius) {
            double col = collisionTest(padState.getWidth(), padState.getX());
            if (Math.abs(col) > 1) {
                throw new IllegalStateException("Ball slipped below the floor.");
            } else {
                deltaX = col * 3;
                deltaY = -Math.abs(deltaY);
            }
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
