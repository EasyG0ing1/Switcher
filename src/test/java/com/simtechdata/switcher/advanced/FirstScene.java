package com.simtechdata.switcher.advanced;

import com.simtechdata.Switcher;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import static javafx.scene.layout.AnchorPane.*;

public class FirstScene {

	public FirstScene() {
		Switcher.addScene(C.FIRST_SCENE, ap, width, height);
		makeControls();
		setControlActions();
	}

	private final double      width      = 400;
	private final double      height     = 300;
	private final AnchorPane  ap         = ap();
	private       Button      btnSecondForm;
	private       Button      btnExit;

	private AnchorPane ap() {
		AnchorPane newAP = new AnchorPane();
		newAP.setPrefWidth(this.width);
		newAP.setPrefHeight(this.height);
		return newAP;
	}

	private void makeControls() {
		Label label = new Label("First Scene");
		btnSecondForm = new Button("Second Scene");
		btnExit = new Button("Exit");
		ap.getChildren().addAll(label, btnSecondForm,btnExit);
		Node control = ap.getChildren().get(ap.getChildren().indexOf(label));
		setNodePosition(control, 20,20,20,-1);
		control = ap.getChildren().get(ap.getChildren().indexOf(btnSecondForm));
		setNodePosition(control, 20,-1,55,-1);
		control = ap.getChildren().get(ap.getChildren().indexOf(btnExit));
		setNodePosition(control, 125,-1,55,-1);
	}

	private void setControlActions() {
		btnSecondForm.setOnAction(e -> Switcher.showScene(C.SECOND_SCENE));
		btnExit.setOnAction(e-> closeApp());
	}

	private void closeApp() {
		System.err.println("Exiting from First Scene");
		System.exit(0);
	}

	private void setNodePosition(Node node, double left, double right, double top, double bottom) {
		if (top != -1) setTopAnchor(node, top);
		if (bottom != -1) setBottomAnchor(node, bottom);
		if (left != -1) setLeftAnchor(node, left);
		if (right != -1) setRightAnchor(node, right);
	}
}