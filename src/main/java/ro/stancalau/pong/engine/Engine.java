package ro.stancalau.pong.engine;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.stancalau.pong.config.Constants;
import ro.stancalau.pong.model.*;

import java.util.Observable;

public class Engine extends Observable implements Runnable {

    private static Logger logger = LogManager.getLogger(Engine.class);
    private static final double SECOND_IN_MILIS = 1000d;
    private static final String GRADIENT_STRING = String.format("radial-gradient(center 50%% 50%%, radius 100%%, %s, %s)", Constants.BALL_COLOR_1, Constants.BALL_COLOR_2);

    private Circle ball;
    private Rectangle pad;
    private int framerate = Constants.FPS;
    private boolean running = false;

    private double speed = Constants.SPEED;

    private BallState ballState;
    private MouseState mouseState;
    private PadState padState;
    private PlayGroundState playGroundState;

    private Metrics metrics;

    public Engine(Pane pane) {
        playGroundState = new PlayGroundState(Constants.PLAY_GROUND_WIDTH, Constants.PLAY_GROUND_HEIGHT);
        ballState = new BallState(Constants.BALL_RADIUS);
        padState = new PadState(Constants.PAD_WIDTH, Constants.PAD_HEIGHT);
        mouseState = new MouseState();
        metrics = new Metrics();

        createBall(pane, ballState);
        createPad(pane, padState);

        bindPaneSizeProperties(pane);
    }

    private void bindPaneSizeProperties(final Pane pane) {
        ChangeListener<Number> listener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0,
                                Number arg1, Number arg2) {
                updateSizes(pane);
            }
        };

        pane.widthProperty().addListener(listener);
        pane.heightProperty().addListener(listener);

        updateSizes(pane);
    }

    private void updateSizes(Pane pane) {
        playGroundState.setWidth(pane.getWidth());
        playGroundState.setHeight(pane.getHeight());
        pad.setY(playGroundState.getHeight() - pad.getHeight());
    }

    private void createBall(Pane pane, BallState state) {
        ball = new Circle();
        ball.setRadius(state.getRadius());

        RadialGradient g = RadialGradient.valueOf(GRADIENT_STRING);
        ball.setFill(g);

        pane.getChildren().add(ball);
        moveBall(0);
    }

    private void createPad(Pane pane, PadState state) {
        pad = new Rectangle(state.getWidth(), state.getHeight());
        pad.setX(0);
        pad.setY(pane.getHeight() - state.getHeight());

        RadialGradient g = RadialGradient.valueOf(GRADIENT_STRING);
        pad.setFill(g);

        pane.getChildren().add(pad);
    }

    @Override
    public void run() {
        setRunning(true);

        long lastUpdateTime = System.currentTimeMillis() + 100;

        while (isRunning()) {
            double vector = getVector(lastUpdateTime);

            try {
                ballState.updatePositions(playGroundState, vector, padState, speed);
                moveBall(lastUpdateTime);
                lastUpdateTime = System.currentTimeMillis();
                Thread.sleep((long) (SECOND_IN_MILIS / (double) framerate));
            } catch (IllegalStateException e) {
                stop();
                ballState.reset();
            } catch (InterruptedException e) {
                logger.error("Could not sleep... maybe it's insomnia.", e);
            }
        }
    }

    private void moveBall(final long lastUpdateTime) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ball.setCenterX(ballState.getXPosition());
                ball.setCenterY(ballState.getYPosition());
                padState.updatePosition(mouseState.getCurrentDeltaX(), playGroundState.getWidth());
                pad.setX(padState.getX());
                metrics.setFps(1 / ((double) System.currentTimeMillis() - lastUpdateTime) * SECOND_IN_MILIS);
            }
        });
    }

    private double getVector(long lastUpdateTime) {
        long sinceLastPositionUpdate = System.currentTimeMillis() - lastUpdateTime;
        double distance = speed / SECOND_IN_MILIS * sinceLastPositionUpdate;
        return distance / Math.sqrt(Math.pow(ballState.getDeltaX(), 2) + Math.pow(ballState.getDeltaY(), 2)); // the distance is the hypotenuse but we need the cathetus
    }

    private void setRunning(boolean running) {
        if (running == this.running) return;
        this.running = running;
        setChanged();
        notifyObservers();
    }

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        setRunning(false);
    }

    public Metrics getMetrics() {
        return metrics;
    }
}
