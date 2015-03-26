package ro.stancalau.pong.engine;

public class IllegalBallPositionException extends Exception {

    public IllegalBallPositionException(String message) {
        super(message);
    }
}
