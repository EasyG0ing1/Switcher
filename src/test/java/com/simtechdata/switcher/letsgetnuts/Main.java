package com.simtechdata.switcher.letsgetnuts;

import com.simtechdata.Switcher;
import javafx.application.Application;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class Main extends Application {
	/**
	 * This package shows how you could build Switcher with all of your scenes
	 * in a single class while your scenes structures are in their own individual
	 * classes, then call up any scene from any class with a single line of code.
	 *
	 * Notice How the Previous Stage buttons behave when there are no stages in
	 * the history to show. To see this clearly, ,change the last line in the start
	 * method in this class to initially show one of the other Scenes.
	 *
	 * Also notice that we have a different stage assigned to each Scene,
	 * Each with different StageStyles and Modality
	 *
	 * @param primaryStage usually the primary Stage from your first Application method
	 */
	@Override
	public void start(Stage primaryStage) {
		FirstScene  ms = new FirstScene();
		SecondScene ss = new SecondScene();
		ThirdScene  ts = new ThirdScene();
		Switcher.addScene(C.FIRST_SCENE, ms.getAnchorPane(), ms.getWidth(), ms.getHeight());
		Switcher.addScene(C.SECOND_SCENE, C.STAGE_TRANSPARENT, ss.getAnchorPane(), ss.getWidth(), ss.getHeight(), StageStyle.TRANSPARENT);
		Switcher.addScene(C.THIRD_SCENE, C.STAGE_DECORATED_WINDOW_MODAL, ts.getAnchorPane(), ts.getWidth(), ts.getHeight(), StageStyle.DECORATED, Modality.WINDOW_MODAL);

		/*
		 * Notice we can just use our StageID to pull up the given stage and set its onCloseRequest
		 */
		Objects.requireNonNull(Switcher.getStage(C.STAGE_TRANSPARENT)).setOnCloseRequest(e->closeApp());
		Objects.requireNonNull(Switcher.getStage(C.STAGE_DECORATED_WINDOW_MODAL)).setOnCloseRequest(e->closeApp());
		Objects.requireNonNull(Switcher.getDefaultStage()).setOnCloseRequest(e->closeApp());

		/*
		 * We are going to show the first scene closer to the upper left of the screen so we can
		 * see that subsequent calls to show that scene will maintain that anchor point.
		 * Change out the scene that is first displayed to see this in action fully.
		 */
		Switcher.showScene(C.SECOND_SCENE, 100, 200);
	}


	private void closeApp() {
		System.err.println("Exiting from main");
		System.exit(0);
	}
}
