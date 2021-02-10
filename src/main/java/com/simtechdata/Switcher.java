package com.simtechdata;


import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Switcher {

	private static final BooleanProperty                 stageVisibleProperty         = new SimpleBooleanProperty(true);
	private static final BooleanProperty                 hideStageOnLostFocusProperty = new SimpleBooleanProperty();
	private static final BooleanProperty                 hasHistoryProperty           = new SimpleBooleanProperty();
	private static final BooleanProperty                 noHistoryProperty        	  = new SimpleBooleanProperty();
	private static final Map<Integer, SceneObject>       sceneObjectMap               = new HashMap<>();
	private static       boolean                         started                      = false;
	private static final HistoryKeeper                   history                      = new HistoryKeeper();
	private static       Stage                           stage;

	private static void start() {
		Switcher.stage = new Stage();
		stageVisibleProperty.addListener((observable, oldValue, newValue) -> {
			if (newValue && !oldValue) show();
			if (!newValue && oldValue) hide();
		});
		hideStageOnLostFocusProperty.addListener((observable, oldValue, newValue) -> {
			if (newValue && !oldValue) {
				Thread monitor = new Thread(() -> {
					boolean stageFocused;
					while (Switcher.hideStageOnLostFocusProperty.getValue().equals(true)) {
						stageFocused = stage.isFocused();
						while (stageFocused && Switcher.hideStageOnLostFocusProperty.getValue().equals(true)) {
							stageFocused = stage.isFocused();
							sleep(50);
						}
						stageVisibleProperty.setValue(stageFocused);
						sleep(100);
					}
				});
				monitor.setDaemon(true);
				monitor.start();
			}
		});
		Switcher.started = true;
	}

	public static void removeScene(Integer sceneID) {
		if (sceneObjectMap.containsKey(sceneID)) sceneObjectMap.remove(sceneID);
		else warnNoScene("removeScene",sceneID);
	}

	public static void addScene(Integer sceneID, Parent root, double width, double height) {
		addSceneFinally(root, width, height, sceneID, null, null);
	}

	public static void addScene(Integer sceneID, Parent root, double width, double height, StageStyle stageStyle) {
		addSceneFinally(root, width, height, sceneID, stageStyle, null);
	}

	public static void addScene(Integer sceneID, Parent root, double width, double height, Modality stageModality) {
		addSceneFinally(root, width, height, sceneID, null, stageModality);
	}

	public static void addScene(Integer sceneID, Parent root, double width, double height, StageStyle stageStyle, Modality stageModality) {
		addSceneFinally(root, width, height, sceneID, stageStyle, stageModality);
	}

	private static void addSceneFinally(Parent parent, double width, double height, Integer sceneID, StageStyle stageStyle, Modality stageModality) {
		if (!Switcher.started) start();
		sceneObjectMap.put(sceneID, new SceneObject(parent, width, height, stageStyle, stageModality));
	}

	public static void setHideSceneOnLostFocus(boolean hideOnLostFocus) {Switcher.hideStageOnLostFocusProperty.setValue(hideOnLostFocus);}

	public static boolean sceneHiddenOnLostFocus()                      {return Switcher.hideStageOnLostFocusProperty.getValue();}

	public static void setSceneVisible(boolean visible)                 {Switcher.stageVisibleProperty.setValue(visible);}

	public static boolean sceneVisible()                                {return Switcher.stageVisibleProperty.getValue();}

	public static void showScene(Integer sceneID) {
		if (sceneObjectMap.containsKey(sceneID)) showSceneObject(sceneID, false);
		else warnNoScene("showScene",sceneID);
	}

	public static void showScene(Integer sceneID, double stageX, double stageY) {
		if (sceneObjectMap.containsKey(sceneID)) {
			SceneObject sceneObject = sceneObjectMap.get(sceneID);
			sceneObject.setStageX(stageX);
			sceneObject.setStageY(stageY);
			showSceneObject(sceneID,false);
		}
		else warnNoScene("showScene",sceneID);
	}

	public static void showScene(Integer sceneID, double width, double height, double stageX, double stageY) {
		if (sceneObjectMap.containsKey(sceneID)) {
			SceneObject sceneObject = sceneObjectMap.get(sceneID);
			sceneObject.setWidth(width);
			sceneObject.setHeight(height);
			sceneObject.setStageX(stageX);
			sceneObject.setStageY(stageY);
			showSceneObject(sceneID,false);
		}
		else warnNoScene("showScene",sceneID);
	}

	/**
	 * Use the next two methods to get the property you need
	 * to bind to whichever control you use to call the
	 * showPrevious() method.
	 * use hasHistoryProperty to bind to the controls visibleProperty or
	 * use noHistoryProperty to bind to the controls disableProperty if
	 * you just want the control to be disabled if there is no history
	 * @return
	 */
	public static BooleanProperty getHasHistoryProperty() {
		return hasHistoryProperty;
	}

	public static BooleanProperty getNoHistoryProperty() { return noHistoryProperty; }

	public static void showLastScene() {
		Integer lastSceneID = history.getLastSceneID();
		System.out.println("LastSceneID: " + lastSceneID);
		showSceneObject(lastSceneID,true);
	}

	private static void showSceneObject(Integer sceneID, boolean showingPrevious) {
		SceneObject sceneObject = sceneObjectMap.get(sceneID);
		if (!showingPrevious){
			history.showingNewScene(sceneID);
		}
		hasHistoryProperty.setValue(history.hasHistory());
		noHistoryProperty.setValue(!history.hasHistory());
		final Scene  scene       = sceneObject.scene();
		final double stageX      = sceneObject.getStageX();
		final double stageY      = sceneObject.getStageY();
		final double stageWidth  = sceneObject.getWidth();
		final double stageHeight = sceneObject.getHeight();
		stage.setWidth(stageWidth);
		stage.setHeight(stageHeight);
		final StageStyle stageStyle    = sceneObject.stageStyle();
		final Modality   stageModality = sceneObject.stageModality();
		stage.setScene(scene);
		Platform.runLater(() -> {
			stage.show();
			Rectangle2D  screenBounds = Screen.getPrimary().getVisualBounds();
			double       screenWidth  = screenBounds.getWidth();
			double       screenHeight = screenBounds.getHeight();
			final double finalX       = (stageX > -1 && stageX <= screenWidth) ? stageX : (screenWidth - (stage.getWidth() / 2));
			final double finalY       = (stageY > -1 && stageY <= screenHeight) ? stageY : (screenHeight - (stage.getHeight() / 2));
			if (stageStyle != null) stage.initStyle(stageStyle);
			if (stageModality != null) stage.initModality(stageModality);
			sceneObject.setStageX(finalX);
			sceneObject.setStageY(finalY);
			stage.toFront();
			stage.requestFocus();
		});
	}

	public static void setParent(Integer sceneID, Parent parent) {
		if (sceneObjectMap.containsKey(sceneID)) {
			sceneObjectMap.get(sceneID).scene().setRoot(parent);
		}
		else warnNoScene("setParent",sceneID);
	}

	public static Stage getStage() {return stage;}

	private static void hide() {
		Platform.runLater(() -> {
			if (Switcher.stage != null) Switcher.stage.hide();
		});
	}

	private static void show() {
		Platform.runLater(() -> {
			if (Switcher.stage != null) {
				Switcher.stage.show();
				Switcher.stage.requestFocus();
			}
		});
	}

	private static void warnNoScene(String callingMethod, Integer sceneID) {
		System.err.println("sceneID " + sceneID + " does not exist being called from method " + callingMethod);
	}

	private static void sleep(long milliseconds) {
		try {TimeUnit.MILLISECONDS.sleep(milliseconds);}catch (InterruptedException e) {e.printStackTrace();}
	}

}

	/**
 * SceneObject is a Class that contains all
 * of the relevant information about a
 * scene so that the showScene methods
 * can properly configure the stage and
 * the scene for display
 */
	class SceneObject extends Switcher {

		SceneObject(Parent parent, double width, double height, StageStyle stageStyle, Modality stageModality) {
			this.parent        = parent;
			this.width         = width;
			this.height        = height;
			this.stageStyle    = stageStyle;
			this.stageModality = stageModality;
			this.scene         = new Scene(this.parent);
		}

		private final Scene      scene;
		private final Parent     parent;
		private final StageStyle stageStyle;
		private final Modality   stageModality;
		private double     width;
		private double     height;
		private       double     stageX = -1;
		private       double     stageY = -1;

		public StageStyle stageStyle()  {return stageStyle;}

		public Modality stageModality() {return stageModality;}

		/**
		 * Get the scene that is currently showing
		 * to change properties etc.
		 * @return
		 */
		public Scene scene()            {return scene;}

		public double getWidth()             {return width;} // Returns width of stage

		public double getHeight()            {return height;} // Returns height of stage

		public double getStageX()            {return stageX;} // Returns the top left corner of stage as X coordinate

		public double getStageY()            {return stageY;} // Returns the top left corner of stage as Y coordinate

		/**
		 * With these two settings, you can position the stage anywhere on the screen
		 * @param stageX
		 */
		public void setStageX(double stageX) {this.stageX = stageX;}

		public void setStageY(double stageY) {this.stageY = stageY;}

		public void setWidth(double width) {
			this.width = width;
		}

		public void setHeight(double height) {
			this.height = height;
		}

	}

	/**
	 * This class simply maintains the history of
	 * scenes as they are shown so that you can
	 * easily go back to the previously shown
	 * scene if desired
	 */
	class HistoryKeeper {

		public HistoryKeeper() {
			index = 0;
		}

		private       Integer               lastSceneID  = null;
		private       int                   index;
		private final Map<Integer, Integer> pastSceneIDs = new HashMap<>();

		private void addLastSceneID() {
			if (lastSceneID != null) {
				pastSceneIDs.put(index, this.lastSceneID);
			}
		}

		public Integer getLastSceneID() {
			if (index > 0) {
				index--;
				lastSceneID = pastSceneIDs.get(index);
				pastSceneIDs.remove(index);
			}
			return lastSceneID;
		}

		public void showingNewScene(Integer newSceneID) {
			addLastSceneID();
			this.lastSceneID = newSceneID;
			index++;
		}

		public boolean hasHistory() {
			return pastSceneIDs.size() > 0;
		}

	}