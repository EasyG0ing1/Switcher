package com.simtechdata.switcher.basic;

import com.simtechdata.Switcher;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import static javafx.scene.layout.AnchorPane.*;
import static javafx.scene.layout.AnchorPane.setRightAnchor;

public class FirstScene {

	public FirstScene() {
		Switcher.addScene(C.FIRST_SCENE, ap, width, height);
		makeControls();
		setControlActions();
	}

	private final double     width  = 400;
	private final double     height = 300;
	private final AnchorPane ap     = ap();
	private       Button     btnSecondScene;
	private       Button     btnExit;

	private AnchorPane ap() {
		AnchorPane newAP = new AnchorPane();
		newAP.setPrefWidth(this.width);
		newAP.setPrefHeight(this.height);
		return newAP;
	}

	private void makeControls() {
		Label label = new Label("First Scene");
		btnSecondScene = new Button("Second Scene");
		btnExit = new Button("Exit");
		ap.getChildren().addAll(label, btnSecondScene,btnExit);
		Node control = ap.getChildren().get(ap.getChildren().indexOf(label));
		setNodePosition(control, 20, 20, 20, -1);
		control = ap.getChildren().get(ap.getChildren().indexOf(btnSecondScene));
		setNodePosition(control, 20, -1, 55, -1);
		control = ap.getChildren().get(ap.getChildren().indexOf(btnExit));
		setNodePosition(control, 120, -1, 55, -1);
	}

	private void setControlActions() {
		btnSecondScene.setOnAction(e -> new SecondScene().start());
		btnExit.setOnAction(e-> System.exit(0));
	}

	private void setNodePosition(Node node, double left, double right, double top, double bottom) {
		if (top != -1) setTopAnchor(node, top);
		if (bottom != -1) setBottomAnchor(node, bottom);
		if (left != -1) setLeftAnchor(node, left);
		if (right != -1) setRightAnchor(node, right);
	}

	public void start() {
		Switcher.showScene(C.FIRST_SCENE);
	}
}