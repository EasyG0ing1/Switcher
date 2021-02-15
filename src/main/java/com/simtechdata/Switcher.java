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

/**
 * Switcher is a library that makes managing your scenes literally one line of code easy!
 * It all starts with the addScene() method.
 */
public class Switcher {

	private static final BooleanProperty           stageVisibleProperty         = new SimpleBooleanProperty(true);
	private static final BooleanProperty           hideStageOnLostFocusProperty = new SimpleBooleanProperty();
	private static final BooleanProperty           visibleWithHistoryProperty   = new SimpleBooleanProperty();
	private static final BooleanProperty           enabledWithHistoryProperty   = new SimpleBooleanProperty();
	private static final Map<Integer, SceneObject> sceneObjectMap               = new HashMap<>();
	private static       boolean                   started                      = false;
	private static final HistoryKeeper             history                      = new HistoryKeeper();
	private static       Stage                     stage;
	private static Integer                         showingSceneID;

	/**
	 * The addScene method is the first step to using Switcher. You maintain
	 * in your code, a unique sceneID for each scene you wish to have on tap.<BR><BR>
	 * It is suggested that you have a class in your project that holds
	 * <strong>public final static Integer</strong> variables for each scene you create with unique
	 * values assigned to each.<BR><BR>At a minimum, you must supply to addScene, the
	 * sceneID, the Parent control for the Stage, the width and the height of
	 * the Stage so that Switcher can properly configure the Stage each time
	 * you call showScene.<BR><BR>You can optionally pass into this method, the <strong>X and Y</strong>
	 * coordinates for the Stages upper left corner, as well as the Stages desired
	 * <strong>initStyle and Modality</strong>.
	 *
	 * <pre>
	 * For example, this would be an ideal way to keep sceneIDs so that every
	 * class in your project could call up any scene you desire.
	 *
	 * public static final Integer SCENE_ONE   = 101;
	 * public static final Integer SCENE_TWO   = 102;
	 * public static final Integer SCENE_THREE = 103;
	 *
	 * Then from any Class in your project you would add a Scene and display
	 * them like this (Assuming the class name storing your constants was called C:
	 *
	 * Switcher.addScene(C.SCENE_ONE, anchorPane, width, height);
	 * Switcher.showScene(C.SCENE_ONE);
	 * </pre>
	 * @param sceneID a unique Integer that you provide and maintain in your code
	 * @param root a Parent such as an AnchorPane or a VBox - it is the foundation of the Stage
	 * @param width double - sets the stage width for this scene.
	 * @param height double - sets the stage height for this scene
	 */
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

	/**
	 * Use removeScene to take a sceneID out of Switcher if needed.
	 * @param sceneID a unique Integer
	 */
	public static void removeScene(Integer sceneID) {
		if (sceneObjectMap.containsKey(sceneID)) sceneObjectMap.remove(sceneID);
		else warnNoScene("removeScene",sceneID);
	}

	/**
	 * When this is set to true, the stage will automatically
	 * hide itself when the user clicks anywhere else on their
	 * screen other than this scene. Useful in pop style
	 * applications. Pop style apps can be easily created using the
	 * <a href="https://github.com/dustinkredmond/FXTrayIcon" target="_blank">
	 *     FXTrayIcon library, written by Dustin Redmond</a>
	 *
	 * @param hideOnLostFocus set {@code true} to enable
	 */
	public static void setHideSceneOnLostFocus(boolean hideOnLostFocus) {Switcher.hideStageOnLostFocusProperty.setValue(hideOnLostFocus);}

	/**
	 * Call sceneHiddenOnLostFocus to find out if Switcher is configured to
	 * hide the scene when the stage looses focus.
	 * @return boolean if true, then this option is enabled
	 */
	public static boolean sceneHiddenOnLostFocus()                      {return Switcher.hideStageOnLostFocusProperty.getValue();}

	/**
	 * Use setSceneVisible and pass <strong>true</strong> into the argument to show the scene
	 * if it is currently hidden, or pass <strong>false</strong> to hide the scene if desired.
	 * @param visible set true to un hide the scene, or false to hide it
	 */
	public static void setSceneVisible(boolean visible)                 {Switcher.stageVisibleProperty.setValue(visible);}

	/**
	 * Use sceneVisible to find out if the scene is currently showing on the screen
	 * @return boolean - if true, then Stage is currently being shown on screen
	 */
	public static boolean sceneVisible()                                {return Switcher.stageVisibleProperty.getValue();}


	/**
	 * Used to reposition the Stage on the screen.
	 * This sets the x coordinate of the stages upper
	 * left corner pixel.
	 *
	 * call refresh() after changing the stages x, y,
	 * width and height properties, or optionally set
	 * these values in the addScene() method.
	 *
	 * @param x double
	 */
	public static void setX(double x) {
		if (showingSceneID != null && sceneObjectMap.containsKey(showingSceneID)) {
			sceneObjectMap.get(showingSceneID).setStageX(x);
		}
	}

	/**
	 * Used to reposition the Stage on the screen.
	 * This sets the y coordinate of the stages upper
	 * left corner pixel.
	 *
	 * call refresh() after changing the stages x, y,
	 * width and height properties, or optionally set
	 * these values in the addScene() method.
	 *
	 * @param y double
	 */
	public static void setY(double y) {
		if (showingSceneID != null && sceneObjectMap.containsKey(showingSceneID)) {
			sceneObjectMap.get(showingSceneID).setStageY(y);
		}
	}

	/**
	 * Used to resize the Stages Width property
	 * for the scene currently being displayed.
	 *
	 * call refresh() after changing the stages x, y,
	 * width and height properties, or optionally set
	 * these values in the addScene() method.
	 * @param width double
	 */
	public static void setWidth(double width) {
		if (showingSceneID != null && sceneObjectMap.containsKey(showingSceneID)) {
			sceneObjectMap.get(showingSceneID).setStageWidth(width);
		}
	}

	/**
	 * Used to resize the Stages Height property
	 * for the scene currently being displayed.
	 *
	 * call refresh() after changing the stages x, y,
	 * width and height properties, or optionally set
	 * these values in the addScene() method.
	 * @param height double
	 */
	public static void setHeight(double height) {
		if (showingSceneID != null && sceneObjectMap.containsKey(showingSceneID)) {
			sceneObjectMap.get(showingSceneID).setStageHeight(height);
		}
	}

	/**
	 * This will take the new values that were set using
	 * <strong>setX, setY, setWidth and setHeight</strong> and place the
	 * stage using the new values
	 */
	public static void refresh() {
		if (showingSceneID != null && sceneObjectMap.containsKey(showingSceneID)) {
			showSceneObject(showingSceneID,false);
		}
	}

	/**
	 * showScene is used to display any of the scenes that
	 * have been added to Switcher using the addScene method.<BR><BR>
	 * Simply put the <strong>sceneID</strong> into the argument (sceneID is an
	 * integer that you manage in your code).<BR><BR>You can optionally
	 * include the <strong>X and Y</strong> coordinates of the upper left corner
	 * of the Stage as well as the <strong>width and height</strong> of the stage
	 * if you did not define those parameters in the addScene method.<BR><BR>
	 * <strong>X, Y, width and height will persist in subsequent calls to showScene
	 * without the need to pass those parameters again.</strong>
	 * @param sceneID a unique Integer - each scene needs a unique sceneID
	 */
	public static void showScene(Integer sceneID) {
		if (sceneObjectMap.containsKey(sceneID)) showSceneObject(sceneID, true);
		else warnNoScene("showScene",sceneID);
	}

	/**
	 * showScene by providing its sceneID and optional X and Y coordinates
	 * of the Stage upper left corner. X and Y parameters will persist
	 * in subsequent calls to showScene without the need to pass those parameters.
	 * @param sceneID Integer
	 * @param stageX double
	 * @param stageY double
	 */
	public static void showScene(Integer sceneID, double stageX, double stageY) {
		if (sceneObjectMap.containsKey(sceneID)) {
			SceneObject sceneObject = sceneObjectMap.get(sceneID);
			sceneObject.setStageX(stageX);
			sceneObject.setStageY(stageY);
			showSceneObject(sceneID,true);
		}
		else warnNoScene("showScene",sceneID);
	}

	/**
	 * showScene by providing its sceneID and optional X and Y coordinates
	 * of the Stage upper left corner as well as the desired width and height
	 * of the Stage. X, Y, width and height settings will persist in
	 * subsequent calls to showScene without the need to pass those parameters.
	 * @param sceneID Integer
	 * @param width double
	 * @param height double
	 * @param stageX double
	 * @param stageY double
	 */
	public static void showScene(Integer sceneID, double width, double height, double stageX, double stageY) {
		if (sceneObjectMap.containsKey(sceneID)) {
			SceneObject sceneObject = sceneObjectMap.get(sceneID);
			sceneObject.setStageWidth(width);
			sceneObject.setStageHeight(height);
			sceneObject.setStageX(stageX);
			sceneObject.setStageY(stageY);
			showSceneObject(sceneID,true);
		}
		else warnNoScene("showScene",sceneID);
	}

	/**
	 * Use getVisibleWithHistoryProperty to bind to a control that invokes the showLastScene method.
	 * For example: myButton.visibleProperty.bind(Switcher.getVisibleWithHistoryProperty());
	 * Your control will then be hidden when Switcher has no more scenes in its history to pull up.
	 * @return BooleanProperty
	 */
	public static BooleanProperty getVisibleWithHistoryProperty() { return visibleWithHistoryProperty;	}

	/**
	 * Use getEnabledWithHistoryProperty to bind to a control that invokes the showLastScene method.
	 * For example: myButton.disableProperty().bind(Switcher.getEnabledWithHistoryProperty());
	 * Your control will then be disabled when Switcher has no more scenes in its history to pull up.
	 * @return BooleanProperty
	 */
	public static BooleanProperty getEnabledWithHistoryProperty() { return enabledWithHistoryProperty; }

	/**
	 * As your code invokes showScene to show the different Scenes that you have added into Switcher,
	 * A history of the SceneIDs that you have been showing are stacked up in a que, so that you can
	 * invoke showLastScene to show the Scene that was last displayed. You can continue calling this
	 * method until the first scene displayed is reached. This is similar behavior to a Back button
	 * on a web browser.
	 */
	public static void showLastScene() {
		Integer lastSceneID = history.getLastSceneID();
		System.out.println("LastSceneID: " + lastSceneID);
		showSceneObject(lastSceneID,false);
	}

	/**
	 * setParent lets you assign the Parent container to which sceneID you pass into the argument.
	 * @param sceneID Integer
	 * @param parent Parent
	 */
	public static void setParent(Integer sceneID, Parent parent) {
		if (sceneObjectMap.containsKey(sceneID)) {
			sceneObjectMap.get(sceneID).scene().setRoot(parent);
		}
		else warnNoScene("setParent",sceneID);
	}

	/**
	 * Use getStage() to gain access to the Stage for making whatever changes to it that you need.
	 * Switcher currently only holds one Stage and then switches out scenes on that stage by using
	 * the showScene method.
	 * @return Stage
	 */
	public static Stage getStage() {return stage;}

	private static void showSceneObject(Integer sceneID, boolean showingNewScene) {
		SceneObject sceneObject = sceneObjectMap.get(sceneID);
		if (showingNewScene){
			history.showingNewScene(sceneID);
		}
		visibleWithHistoryProperty.setValue(history.hasHistory());
		enabledWithHistoryProperty.setValue(!history.hasHistory());
		showingSceneID = sceneID;
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
			stage.hide();
			Rectangle2D  screenBounds = Screen.getPrimary().getVisualBounds();
			double       screenWidth  = screenBounds.getWidth();
			double       screenHeight = screenBounds.getHeight();
			final double finalX       = (stageX > -1 && stageX <= screenWidth) ? stageX : (screenWidth - (stage.getWidth() / 2));
			final double finalY       = (stageY > -1 && stageY <= screenHeight) ? stageY : (screenHeight - (stage.getHeight() / 2));
			if (stageStyle != null) stage.initStyle(stageStyle);
			if (stageModality != null) stage.initModality(stageModality);
			sceneObject.setStageX(finalX);
			sceneObject.setStageY(finalY);
			stage.show();
			stage.toFront();
			stage.requestFocus();
		});
	}

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

	private static void addSceneFinally(Parent parent, double width, double height, Integer sceneID, StageStyle stageStyle, Modality stageModality) {
		if (!Switcher.started) start();
		sceneObjectMap.put(sceneID, new SceneObject(parent, width, height, stageStyle, stageModality));
	}

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

}

/**
 * SceneObject is a Class that contains all
 * of the relevant information about a
 * scene so that the showScene methods
 * can properly configure the stage and
 * the scene for display. This class is
 * private to Switcher.
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

	public Scene scene()            {return scene;}

	public double getWidth()             {return width;} // Returns width of stage

	public double getHeight()            {return height;} // Returns height of stage

	public double getStageX()            {return stageX;} // Returns the top left corner of stage as X coordinate

	public double getStageY()            {return stageY;} // Returns the top left corner of stage as Y coordinate

	public void setStageX(double stageX) {this.stageX = stageX;}

	public void setStageY(double stageY) {this.stageY = stageY;}

	public void setStageWidth(double width) {
		this.width = width;
	}

	public void setStageHeight(double height) {
		this.height = height;
	}

}

/**
 * This class simply maintains the history of
 * scenes as they are shown so that Switcher can
 * easily go back to the previously shown
 * scene when showLastScene is invoked.
 * This class is private to Switcher.
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