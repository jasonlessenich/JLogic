package dev.jasonlessenich.jlogic;

import dev.jasonlessenich.jlogic.scenes.SceneManager;
import dev.jasonlessenich.jlogic.scenes.SceneType;
import dev.jasonlessenich.jlogic.utils.Constants;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.annotation.Nonnull;

public class JLogicApplication extends Application {
    private static SceneManager sceneManager;

    public static void main(String[] args) {
        launch(args);
    }

    public static SceneManager getSceneManager() {
        return sceneManager;
    }

    @Override
    public void start(@Nonnull Stage primaryStage) throws Exception {
        primaryStage.setTitle(Constants.APP_NAME);
        sceneManager = new SceneManager(primaryStage);
        for (SceneType type : SceneType.values()) {
            type.init();
        }
        SceneType.MAIN.show();
    }
}