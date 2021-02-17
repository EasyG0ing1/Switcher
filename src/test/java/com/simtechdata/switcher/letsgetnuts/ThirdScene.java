package com.simtechdata.switcher.letsgetnuts;

import com.simtechdata.Switcher;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import static javafx.scene.layout.AnchorPane.*;
import static javafx.scene.layout.AnchorPane.setRightAnchor;

public class ThirdScene {

	public ThirdScene() {
		makeControls();
		setControlActions();
	}

	private final double     width      = 200;
	private final double     height     = 400;
	private final AnchorPane ap         = ap();
	private       Button     btnLastScene;
	private       Button     btnFirstScene;
	private       Button     btnSecondScene;
	private       Button     btnExit;

	private AnchorPane ap() {
		AnchorPane newAP = new AnchorPane();
		newAP.setPrefWidth(this.width);
		newAP.setPrefHeight(this.height);
		return newAP;
	}

	public AnchorPane getAnchorPane() {
		return ap;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	private void makeControls() {
		Label label = new Label("Third Scene");
		btnLastScene   = new Button("Previous Scene");
		btnFirstScene  = new Button("First Scene");
		btnSecondScene = new Button("Second Scene");
		btnExit        = new Button("Exit");
		ap.getChildren().addAll(label, btnLastScene, btnFirstScene, btnSecondScene, btnExit);
		Node control = ap.getChildren().get(ap.getChildren().indexOf(label));
		setNodePosition(control, 20,20,20,-1);
		control = ap.getChildren().get(ap.getChildren().indexOf(btnLastScene));
		setNodePosition(control, 20,-1,55,-1);
		control = ap.getChildren().get(ap.getChildren().indexOf(btnFirstScene));
		setNodePosition(control, 20,-1,90,-1);
		control = ap.getChildren().get(ap.getChildren().indexOf(btnSecondScene));
		setNodePosition(control, 20,-1,125,-1);
		control = ap.getChildren().get(ap.getChildren().indexOf(btnExit));
		setNodePosition(control, 20,-1,160,-1);
	}

	private void setControlActions() {
		btnLastScene.disableProperty().bind(Switcher.getEnabledWithHistoryProperty());
		btnLastScene.setOnAction(e->Switcher.showLastScene());
		btnFirstScene.setOnAction(e -> Switcher.showScene(C.FIRST_SCENE));
		btnSecondScene.setOnAction(e -> Switcher.showScene(C.SECOND_SCENE));
		btnExit.setOnAction(e-> closeApp());
	}

	private void setNodePosition(Node node, double left, double right, double top, double bottom) {
		if (top != -1) setTopAnchor(node, top);
		if (bottom != -1) setBottomAnchor(node, bottom);
		if (left != -1) setLeftAnchor(node, left);
		if (right != -1) setRightAnchor(node, right);
	}

	private void closeApp() {
		System.err.println("Exiting from Third Scene");
		System.exit(0);
	}
}
