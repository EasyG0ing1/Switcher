package com.simtechdata.switcher.basic;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	/**
	 * This package is what I consider to be the most basic way
	 * to use Switcher.
	 *
	 * One of the problems with using Switcher like this is that
	 * you create a new instance of each scenes class when you
	 * switch to the other Scene. Which might be just fine.
	 * However, See the Advanced Test package to understand how
	 * to use Switcher while allowing each Scenes class to remain
	 * instantiated.
	 *
	 * Notice we keep the ID for each scene in the C class which
	 * holds constants that are visible from any class.
	 *
	 * @param primaryStage
	 * @throws Exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		new FirstScene().start();
	}
}
