package ro.stancalau.pong.engine;

import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.stancalau.pong.config.Constants;
import ro.stancalau.pong.model.BallState;
import ro.stancalau.pong.model.MouseState;
import ro.stancalau.pong.model.PadState;
import ro.stancalau.pong.model.PlayGroundState;

import java.util.Observable;
import java.util.Observer;

import static java.lang.System.currentTimeMillis;
import static javafx.animation.Animation.INDEFINITE;

public class Engine extends Observable implements Runnable {

    private static final Logger logger = LogManager.getLogger(Engine.class);
    private static final double SECOND_IN_MILLIS = 1000d;
    private static final String GRADIENT_STRING = String.format("radial-gradient(center 50%% 50%%, radius 100%%, %s, %s)", Constants.BALL_COLOR_1, Constants.BALL_COLOR_2);

    private final int framerate = Constants.FPS;
    private final double speed = Constants.SPEED;

    private final BallState ballState;
    private final MouseState mouseState;
    private final PadState padState;
    private final PlayGroundState playGroundState;
    private final Metrics metrics;

    private Group ball;
    private Rectangle pad;
    private boolean running = false;
    private RotateTransition rotateTransition;

    public Engine(Pane pane) {
        playGroundState = new PlayGroundState(Constants.PLAY_GROUND_WIDTH, Constants.PLAY_GROUND_HEIGHT);
        ballState = new BallState(playGroundState, Constants.BALL_RADIUS);
        padState = new PadState(Constants.PAD_WIDTH, Constants.PAD_HEIGHT);
        mouseState = new MouseState();
        metrics = new Metrics();

        createBall(pane, ballState);
        createPad(pane, padState);

        bindPaneSizeProperties(pane);
    }

    private void bindPaneSizeProperties(final Pane pane) {
        ChangeListener<Number> listener = (arg0, arg1, arg2) -> updateSizes(pane);

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
        Image image = new Image("egg.png", true);
        ImageView imageView = new ImageView(image);

        imageView.setFitHeight(2 * ballState.getRadius());
        imageView.setFitWidth(2 * ballState.getRadius());
        imageView.setX(-ballState.getRadius());
        imageView.setY(-ballState.getRadius());

        rotateTransition = new RotateTransition(Duration.millis(2000), imageView);
        rotateTransition.setByAngle(180f);
        rotateTransition.setCycleCount(INDEFINITE);
        rotateTransition.setAutoReverse(true);

        ball = new Group();
        ball.getChildren().add(imageView);

        pane.getChildren().add(ball);
        moveBall();
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
        long lastUpdateTime = currentTimeMillis();

        while (isRunning()) {
            double vector = getVector(lastUpdateTime);

            try {
                ballState.updatePositions(padState, vector);
                updateMetrics(lastUpdateTime);
                lastUpdateTime = currentTimeMillis();
                rotateTransition.play();
                Thread.sleep((long) (SECOND_IN_MILLIS / (double) framerate));
            } catch (IllegalBallPositionException e) {
                stop();
                ballState.reset();
                rotateTransition.stop();
            } catch (InterruptedException e) {
                logger.error("Could not sleep... maybe it's insomnia.", e);
            } finally {
                moveBall();
                movePad();
            }
        }
    }

    private void updateMetrics(final long lastUpdateTime) {
        Platform.runLater(() -> {
            final double fps = 1 / ((double) currentTimeMillis() - lastUpdateTime) * SECOND_IN_MILLIS;
            metrics.setFps(Math.min(framerate, fps)); // minimum because the first cycle tends to give off huge unrealistic values
        });
    }

    private void moveBall() {
        Platform.runLater(() -> {
            ball.setLayoutX(ballState.getXPosition());
            ball.setLayoutY(ballState.getYPosition());
        });
    }

    private void movePad() {
        Platform.runLater(() -> {
            padState.updatePosition(mouseState.getCurrentDeltaX(), playGroundState.getWidth());
            pad.setX(padState.getX());
        });
    }

    private double getVector(long lastUpdateTime) {
        long sinceLastPositionUpdate = currentTimeMillis() - lastUpdateTime;
        double hypotenuse = speed / SECOND_IN_MILLIS * sinceLastPositionUpdate;
        return hypotenuse / Math.sqrt(Math.pow(ballState.getDeltaX(), 2) + Math.pow(ballState.getDeltaY(), 2));
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

    public void addMetricsObserver(Observer observer) {
        metrics.addObserver(observer);
    }
}
