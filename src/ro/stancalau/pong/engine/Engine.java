package ro.stancalau.pong.engine;

import java.awt.MouseInfo;
import java.awt.Point;
import java.util.Observable;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import ro.stancalau.pong.config.Constants;

public class Engine extends Observable implements Runnable {

	private Pane pane;
	private Circle ball;
	private Rectangle pad;
	private int radius = Constants.BALL_RADIUS;
	private int framerate = Constants.FPS;
	private boolean running = false;


	private double dX=1;
	private double dY=1;
	private double cX;
	private double cY;
	private double width;
	private double height;
	private double speed = 400; 
	
	private double padX;
	
	private Metrics metrics;
	
	public Engine(Pane pane){
		this.pane = pane;
		
		createBall();
		createPad();
		
		ChangeListener<Number> listener = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0,
					Number arg1, Number arg2) {
				resized();				
			}
		};
		
		pane.widthProperty().addListener(listener);		
		pane.heightProperty().addListener(listener);

		resized();

		metrics = new Metrics();
	}
	
	private void resized() {
		width = pane.getWidth();
		height = pane.getHeight();
		pad.setY(height-pad.getHeight());
	}

	private void createBall() {
		ball = new Circle();
		ball.setCenterX(radius*2);
		ball.setCenterY(radius*2);
		ball.setRadius(radius);
		
		RadialGradient g = RadialGradient.valueOf("radial-gradient(center 50% 50%, radius 100%, "+Constants.BALL_COLOR_1+", "+Constants.BALL_COLOR_2+")");
		ball.setFill(g);
		
		pane.getChildren().add(ball);
	}
	
	private void createPad() {
		pad = new Rectangle(Constants.PAD_WIDTH, Constants.PAD_HEIGHT);		
		pad.setX(0);
		
		RadialGradient g = RadialGradient.valueOf("radial-gradient(center 50% 50%, radius 100%, "+Constants.BALL_COLOR_1+", "+Constants.BALL_COLOR_2+")");
		pad.setFill(g);
		
		pane.getChildren().add(pad);
	}
	
	@Override
	public void run() {
		setRunning(true);
		
		long lastRun = System.currentTimeMillis()+100;
		long since = System.currentTimeMillis();
		Point mousePos = MouseInfo.getPointerInfo().getLocation();
		
		cX = ball.getCenterX();
		cY = ball.getCenterY();
		
		while (isRunning()){				
			
			since = System.currentTimeMillis()-lastRun;
			double delta = speed/1000d*since;
				
			double alpha = delta / Math.sqrt(dX*dX+dY*dY);
			
			cX += alpha*dX;
			cY += alpha*dY;				
			
			if (cX <= radius){
				dX = Math.abs(dX);
			}
			if (cX >= width-radius){
				dX = -Math.abs(dX);
			}
			if (cY <= radius){
				dY = Math.abs(dY);
			}
			if (cY >= height-radius){		
				double col = collisionTest();
				if (Math.abs(col)>1) { 
					setRunning(false);
					reset();
				}else{
					dX = col*3;
					dY = -Math.abs(dY);
				}
			}
			
			Point p = MouseInfo.getPointerInfo().getLocation();
			
			double newX = cX;
			double newY = cY;
			long lastRunFinal = lastRun;
			int mouseDelta = p.x-mousePos.x;
			
			Platform.runLater(new Runnable() {
			      @Override public void run() {
			    	  ball.setCenterX(newX);
					  ball.setCenterY(newY);    
					  setPadPosition(mouseDelta);
					  metrics.setFps(1/((double)System.currentTimeMillis()-lastRunFinal)*1000 );
			      }
			});
			
			lastRun = System.currentTimeMillis();
			mousePos = p;
			
			try {
				Thread.sleep((long) (1000d/(double)framerate));
			} catch (InterruptedException e) {
			}
		}		
	}
	
	private void reset() {
		cX = ball.getRadius();
		cY = ball.getRadius();
		dX = 1;
		dY = 1;
	}

	/**
	 * @return Percentage of hit point deviation from center. Perfect center hit returns 0.
	 * Full right returns 1.
	 * Full left returns -1. 
	 * Also returns a value whose ABS is larger then 1 if the collision did not happen.
	 */
	private double collisionTest() {
		int w = (int) pad.getWidth();
		return (cX - (padX+w/2) )/ (w/2) ;
	}

	private void setPadPosition(int delta) {
		padX+=delta;
		if (padX<0) padX=0;
		if (padX>width-pad.getWidth()) padX=width-pad.getWidth();
		
		pad.setX(padX);
	}

	public void setRunning(boolean running) {
		if (running== this.running) return;
		this.running = running;
		setChanged();
		notifyObservers();
	}
	
	public boolean isRunning() {
		return running;
	}

	public void stop(){
		setRunning(false);
	}

	public Metrics getMetrics() {
		return metrics;
	}


}
