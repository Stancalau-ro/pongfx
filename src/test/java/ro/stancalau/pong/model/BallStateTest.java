package ro.stancalau.pong.model;

import org.junit.Before;
import org.junit.Test;
import ro.stancalau.pong.engine.IllegalBallPositionException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class BallStateTest {

    private static final double WIDTH = 1000;
    private static final double HEIGHT = 500;
    private static final double PAD_WIDTH = 100;
    private static final double PAD_HEIGHT = 5;
    private static final double RADIUS = 10;

    private BallState state;

    @Before
    public void setUp() throws Exception {
        PlayGroundState playGround = new PlayGroundState(WIDTH, HEIGHT);
        PadState pad = new PadState(PAD_WIDTH, PAD_HEIGHT);
        state = new BallState(playGround, RADIUS);
    }

    @Test
    public void testPositionBounds() throws IllegalBallPositionException {
        PlayGroundState playGround = new PlayGroundState(WIDTH, HEIGHT);
        BallState state = new BallState(playGround, RADIUS);
        PadState fullWidthPad = new PadState(WIDTH, PAD_HEIGHT);

        int iterationsToGo = 10000000;
        do{
            double randomVector = Math.random() * 10;
            state.updatePositions(fullWidthPad, randomVector);
            assertPositionsAreValid(playGround, state);

            iterationsToGo--;
        }while (iterationsToGo > 0);
    }

    private static void assertPositionsAreValid(PlayGroundState playGround, BallState state){
        double radius = state.getRadius();
        assertTrue( state.getXPosition() >= radius );
        assertTrue( state.getYPosition() >= radius );
        assertTrue( state.getXPosition() <= playGround.getWidth() - radius );
        assertTrue( state.getYPosition() <= playGround.getHeight() - radius );
    }

    @Test
    public void testDefaultValues()
    {
        assertEquals(RADIUS, state.getXPosition());
        assertEquals(RADIUS, state.getYPosition());
        assertEquals(1d, state.getDeltaX());
        assertEquals(1d, state.getDeltaY());
    }

}