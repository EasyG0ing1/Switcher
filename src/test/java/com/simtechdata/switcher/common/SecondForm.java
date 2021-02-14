package com.simtechdata.switcher.common;

import com.simtechdata.Switcher;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import static javafx.scene.layout.AnchorPane.*;
import static javafx.scene.layout.AnchorPane.setRightAnchor;

public class SecondForm {

	public SecondForm() {
		Switcher.addScene(C.SECOND_FORM, ap, width, height);
		makeControls();
		setControlActions();
	}

	private final double     width      = 200;
	private final double     height     = 400;
	private final AnchorPane ap         = ap();
	private       Button     button;

	private AnchorPane ap() {
		AnchorPane newAP = new AnchorPane();
		newAP.setPrefWidth(this.width);
		newAP.setPrefHeight(this.height);
		return newAP;
	}

	private void makeControls() {
		Label label = new Label("Second Form");
		button = new Button("Main Form");
		ap.getChildren().addAll(label, button);
		Node control = ap.getChildren().get(ap.getChildren().indexOf(label));
		setNodePosition(control, 20,20,20,-1);
		control = ap.getChildren().get(ap.getChildren().indexOf(button));
		setNodePosition(control, 20,-1,55,-1);
	}

	private void setControlActions() {
		button.setOnAction(e -> Switcher.showScene(C.MAIN_FORM));
	}


	private void setNodePosition(Node node, double left, double right, double top, double bottom) {
		if (top != -1) setTopAnchor(node, top);
		if (bottom != -1) setBottomAnchor(node, bottom);
		if (left != -1) setLeftAnchor(node, left);
		if (right != -1) setRightAnchor(node, right);
	}
}
