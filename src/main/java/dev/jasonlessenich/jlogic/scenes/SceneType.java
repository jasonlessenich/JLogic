package dev.jasonlessenich.jlogic.scenes;

import dev.jasonlessenich.jlogic.JLogicApplication;
import dev.jasonlessenich.jlogic.utils.Constants;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public enum SceneType {
	MAIN("/views/main.fxml");

	private final String path;

	SceneType(String path) {
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
