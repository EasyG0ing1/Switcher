package com.simtechdata.switcher.common;

import com.simtechdata.switcher.Switcher;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import static javafx.scene.layout.AnchorPane.*;
import static javafx.scene.layout.AnchorPane.setRightAnchor;

public class MainForm {

	public MainForm() {
		Switcher.addScene(C.MAIN_FORM, ap, width, height);
		makeControls();
		setControlActions();
	}

	private final double     width      = 400;
	private final double     height     = 300;
	private final AnchorPane ap         = ap();
	private final SecondForm secondForm = new SecondForm();
	private       Button     button;

	private AnchorPane ap() {
		AnchorPane newAP = new AnchorPane();
		newAP.setPrefWidth(this.width);
		newAP.setPrefHeight(this.height);
		return newAP;
	}


	private void makeControls() {
		Label label = new Label("Main Form");
		button = new Button("Second Form");
		ap.getChildren().addAll(label, button);
		Node control = ap.getChildren().get(ap.getChildren().indexOf(label));
		setNodePosition(control, 20,20,20,-1);
		control = ap.getChildren().get(ap.getChildren().indexOf(button));
		setNodePosition(control, 20,-1,55,-1);
	}

	private void setControlActions() {
		button.setOnAction(e -> Switcher.showScene(C.SECOND_FORM));
		Switcher.getStage().setOnCloseRequest(e-> System.exit(0));
	}


	private void setNodePosition(Node node, double left, double right, double top, double bottom) {
		if (top != -1) setTopAnchor(node, top);
		if (bottom != -1) setBottomAnchor(node, bottom);
		if (left != -1) setLeftAnchor(node, left);
		if (right != -1) setRightAnchor(node, right);
	}

	private void closeApp() {
		System.exit(0);
	}

	public void start() {
		Switcher.showScene(C.MAIN_FORM);
	}
}