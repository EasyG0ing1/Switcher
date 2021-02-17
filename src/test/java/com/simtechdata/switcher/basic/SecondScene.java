package com.simtechdata.switcher.basic;

import com.simtechdata.Switcher;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import static javafx.scene.layout.AnchorPane.*;
import static javafx.scene.layout.AnchorPane.setRightAnchor;

public class SecondScene {

	public SecondScene() {
		Switcher.addScene(C.SECOND_SCENE, ap, width, height);
		makeControls();
		setControlActions();
	}

	private final double     width      = 200;
	private final double     height     = 400;
	private final AnchorPane ap         = ap();
	private       Button     btnFirstScene;
	private       Button     btnExit;

	private AnchorPane ap() {
		AnchorPane newAP = new AnchorPane();
		newAP.setPrefWidth(this.width);
		newAP.setPrefHeight(this.height);
		return newAP;
	}

	private void makeControls() {
		Label label = new Label("Second Scene");
		btnFirstScene = new Button("First Scene");
		btnExit = new Button("Exit");
		ap.getChildren().addAll(label, btnFirstScene,btnExit);
		Node control = ap.getChildren().get(ap.getChildren().indexOf(label));
		setNodePosition(control, 20,20,20,-1);
		control = ap.getChildren().get(ap.getChildren().indexOf(btnFirstScene));
		setNodePosition(control, 20,-1,55,-1);
		control = ap.getChildren().get(ap.getChildren().indexOf(btnExit));
		setNodePosition(control, 120,-1,55,-1);
	}

	private void setControlActions() {
		btnFirstScene.setOnAction(e -> new FirstScene().start());
		btnExit.setOnAction(e-> System.exit(0));
	}

	private void setNodePosition(Node node, double left, double right, double top, double bottom) {
		if (top != -1) setTopAnchor(node, top);
		if (bottom != -1) setBottomAnchor(node, bottom);
		if (left != -1) setLeftAnchor(node, left);
		if (right != -1) setRightAnchor(node, right);
	}

	public void start() {
		Switcher.showScene(C.SECOND_SCENE);
	}
}
