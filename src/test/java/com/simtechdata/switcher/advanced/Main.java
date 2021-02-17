package com.simtechdata.switcher.advanced;

import com.simtechdata.Switcher;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.Objects;

public class Main extends Application {

	/**
	 * In this implementation of Switcher, we first instantiate
	 * each Scenes associated class, and within each of those
	 * classes, they add their respective Scenes to Switcher.
	 * Then all that has to be done is issue a showScene(sceneID)
	 * from any class in the application to bring up that Scene.
	 *
	 * Also notice that we have set the default Stages OnCloseRequest
	 * to use the closeApp method in this class. The OnCloseRequest
	 * is invoked when you close the app using the close handle
	 * on the Window, or you hit CMD+Q (Mac) or ALT+F4 (Windows)
	 *
	 * Experiment with different ways to close the app, using the
	 * above mentioned ways, or by using each Scenes Exit button
	 * then in the console output you will see which method was
	 * actually used to close the app.
	 *
	 * @param primaryStage
	 * @throws Exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		Switcher.configureDefaultStage(StageStyle.DECORATED,null);
		Objects.requireNonNull(Switcher.getDefaultStage()).setOnCloseRequest(e->closeApp());
		new FirstScene();
		new SecondScene();
		Switcher.showScene(C.FIRST_SCENE);
	}

	private void closeApp() {
		System.err.println("Exiting from Main");
		System.exit(0);
	}
}
