package ro.stancalau.pong.gui;

import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import ro.stancalau.pong.engine.Engine;
import ro.stancalau.pong.engine.Metrics;

public class PlayGroundPresentation extends Presentation {

	public PlayGroundPresentation(ScreensConfig config) {
		super(config);
	}
	
	Engine engine;

	@FXML
	StackPane root;
	@FXML
	Pane pane;
	@FXML
	Label fpsLabel, fpsLabelMin, fpsLabelMax;
	@FXML
	Button startButton;
	
	DecimalFormat df = new DecimalFormat("##.#");
	
	@FXML
	public void onPressStart(ActionEvent event){
		if (!engine.isRunning()){
			start();
			startButton.setText("stop");
		}else {
			stop();
			startButton.setText("start");
		}
	}	
	
	@FXML
	void initialize() {		
		engine = new Engine(pane);		
		engine.getMetrics().addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				fpsLabel.setText( df.format( ((Metrics)o).getFps()));
				fpsLabelMin.setText( df.format( ((Metrics)o).getMinFps()));
				fpsLabelMax.setText( df.format( ((Metrics)o).getMaxFps()));
			}
		});
	}
	
	private void start(){
		new Thread(engine).start();
	}
	
	private void stop(){
		engine.stop();
	}

}
