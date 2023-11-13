package dev.jasonlessenich.jlogic;

import dev.jasonlessenich.jlogic.objects.nodes.gates.loader.JGateManager;
import dev.jasonlessenich.jlogic.utils.Constants;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.nio.file.Path;

public class JLogicApplication extends Application {
	@Getter
	private static SceneManager sceneManager;
	@Getter
	private static JGateManager gateManager;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(@Nonnull Stage primaryStage) throws Exception {
		primaryStage.setTitle(Constants.APP_NAME);
		sceneManager = new SceneManager(primaryStage);
		gateManager = new JGateManager(Path.of("gates"));
		for (SceneManager.Type type : SceneManager.Type.values()) {
			type.init();
		}
		SceneManager.Type.MAIN.show();
	}
}