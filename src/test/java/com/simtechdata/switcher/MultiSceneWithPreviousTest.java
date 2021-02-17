package com.simtechdata.switcher;

import com.simtechdata.Switcher;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MultiSceneWithPreviousTest extends Application {

	public final int    SCENE_ONE = 1;
	public final int    SCENE_TWO = 2;
	public final int    SCENE_THREE = 3;
	private final Map<Integer, HBox> buttonMap = new HashMap<>();
	private final Map<Integer,VBox> vboxMap = new HashMap<>();


	private void showSceneOne() {
		Switcher.showScene(SCENE_ONE);
 	}

	private void showSceneTwo() {
		Switcher.showScene(SCENE_TWO);
	}

	private void showSceneThree() {
		Switcher.showScene(SCENE_THREE);
	}

	private void showPrevious() {
		Switcher.showLastScene();
	}

	private final VBox vbox1  = new VBox();
	private final VBox vbox2  = new VBox();
	private final VBox vbox3  = new VBox();
	private final HBox hblbl1 = new HBox();
	private final HBox hblbl2 = new HBox();
	private final HBox hblbl3 = new HBox();
	private final HBox hbbtn1 = new HBox();
	private final HBox hbbtn2 = new HBox();
	private final HBox hbbtn3 = new HBox();
	private final HBox hbPic1 = new HBox();
	private final HBox hbPic2 = new HBox();
	private final HBox hbPic3 = new HBox();
	private ImageView ivPic1;
	private ImageView ivPic2;
	private ImageView ivPic3;



	@Override
	public void start(Stage primaryStage)  {
		double width = 300;
		double height = 250;
		loadPics();
		hblbl1.getChildren().add(new Label("Scene One"));
		hblbl2.getChildren().add(new Label("Scene Two"));
		hblbl3.getChildren().add(new Label("Scene Three"));
		hblbl1.setAlignment(Pos.CENTER);
		hblbl2.setAlignment(Pos.CENTER);
		hblbl3.setAlignment(Pos.CENTER);
		hbbtn1.setAlignment(Pos.CENTER);
		hbbtn2.setAlignment(Pos.CENTER);
		hbbtn3.setAlignment(Pos.CENTER);
		hbbtn1.setSpacing(5);
		hbbtn2.setSpacing(5);
		hbbtn3.setSpacing(5);
		buttonMap.put(SCENE_ONE, hbbtn1);
		buttonMap.put(SCENE_TWO, hbbtn2);
		buttonMap.put(SCENE_THREE, hbbtn3);
		vboxMap.put(SCENE_ONE,vbox1);
		vboxMap.put(SCENE_TWO,vbox2);
		vboxMap.put(SCENE_THREE,vbox3);
		buttonMap.get(SCENE_ONE).getChildren().setAll(buttonTwo(), buttonPrevious(), buttonThree());
		buttonMap.get(SCENE_TWO).getChildren().setAll(buttonOne(), buttonPrevious(), buttonThree());
		buttonMap.get(SCENE_THREE).getChildren().setAll(buttonOne(), buttonPrevious(), buttonTwo());
		vboxMap.get(SCENE_ONE).getChildren().setAll(hblbl1,hbPic1,buttonMap.get(SCENE_ONE));
		vboxMap.get(SCENE_TWO).getChildren().setAll(hblbl2,hbPic2,buttonMap.get(SCENE_TWO));
		vboxMap.get(SCENE_THREE).getChildren().setAll(hblbl3,hbPic3,buttonMap.get(SCENE_THREE));
		Switcher.addScene(SCENE_ONE, vboxMap.get(SCENE_ONE),width,height);
		Switcher.addScene(SCENE_TWO, vboxMap.get(SCENE_TWO),width,height);
		Switcher.addScene(SCENE_THREE, vboxMap.get(SCENE_THREE),width,height);
		Switcher.showScene(SCENE_ONE);
		Switcher.getDefaultStage().setOnCloseRequest(e-> System.exit(0));
		new Stage().initStyle(StageStyle.TRANSPARENT);
		new Stage().initModality(Modality.WINDOW_MODAL);
	}

	private void loadPics() {
		ClassLoader resource = ClassLoader.getSystemClassLoader();
		String pic1 = Objects.requireNonNull(resource.getResource("images/square.png")).toExternalForm();
		String pic2 = Objects.requireNonNull(resource.getResource("images/flag.png")).toExternalForm();
		String pic3 = Objects.requireNonNull(resource.getResource("images/umbrella.png")).toExternalForm();
		Image image1 = new Image(pic1);
		Image image2 = new Image(pic2);
		Image image3 = new Image(pic3);
		ivPic1 = new ImageView(image1);
		ivPic2 = new ImageView(image2);
		ivPic3 = new ImageView(image3);
		hbPic1.getChildren().add(ivPic1);
		hbPic2.getChildren().add(ivPic2);
		hbPic3.getChildren().add(ivPic3);
		hbPic1.setAlignment(Pos.CENTER);
		hbPic2.setAlignment(Pos.CENTER);
		hbPic3.setAlignment(Pos.CENTER);
		hbPic1.setPadding(new Insets(10,10,10,10));
		hbPic2.setPadding(new Insets(10,10,10,10));
		hbPic3.setPadding(new Insets(10,10,10,10));
	}
	
	private Button buttonPrevious() {
		Button button = new Button("Previous Scene");
		button.setOnAction(e-> showPrevious());

		// It is important to bind one of these properties to
		// whichever control you are going to use to engage
		// the showPrevious() getScene so that when there is no
		// history to show, your control is either hidden
		// or disabled.
		//button.visibleProperty().bind(Switcher.getHasHistoryProperty());
		button.disableProperty().bind(Switcher.getEnabledWithHistoryProperty());

		return button;
	}

	private Button buttonOne() {
		Button button = new Button("Scene One");
		button.setOnAction(e-> showSceneOne());
		return button;
	}

	private Button buttonTwo() {
		Button button = new Button("Scene Two");
		button.setOnAction(e-> showSceneTwo());
		return button;
	}

	private Button buttonThree() {
		Button button = new Button("Scene Three");
		button.setOnAction(e-> showSceneThree());
		return button;
	}

}
