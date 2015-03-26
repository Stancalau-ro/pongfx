package ro.stancalau.pong.engine;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class MetricsTest {

    private Metrics metrics;
    private static final double FPS = 123d;

    @Before
    public void setUp() throws Exception {
        metrics = new Metrics();
        metrics.setFps(FPS);
    }

    @Test
    public void testFirstValidSet()
    {
        assertEquals(FPS, metrics.getFps());
        assertEquals(FPS, metrics.getMaxFps());
        assertEquals(FPS, metrics.getMinFps());
    }

    @Test
    public void testDefaultState()
    {
        Metrics metrics = new Metrics();
        assertEquals(0d, metrics.getFps());
        assertEquals(Double.MIN_VALUE, metrics.getMaxFps());
        assertEquals(Double.MAX_VALUE, metrics.getMinFps());
    }

    @Test
    public void testInvalidValuesForFps()
    {
        testNoChange(FPS);
        testNoChange(-1);
        testNoChange(Double.POSITIVE_INFINITY);
        testNoChange(Double.NEGATIVE_INFINITY);
    }

    @Test
    public void testLowerMin()
    {
        double lowerValue = FPS - 1;

        metrics.setFps(lowerValue);
        assertEquals(lowerValue, metrics.getFps());
        assertEquals(FPS, metrics.getMaxFps());
        assertEquals(lowerValue, metrics.getMinFps());
    }

    @Test
    public void testHigherMax()
    {
        double higherValue = FPS + 1;

        metrics.setFps(higherValue);
        assertEquals(higherValue, metrics.getFps());
        assertEquals(higherValue, metrics.getMaxFps());
        assertEquals(FPS, metrics.getMinFps());
    }

    private void testNoChange(double value){
        metrics.setFps(value);
        assertEquals(FPS, metrics.getFps());
        assertEquals(FPS, metrics.getMaxFps());
        assertEquals(FPS, metrics.getMinFps());
    }

}