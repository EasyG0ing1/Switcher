package com.simtechdata.switcher.onshown;

import com.simtechdata.Switcher;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Runnable JavaFX application to test the
 * {@code Switcher.setOnShown()} method.
 *
 * We register an {@code EventHandler} with a
 * {@code Scene} then print to the console when
 * the Scene is shown.
 */
public class TestOnAction extends Application {

    @Override
    public void start(Stage stage) {

        // Create simple window with a button and two possible Scene objects
        Button buttonChangeScene = new Button("Change Scene");
        BorderPane scene1 = new BorderPane(new VBox(10, new Label("Hello, World!"), buttonChangeScene));
        BorderPane scene2 = new BorderPane(new VBox(10, new Label("Goodbye, cruel World!")));

        Switcher.addScene(1, scene1, 400.0, 200.0);
        Switcher.addScene(2, scene2, 400.0, 200.0);

        // On button action, change to scene two
        buttonChangeScene.setOnAction(e -> Switcher.showScene(2));

        // Register an EventHandler with Switcher for Scene 2
        Switcher.setOnShown(2, e -> {
            System.out.println("Scene2 was shown.");
            Switcher.getDefaultStage().setTitle("Scene 2");
        });


        // Start off by showing Scene oen
        Switcher.showScene(1);
        Switcher.getDefaultStage().setTitle("Scene 1");

    }

    public static void main(String[] args) {
        Application.launch(TestOnAction.class, args);
    }
}
