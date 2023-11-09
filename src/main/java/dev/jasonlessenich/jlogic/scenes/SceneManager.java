package dev.jasonlessenich.jlogic.scenes;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages scenes for the application.
 */
public class SceneManager {
	private final Map<SceneType, Scene> sceneMap = new HashMap<>();
	private final Stage primaryStage;

	public SceneManager(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void addScene(SceneType name, Scene pane) {
		sceneMap.put(name, pane);
	}

	public Scene removeScene(SceneType name) {
		return sceneMap.remove(name);
	}

	public void show(SceneType name) {
		primaryStage.setScene(sceneMap.get(name));
		primaryStage.show();
	}
}