package dev.jasonlessenich.jlogic;

import dev.jasonlessenich.jlogic.utils.Constants;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages scenes for the application.
 */
public class SceneManager {
	private final Map<Type, Scene> sceneMap = new HashMap<>();
	@Getter
	private final Stage primaryStage;

	public SceneManager(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void addScene(Type name, Scene pane) {
		sceneMap.put(name, pane);
	}

	public Scene removeScene(Type name) {
		return sceneMap.remove(name);
	}

	public void show(Type name) {
		primaryStage.setScene(sceneMap.get(name));
		primaryStage.show();
	}

	public enum Type {
		MAIN("/views/main.fxml");

		private final String path;

		Type(String path) {
			this.path = path;
		}

		public void init() throws IOException {
			final SceneManager manager = JLogicApplication.getSceneManager();
			final FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
			manager.addScene(this, new Scene(loader.load(), Constants.STAGE_WIDTH, Constants.STAGE_HEIGHT));
		}

		public void show() {
			JLogicApplication.getSceneManager().show(this);
		}
	}
}