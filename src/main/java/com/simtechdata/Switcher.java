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

import java.lang.management.PlatformManagedObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
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
	private static final Map<Integer, Stage>       stageMap               		= new HashMap<>();
	private static       boolean                   started                      = false;
	private static final HistoryKeeper             history                      = new HistoryKeeper();
	private static       Stage                     stage;
	private static Integer                         coreStageID = getRandom();
	private static Integer                         showingSceneID;
	private static Integer                         lastSceneIDShowing;

	/**
	 * The addScene method is the first step to using Switcher. You maintain
	 * in your code, a unique sceneID for each getScene you wish to have on tap.<BR><BR>
	 * It is suggested that you have a class in your project that holds
	 * <strong>public final static Integer</strong> constants for each Scene you create with unique
	 * values assigned to each.<BR><BR>At a minimum, you must supply to addScene, the
	 * sceneID, the Parent control for the Stage, the width and the height of
	 * the Stage so that Switcher can properly configure the Stage each time
	 * you call showScene.<BR><BR>You can also include a StageID if you need a
	 * Scene to be shown on a specific stage. You might, for example, wish to
	 * have one stage with one StageStyle and initModality while another stage
	 * uses the default settings for StageStyle and initModality.<BR><BR>
	 * There are different ways of adding a Scene with an assigned Stage,
	 * including being able to chose StageStyle and initModality on the same
	 * line, or each one individually, or just leave them out all together to use
	 * a default Stage configuration.
	 *
	 * <pre>
	 * For example, this would be an ideal way to keep sceneIDs and stageIDs so that every
	 * class in your project could call up any getScene you desire and if it has an assigned
	 * stageID, Everything will be built and shown automatically for you.
	 *
	 * public static final Integer SCENE_ONE   = 101;
	 * public static final Integer SCENE_TWO   = 102;
	 * public static final Integer SCENE_THREE = 103;
	 * public static final Integer STAGE_DEFAULT = 201;
	 * public static final Integer STAGE_TRANSPARENT = 202;
	 * public static final Integer STAGE_TRANSPARENT_WINDOW_MODAL = 203;
	 *
	 * Then from any Class in your project you would add a Scene and display
	 * them like this (Assuming the class name storing your constants was called C:
	 *
	 * Switcher.addScene(C.SCENE_ONE, anchorPane, width, height);
	 * Switcher.addScene(C.SCENE_TWO, C.STAGE_TRANSPARENT_WINDOW_MODAL, anchorPane, width, height, StageStyle.TRANSPARENT, Modality.WINDOW_MODAL);
	 * Switcher.addScene(C.SCENE_THREE, C.STAGE_TRANSPARENT_WINDOW_MODAL, anchorPane, width, height);
	 * Switcher.showScene(C.SCENE_ONE);
	 * Switcher.showScene(C.SCENE_TWO);
	 *
	 * </pre>
	 *
	 * I doesn't matter if you accidentally include the style and modal again after you've
	 * defined the stage once, Switcher will not overwrite a Stage when it's ID already exists.
	 *
	 * @param sceneID a unique Integer that you provide and maintain in your code
	 * @param root a Parent such as an AnchorPane or a VBox - it is the foundation of the Stage
	 * @param width double - sets the stage width for this getScene.
	 * @param height double - sets the stage height for this getScene
	 */
	public static void addScene(Integer sceneID, Parent root, double width, double height) {
		addSceneObject(sceneID, root, width, height);
	}

	public static void addScene(Integer sceneID, Integer stageID, Parent root, double width, double height, StageStyle initStyle, Modality initModality) {
		checkStageID(stageID);
		if (!stageMap.containsKey(stageID)) {
			Stage stage = new Stage();
			if (initStyle != null) stage.initStyle(initStyle);
			if (initModality != null) stage.initModality(initModality);
			stageMap.put(stageID,stage);
		}
		addSceneObject(sceneID, stageID, root, width, height);
	}

	public static void addScene(Integer sceneID, Integer stageID, Parent root, double width, double height, StageStyle initStyle) {
		checkStageID(stageID);
		if (!stageMap.containsKey(stageID)) {
			Stage stage = new Stage();
			if (initStyle != null) stage.initStyle(initStyle);
			stageMap.put(stageID,stage);
		}
		addSceneObject(sceneID, stageID, root, width, height);
	}

	public static void addScene(Integer sceneID, Integer stageID, Parent root, double width, double height, Modality initModality) {
		checkStageID(stageID);
		if (!stageMap.containsKey(stageID)) {
			Stage stage = new Stage();
			if (initModality != null) stage.initModality(initModality);
			stageMap.put(stageID,stage);
		}
		addSceneObject(sceneID, stageID, root, width, height);
	}

	public static void addScene(Integer sceneID, Integer stageID, Parent root, double width, double height) {
		checkStageID(stageID);
		if (!stageMap.containsKey(stageID)) {
			Stage stage = new Stage();
			stageMap.put(stageID,stage);
		}
		addSceneObject(sceneID, stageID, root, width, height);
	}

	/**
	 * Use addStage to give Switcher a stage that you configured.
	 * along with the stageID for that stage. Then,
	 * you can add Scenes and assign them to this stageID.
	 * But Remember, when you include a stageID in the addScene method,
	 * if the stageID has not been previously added to Switcher, it will
	 * create a new one and add it. So ideally, you would use this method
	 * before adding scenes to assign to it. <BR><BR>
	 * Alternatively, you can later re-assign a scene to a stageID by using the
	 * assignSceneToStage method.
	 * @param stageID a unique Integer
	 * @param stage a Stage that you configured
	 */
	public static void addStage(Integer stageID, Stage stage) {
		checkStageID(stageID);
		if (!stageMap.containsKey(stageID)) {
			stageMap.put(stageID, stage);
		}
		else { System.err.println("addStage - stageID " + stageID + " ALREADY EXIST USE removeStage first"); }
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
	 * Use removeStage to remove a stage from Switcher.
	 * This also removes the stageID from any Scenes that
	 * are assigned to it, causing them to be shown on the
	 * default Stage if they are not re assigned to a different
	 * stageID using the assignSceneToStage method.
	 * @param stageID a unique Integer
	 */
	public static void removeStage(Integer stageID) {
		if (stageMap.containsKey(stageID)) {
			stageMap.remove(stageID);
			for (Integer sid : sceneObjectMap.keySet()) {
				SceneObject so = sceneObjectMap.get(sid);
				if (so.getStageID().equals(stageID)) {
					so.setStageID(null);
					sceneObjectMap.replace(sid,so);
				}
			}
		}
		else warnNoStage("removeStage",stageID);
	}

	/**
	 * If you have already added a scene to Switcher and later want to assign it
	 * to a Stage that you also have already added, then assignSceneToStage is
	 * how you do it. If the Scene was assigned to a different stage before,
	 * then that stages ID will be replaced with this one.
	 * @param sceneID Integer containing the Scenes ID
	 * @param stageID Integer containing the Stages ID
	 */
	public static void assignSceneToStage (Integer sceneID, Integer stageID){
		if (sceneObjectMap.containsKey(sceneID)) sceneObjectMap.get(sceneID).setStageID(stageID);
		else warnNoScene("assignSceneToStage",sceneID);
	}

	/**
	 * When a Scene is not assigned to a Stage, Switcher will show it
	 * on the default Stage. By default, the default stage is created
	 * without any StageStyle or Modality. You can, however, change that
	 * with this method.<BR><BR>If you only need to set one of the two parameters,
	 * pass null into the other one and the Stage will not be configured
	 * with that option.
	 * @param initStyle your StageStyle
	 * @param initModality your Modality
	 */
	public static void configureDefaultStage(StageStyle initStyle, Modality initModality) {
		stageMap.remove(coreStageID);
		Stage stage = new Stage();
		if (initStyle != null) stage.initStyle(initStyle);
		if (initModality != null) stage.initModality(initModality);
		stageMap.put(coreStageID,stage);
	}

	public static Stage getStage(Integer stageID) {
		if (stageMap.containsKey(stageID)) return stageMap.get(stageID);
		return null;
	}

	/**
	 * use setDefaultStage to get access to the default stage
	 * This would mainly be used so that you can set the
	 * setOnCloseRequest option of the stage to have it
	 * call whatever method you want when the stage is asked to close.
	 * @return default Stage or null if you have not added any scenes.
	 */
	public static Stage getDefaultStage() {
		return stageMap.getOrDefault(coreStageID, null);
	}

	/**
	 * Use getScene to gain access to the Scene that Switcher creates
	 * so that you can make changes to it as needed
	 * @param sceneID the sceneID of the scene you want
	 * @return Will return null if the sceneID does not exist
	 */
	public static Scene getScene(Integer sceneID) {
		if (sceneObjectMap.containsKey(sceneID)) {
			return sceneObjectMap.get(sceneID).getScene();
		}
		else {
			warnNoScene("getScene",sceneID);
			return null;
		}
	}

	/**
	 * When this is set to true, the stage will automatically
	 * hide itself when the user clicks anywhere else on their
	 * screen other than this getScene. Useful in pop style
	 * applications. Pop style apps can be easily created using the
	 * <a href="https://github.com/dustinkredmond/FXTrayIcon" target="_blank">
	 *     FXTrayIcon library, written by Dustin Redmond</a>
	 *
	 * @param hideOnLostFocus set {@code true} to enable
	 */
	public static void setHideSceneOnLostFocus(boolean hideOnLostFocus) {Switcher.hideStageOnLostFocusProperty.setValue(hideOnLostFocus);}

	/**
	 * Call sceneHiddenOnLostFocus to find out if Switcher is configured to
	 * hide the getScene when the stage looses focus.
	 * @return boolean if true, then this option is enabled
	 */
	public static boolean sceneHiddenOnLostFocus()                      {return Switcher.hideStageOnLostFocusProperty.getValue();}

	/**
	 * Use setSceneVisible and pass <strong>true</strong> into the argument to show the getScene
	 * if it is currently hidden, or pass <strong>false</strong> to hide the getScene if desired.
	 * @param visible set true to un hide the getScene, or false to hide it
	 */
	public static void setSceneVisible(boolean visible) {
		if (visible) {
			if (showingSceneID == null) showingSceneID = lastSceneIDShowing;
		}
		else {
			lastSceneIDShowing = showingSceneID;
			showingSceneID = null;
		}
		Switcher.stageVisibleProperty.setValue(visible);
	}

	/**
	 * Use sceneVisible to find out if the getScene is currently showing on the screen
	 * @return boolean - if true, then Stage is currently being shown on screen
	 */
	public static boolean sceneVisible() {return Switcher.stageVisibleProperty.getValue();}

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
	 * @param sceneID a unique Integer - each getScene needs a unique sceneID
	 */
	public static void showScene(Integer sceneID) {
		Platform.runLater(()->{
			if (sceneObjectMap.containsKey(sceneID)) showSceneObject(sceneID, true);
			else warnNoScene("showScene",sceneID);
		});
	}

	/**
	 * showScene by providing its sceneID and optional X and Y coordinates
	 * of the Stage upper left corner as well as the desired width and height
	 * of the Stage. X, Y, width and height settings will persist in
	 * subsequent calls to showScene without the need to pass those parameters again.
	 * @param sceneID Integer
	 * @param width double
	 * @param height double
	 * @param stageX double
	 * @param stageY double
	 */
	public static void showScene(Integer sceneID, double width, double height, double stageX, double stageY) {
		Platform.runLater(()->{
			if (sceneObjectMap.containsKey(sceneID)) {
				SceneObject sceneObject = sceneObjectMap.get(sceneID);
				sceneObject.setStageWidth(width);
				sceneObject.setStageHeight(height);
				sceneObject.setStageX(stageX);
				sceneObject.setStageY(stageY);
				showSceneObject(sceneID,true);
			}
			else warnNoScene("showScene",sceneID);
		});
	}

	/**
	 * showSceneWithPosition by providing its sceneID and optional X and Y coordinates
	 * of the Stage upper left corner. X and Y parameters will persist
	 * in subsequent calls to showScene without the need to pass those parameters again.
	 * @param sceneID Integer
	 * @param stageX double
	 * @param stageY double
	 */
	public static void showSceneWithPosition(Integer sceneID, double stageX, double stageY) {
		Platform.runLater(()->{
			if (stageX < 0 || stageY < 0) System.err.println("Values for X and Y in showScene must not be negative");
			else {
				if (sceneObjectMap.containsKey(sceneID)) {
					SceneObject so = sceneObjectMap.get(sceneID);
					so.setStageX(stageX);
					so.setStageY(stageY);
					so.setCustomXY(true);
					showSceneObject(sceneID,true);
				}
				else warnNoScene("showScene",sceneID);
			}
		});
	}

	/**
	 * showSceneWithSize by providing its sceneID and optional width and height
	 * values. There settings will persist in subsequent calls to showScene without
	 * the need to pass those parameters again.
	 * @param sceneID Integer
	 * @param width double
	 * @param height double
	 */
	public static void showSceneWithSize(Integer sceneID, double width, double height) {
		Platform.runLater(()->{
			if (sceneObjectMap.containsKey(sceneID)) {
				SceneObject sceneObject = sceneObjectMap.get(sceneID);
				sceneObject.setStageWidth(width);
				sceneObject.setStageHeight(height);
				showSceneObject(sceneID,true);
			}
			else warnNoScene("showScene",sceneID);
		});
	}

	/**
	 * use isShowing to find out if a particular scene is the one current being displayed on
	 * the screen. This is different than using visible, since it will tell you if a specific
	 * Scene is on the screen and not just whether the stage is visible or not.
	 * @param sceneID unique ID Integer
	 * @return true if showing, false if not
	 */
	public static boolean isShowing(Integer sceneID) {
		if (showingSceneID == null) return false;
		else return sceneID.equals(showingSceneID);
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
	 * method until the first getScene displayed is reached. This is similar behavior to a Back button
	 * on a web browser.
	 */
	public static void showLastScene() {
		Platform.runLater(()->{
			Integer lastSceneID = history.getLastSceneID();
			System.out.println("LastSceneID: " + lastSceneID);
			showSceneObject(lastSceneID,false);
		});
	}

	private static void showSceneObject(Integer sceneID, boolean showingNewScene) {
		showingSceneID = sceneID;
		SceneObject sceneObject = sceneObjectMap.get(sceneID);
		if (showingNewScene) {
			history.showingNewScene(sceneID);
		}
		visibleWithHistoryProperty.setValue(history.hasHistory());
		enabledWithHistoryProperty.setValue(!history.hasHistory());
		stage.hide();
		stage = (sceneObject.getStageID() == null) ? stageMap.get(coreStageID) : stageMap.get(sceneObject.getStageID());
		final Scene   scene       = sceneObject.getScene();
		final double  stageX      = sceneObject.getStageX();
		final double  stageY      = sceneObject.getStageY();
		final double  stageWidth  = sceneObject.getWidth();
		final double  stageHeight = sceneObject.getHeight();
		final boolean customXY    = sceneObject.hasCustomXY();
		stage.setWidth(stageWidth);
		stage.setHeight(stageHeight);
		stage.setScene(scene);
		Platform.runLater(() -> {
			Rectangle2D  screenBounds = Screen.getPrimary().getVisualBounds();
			double       screenWidth  = screenBounds.getWidth();
			double       screenHeight = screenBounds.getHeight();
			final double finalX       = customXY ? stageX : (screenWidth - (screenWidth / 2) - (stageWidth / 2));
			final double finalY       = customXY ? stageY : (screenHeight - (screenHeight / 2) - (stageHeight / 2));
			stage.setX(finalX);
			stage.setY(finalY);
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

	private static void warnNoStage(String callingMethod, Integer stageID) {
		System.err.println("stageID " + stageID + " does not exist being called from method " + callingMethod);
	}

	private static void sleep(long milliseconds) {
		try {TimeUnit.MILLISECONDS.sleep(milliseconds);}catch (InterruptedException e) {e.printStackTrace();}
	}

	private static Integer getRandom() {
		int min = 1000000;
		int max = 9999999;
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	private static void getNewRandom(Integer duplicateInteger) {
		int min = 1000000;
		int max = 9999999;
		while (coreStageID.equals(duplicateInteger)) {
			coreStageID = ThreadLocalRandom.current().nextInt(min, max + 1);
		}
	}

	private static void addSceneObject(Integer sceneID, Parent parent, double width, double height) {
		if (!Switcher.started) {
			start();
		}
		sceneObjectMap.put(sceneID, new SceneObject(parent, width, height));
	}

	private static void addSceneObject(Integer sceneID, Integer stageID, Parent parent, double width, double height) {
		if (!Switcher.started) {
			start();
		}
		sceneObjectMap.put(sceneID, new SceneObject(stageID, parent, width, height));
	}

	private static void checkStageID(Integer stageID) {
		if (stageID.equals(coreStageID)) {
			getNewRandom(stageID);
			stageMap.put(coreStageID,stageMap.get(stageID));
			stageMap.remove(stageID);
		}
	}

	private static void start() {
		if (!stageMap.containsKey(coreStageID)) {
			stage = new Stage();
			stageMap.put(coreStageID,stage);
		}
		else stage = stageMap.get(coreStageID);
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
 * getScene so that the showScene methods
 * can properly configure the stage and
 * the getScene for display. This class is
 * private to Switcher.
 */
class SceneObject extends Switcher {

	SceneObject(Integer stageID, Parent parent, double width, double height) {
		this.stageID = stageID;
		this.parent  = parent;
		this.width   = width;
		this.height  = height;
		this.scene   = new Scene(this.parent);
	}

	SceneObject(Parent parent, double width, double height) {
		this.parent  = parent;
		this.width   = width;
		this.height  = height;
		this.scene   = new Scene(this.parent);
	}

	private       Integer stageID;
	private final Scene   scene;
	private final Parent  parent;
	private       double  width;
	private       double  height;
	private       double  stageX = -1;
	private       double  stageY = -1;
	private boolean customXY = false;

	public boolean hasCustomXY() {return customXY;}

	public void setCustomXY(boolean customXY) {this.customXY = customXY;}

	public Scene getScene()                 {return scene;}

	public Integer getStageID()             {return this.stageID;}

	public void setStageID(Integer stageID) {this.stageID = stageID;}

	public double getWidth()                {return width;} // Returns width of stage

	public double getHeight()               {return height;} // Returns height of stage

	public double getStageX()               {return stageX;} // Returns the top left corner of stage as X coordinate

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
 * getScene when showLastScene is invoked.
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