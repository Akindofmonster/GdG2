package application;
	
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import java.util.*;


public class Main extends Application {
	
	Button play;
	Button sound;
	Button language;
	Button resolution;
	Button colors;
	Button speed;
	Button presets;
	Button information;
	Button exit;
	
	String lang = new String();
	final List<String> EN_CONTENT = Arrays.asList("resolution", "colors", "speed", "presets", "information", "exit");
	final List<String> DE_CONTENT = Arrays.asList("Auflösung", "Farben", "Geschwindigkeit", "Vorlagen", "Informationen", "Beenden");
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
			play = new Button();
			sound = new Button();
			language = new Button();
			resolution = new Button();
			colors = new Button();
			speed = new Button();
			presets = new Button();
			information = new Button();
			exit = new Button();
			
			if (lang.equals("en")){
				resolution.setText(EN_CONTENT.get(0));
				colors.setText(EN_CONTENT.get(1));
				speed.setText(EN_CONTENT.get(2));
				presets.setText(EN_CONTENT.get(3));
				information.setText(EN_CONTENT.get(4));
				exit.setText(EN_CONTENT.get(5));
			}else if (lang.equals("de")){
				resolution.setText(DE_CONTENT.get(0));
				colors.setText(DE_CONTENT.get(1));
				speed.setText(DE_CONTENT.get(2));
				presets.setText(DE_CONTENT.get(3));
				information.setText(DE_CONTENT.get(4));
				exit.setText(DE_CONTENT.get(5));
			}
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
