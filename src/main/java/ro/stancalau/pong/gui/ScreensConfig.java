package ro.stancalau.pong.gui;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import ro.stancalau.pong.config.Constants;
import ro.stancalau.pong.model.LanguageModel;

@Configuration
@Lazy
public class ScreensConfig implements Observer{
	private static Logger logger = LogManager.getLogger(ScreensConfig.class);
	
	private Stage stage;
	private Scene scene;
	private LanguageModel lang;
	private StackPane root;	

	public void setPrimaryStage(Stage primaryStage) {
		this.stage = primaryStage;
	}

	public void setLangModel(LanguageModel lang) {
		if (this.lang!=null){
			this.lang.deleteObserver(this);
		}
		lang.addObserver(this);
		this.lang = lang;
	}

	public ResourceBundle getBundle() {
		return lang.getBundle();
	}

	public void showMainScreen() {
		root = new StackPane();
		root.getStylesheets().add(Constants.STYLE_FILE);
		root.getStyleClass().add("main-window");
		stage.setTitle("PongFX");
		scene = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
		stage.setScene(scene);
		stage.setResizable(false);

		stage.setOnHiding(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				System.exit(0); 
			}
		});
		stage.show();
	}

	private void setNode(Node node) {
		root.getChildren().setAll(node);
	}

	private void setNodeOnTop(Node node) {
		root.getChildren().add(node);
	}

	public void removeNode(Node node) {
		root.getChildren().remove(node);
	}

	void loadPlayGround() {
		Node playground = getNode(playGroundPresentation(), getClass().getResource("PlayGround.fxml"));
		((Pane) playground).setPrefSize(Constants.PLAY_GROUND_WIDTH, Constants.PLAY_GROUND_HEIGHT);
		((Pane) playground).setMaxSize(Constants.PLAY_GROUND_WIDTH, Constants.PLAY_GROUND_HEIGHT);
		
		setNode(playground);
	}

	@Bean
	@Scope("prototype")
	PlayGroundPresentation playGroundPresentation() {
		return new PlayGroundPresentation(this);
	}

	private Node getNode(final Presentation control, URL location) {
		FXMLLoader loader = new FXMLLoader(location, lang.getBundle());
		loader.setControllerFactory(new Callback<Class<?>, Object>() {
			public Object call(Class<?> aClass) {
				return control;
			}
		});
				
		try {
			return (Node) loader.load();
		} catch (Exception e) {
			logger.error("Error casting node", e);
			return null;
		}
	}

	public Stage getStage() {
		return stage;
	}

	public void update(Observable o, Object arg) {	
	}

}
