package com.simtechdata;

/*
 * Code developed by Mike Sims  - 2020
 *
 * This library is an evolution that emerged out of a personal library project where
 * I created classes to significantly streamline my development time across projects.
 * I realized that it had matured to a point where it needed to stand on its own
 * and since I use this library in all of my projects, I thought that perhaps the
 * community in general could benefit from it.
 *
 * I hope you enjoy using it as much as I've enjoyed developing it.
 */


import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Switcher is a library that makes managing your scenes literally one line of code easy!
 * It all starts with the addScene() method.
 */
@SuppressWarnings({"unused", "SameParameterValue"}) public class Switcher {

	private static final BooleanProperty           visibleWithHistoryProperty = new SimpleBooleanProperty();
	private static final BooleanProperty           enabledWithHistoryProperty = new SimpleBooleanProperty();
	private static final Map<Integer, SceneObject> sceneObjectMap             = new HashMap<>();
	private static final Map<Integer, Stage>       stageMap                   = new HashMap<>();
	private static final List<Integer>             randomInts                 = new ArrayList<>();
	private static final boolean                   NEW_SCENE                  = true;
	private static final boolean                   PRIOR_SCENE                = false;
	private static final HistoryKeeper             history                    = new HistoryKeeper();
	private static       boolean                   firstRun                   = true;
	private static       boolean                   allHiddenOnLostFocus       = false;
	private static       Integer                   defaultStageID             = getRandom();
	private static       Integer                   lastSceneIDShowing;

	/**
	 * Use this method in situations where you need to assign the primaryStage
	 * that is created at the start of your program as the default Stage
	 * in Switcher.
	 * <p>
	 * THIS MUST BE THE FIRST CALL YOU MAKE TO SWITCHER BEFORE ADDING A SCENE!
	 *
	 * @param primaryStage Stage - your programs primaryStage
	 */
	public static void init(Stage primaryStage) {
		setPrimaryStage(primaryStage);
	}

	/**
	 * Use this method in situations where you need to assign the primaryStage
	 * that is created at the start of your program as the default Stage
	 * in Switcher.
	 * <p>
	 * THIS MUST BE THE FIRST CALL YOU MAKE TO SWITCHER BEFORE ADDING A SCENE!
	 *
	 * @param primaryStage Stage - your programs primaryStage, optionally set
	 *                     the stage style and modality. You can pass null for style or modality
	 *                     if you only need to set one or the other.
	 */
	public static void init(Stage primaryStage, StageStyle initStyle, Modality initModality) {
		if (initStyle != null) primaryStage.initStyle(initStyle);
		if (initModality != null) primaryStage.initModality(initModality);
		setPrimaryStage(primaryStage);
	}

	/**
	 * Alternate for init - options never hurt anything :-)
	 * <p>
	 * THIS MUST BE THE FIRST CALL YOU MAKE TO SWITCHER BEFORE ADDING A SCENE!
	 *
	 * @param primaryStage Stage - your programs primaryStage
	 */
	public static void setPrimary(Stage primaryStage) {
		setPrimaryStage(primaryStage);
	}

	/**
	 * Alternate for init - options never hurt anything :-)
	 * <p>
	 * THIS MUST BE THE FIRST CALL YOU MAKE TO SWITCHER BEFORE ADDING A SCENE!
	 *
	 * @param primaryStage Stage - your programs primaryStage, optionally set
	 *                     the stage style and modality. You can pass null for style or modality
	 *                     if you only need to set one or the other.
	 */
	public static void setPrimary(Stage primaryStage, StageStyle initStyle, Modality initModality) {
		if (initStyle != null) primaryStage.initStyle(initStyle);
		if (initModality != null) primaryStage.initModality(initModality);
		setPrimaryStage(primaryStage);
	}

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
	 * <p>
	 * I doesn't matter if you accidentally include the style and modal again after you've
	 * defined the stage once, Switcher will not overwrite a Stage when it's ID already exists.
	 *
	 * @param sceneID a unique Integer that you provide and maintain in your code
	 * @param root    a Parent such as an AnchorPane or a VBox - it is the foundation of the Stage
	 * @param width   Double - sets the stage width for this getScene.
	 * @param height  Double - sets the stage height for this getScene
	 */
	public static void addScene(Integer sceneID, Parent root, Double width, Double height) {
		addSceneObject(sceneID, null, root, width, height, null);
	}

	public static void addScene(Integer sceneID, Parent root, Double width, Double height, StageStyle initStyle, Modality initModality) {
		addSceneObject(sceneID, null, root, width, height, null);
		stageMap.get(defaultStageID)
				.initStyle(initStyle);
		stageMap.get(defaultStageID)
				.initModality(initModality);
	}

	public static void addScene(Integer sceneID, Integer stageID, Parent root, Double width, Double height, StageStyle initStyle, Modality initModality) {
		if (!stageMap.containsKey(stageID)) {
			Stage stage = new Stage();
			if (initStyle != null) stage.initStyle(initStyle);
			if (initModality != null) stage.initModality(initModality);
			stageMap.put(stageID, stage);
		}
		checkForIDConflict(stageID);
		addSceneObject(sceneID, stageID, root, width, height, null);
	}

	public static void addScene(Integer sceneID, Integer stageID, Parent root, Double width, Double height, StageStyle initStyle) {
		checkForIDConflict(stageID);
		if (!stageMap.containsKey(stageID)) {
			Stage stage = new Stage();
			if (initStyle != null) stage.initStyle(initStyle);
			stageMap.put(stageID, stage);
		}
		addSceneObject(sceneID, stageID, root, width, height, null);
	}

	public static void addScene(Integer sceneID, Integer stageID, Parent root, Double width, Double height, Modality initModality) {
		checkForIDConflict(stageID);
		if (!stageMap.containsKey(stageID)) {
			Stage stage = new Stage();
			if (initModality != null) stage.initModality(initModality);
			stageMap.put(stageID, stage);
		}
		addSceneObject(sceneID, stageID, root, width, height, null);
	}

	public static void addScene(Integer sceneID, Integer stageID, Parent root, Double width, Double height) {
		checkForIDConflict(stageID);
		if (!stageMap.containsKey(stageID)) {
			Stage stage = new Stage();
			stageMap.put(stageID, stage);
		}
		addSceneObject(sceneID, stageID, root, width, height, null);
	}

	public static void addScene(Integer sceneID, Scene scene, Stage stage) {
		Integer newStageID = getRandom();
		stageMap.put(newStageID, stage);
		addSceneObject(sceneID, newStageID, null, null, null, scene);
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
	 *
	 * @param stageID a unique Integer
	 * @param stage   a Stage that you configured
	 */
	public static void addStage(Integer stageID, Stage stage) {
		checkForIDConflict(stageID);
		if (!stageMap.containsKey(stageID)) {
			stageMap.put(stageID, stage);
		}
		else {System.err.println("addStage - stageID " + stageID + " ALREADY EXIST USE removeStage first");}
	}

	/**
	 * Use removeScene to take a sceneID out of Switcher if needed.
	 *
	 * @param sceneID a unique Integer
	 */
	public static void removeScene(Integer sceneID) {
		if (sceneObjectMap.containsKey(sceneID)) {sceneObjectMap.remove(sceneID);}
		else {warnNoScene("removeScene", sceneID);}
	}

	/**
	 * Use removeStage to remove a stage from Switcher.
	 * This also removes the stageID from any Scenes that
	 * are assigned to it, causing them to be shown on the
	 * default Stage if they are not re assigned to a different
	 * stageID using the assignSceneToStage method.
	 *
	 * @param stageID a unique Integer
	 */
	public static void removeStage(Integer stageID) {
		if (stageMap.containsKey(stageID)) {
			stageMap.remove(stageID);
			for (Integer sid : sceneObjectMap.keySet()) {
				SceneObject so = sceneObjectMap.get(sid);
				if (so.getStageID()
					  .equals(stageID)) {
					so.setStageID(null);
					sceneObjectMap.replace(sid, so);
				}
			}
		}
		else {warnNoStage("removeStage", stageID);}
	}

	/**
	 * If you have already added a scene to Switcher and later want to assign it
	 * to a Stage that you also have already added, then assignSceneToStage is
	 * how you do it. If the Scene was assigned to a different stage before,
	 * then that stages ID will be replaced with this one.
	 *
	 * @param sceneID Integer containing the Scenes ID
	 * @param stageID Integer containing the Stages ID
	 */
	public static void assignSceneToStage(Integer sceneID, Integer stageID) {
		if (sceneObjectMap.containsKey(sceneID)) {
			sceneObjectMap.get(sceneID)
						  .setStageID(stageID);
		}
		else {warnNoScene("assignSceneToStage", sceneID);}
	}

	/**
	 * When a Scene is not assigned to a Stage, Switcher will show it
	 * on the default Stage. By default, the default stage is created
	 * without any StageStyle or Modality. You can, however, change that
	 * with this method.<BR><BR>If you only need to set one of the two parameters,
	 * pass null into the other one and the Stage will not be configured
	 * with that option.
	 *
	 * @param initStyle    your StageStyle
	 * @param initModality your Modality
	 */
	public static void configureDefaultStage(final StageStyle initStyle, final Modality initModality) {
		Platform.runLater(() -> {
			if (!stageMap.containsKey(defaultStageID)) stageMap.put(defaultStageID, new Stage());
			if (initStyle != null) {
				stageMap.get(defaultStageID)
						.initStyle(initStyle);
			}
			if (initModality != null) {
				stageMap.get(defaultStageID)
						.initModality(initModality);
			}
		});
	}

	/**
	 * Short version of showScene
	 *
	 * @param sceneID Integer of your unique sceneID
	 */
	public static void show(Integer sceneID) {showScene(sceneID);}

	/**
	 * Short version of showScene
	 *
	 * @param sceneID       Integer of your unique sceneID
	 * @param showMaximized - set true to show maximized
	 */
	public static void show(Integer sceneID, boolean showMaximized) {showScene(sceneID, showMaximized);}

	/**
	 * Short version of showScene
	 * show a Scene by providing its sceneID and optional X and Y coordinates
	 * of the Stage upper left corner as well as the desired width and height
	 * of the Stage. X, Y, width and height settings will persist in
	 * subsequent calls to showScene without the need to pass those parameters again.
	 *
	 * @param sceneID Integer
	 * @param width   Double
	 * @param height  Double
	 * @param stageX  Double
	 * @param stageY  Double
	 */
	public static void show(Integer sceneID, Double width, Double height, Double stageX, Double stageY) {showScene(sceneID, width, height, stageX, stageY);}

	/**
	 * show a Scene by providing its sceneID and optional width and height
	 * values. There settings will persist in subsequent calls to showScene without
	 * the need to pass those parameters again.
	 *
	 * @param sceneID Integer
	 * @param width   Double
	 * @param height  Double
	 */
	public static void show(Integer sceneID, Double width, Double height) {showScene(sceneID, width, height);}

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
	 *
	 * @param sceneID a unique Integer - each getScene needs a unique sceneID
	 */
	public static void showScene(Integer sceneID) {showSceneFinal(sceneID, null, null, null, null, NEW_SCENE, false);}

	/**
	 * Same as showScene with option to show maximized
	 *
	 * @param sceneID       a unique Integer - each getScene needs a unique sceneID
	 * @param showMaximized - set true to show maximized
	 */
	public static void showScene(Integer sceneID, boolean showMaximized) {showSceneFinal(sceneID, null, null, null, null, NEW_SCENE, showMaximized);}

	/**
	 * showScene by providing its sceneID and optional X and Y coordinates
	 * of the Stage upper left corner as well as the desired width and height
	 * of the Stage. X, Y, width and height settings will persist in
	 * subsequent calls to showScene without the need to pass those parameters again.
	 *
	 * @param sceneID Integer
	 * @param width   Double
	 * @param height  Double
	 * @param stageX  Double
	 * @param stageY  Double
	 */
	public static void showScene(Integer sceneID, Double width, Double height, Double stageX, Double stageY) {
		showSceneFinal(sceneID, width, height, stageX, stageY, NEW_SCENE, false);
	}

	/**
	 * showScene by providing its sceneID and optional width and height
	 * values. There settings will persist in subsequent calls to showScene without
	 * the need to pass those parameters again.
	 *
	 * @param sceneID Integer
	 * @param width   Double
	 * @param height  Double
	 */
	public static void showScene(Integer sceneID, Double width, Double height) {
		showSceneFinal(sceneID, width, height, null, null, NEW_SCENE, false);
	}

	/**
	 * use this method to show a scene in maximized form
	 *
	 * @param sceneID Integer of your unique sceneID
	 */
	public static void showMaximized(Integer sceneID) {showScene(sceneID, true);}

	/**
	 * use this method to show a scene in maximized form
	 *
	 * @param sceneID Integer of your unique sceneID
	 */
	public static void showSceneMaximized(Integer sceneID) {showScene(sceneID, true);}

	/**
	 * @param sceneID Integer
	 * @param stageX  Double
	 * @param stageY  Double
	 * @deprecated Use showSceneAt
	 */
	public static void showSceneWithPosition(Integer sceneID, Double stageX, Double stageY) {
		showSceneAt(sceneID, stageX, stageY);
	}

	/**
	 * showSceneAt coordinates by providing its sceneID and optional X and Y coordinates
	 * of the Stage upper left corner. X and Y parameters will persist
	 * in subsequent calls to showScene without the need to pass those parameters again.
	 *
	 * @param sceneID Integer
	 * @param stageX  Double
	 * @param stageY  Double
	 */
	public static void showSceneAt(Integer sceneID, Double stageX, Double stageY) {
		Platform.runLater(() -> {
			if (stageX < 0 || stageY < 0) {System.err.println("Values for X and Y in showScene must not be negative");}
			else {
				showSceneFinal(sceneID, null, null, stageX, stageY, NEW_SCENE, false);
			}
		});
	}

	/**
	 * showSceneSplitX by providing its sceneID and X and Y coordinates
	 * we will get the width of the scene and split it in half, then subtract half
	 * from the X coordinate. This allows you to have Switcher center the scene on a point horizontally.
	 * Switcher will remember the coordinates set so new calls to showScene will be in the same place.
	 *
	 * @param sceneID Integer
	 * @param stageX  Double
	 * @param stageY  Double
	 */
	public static void showSceneSplitX(Integer sceneID, Double stageX, Double stageY) {
		Platform.runLater(() -> {
			if (stageX < 0 || stageY < 0) {System.err.println("Values for X and Y in showScene must not be negative");}
			else {
				if (sceneObjectMap.containsKey(sceneID)) {
					SceneObject so        = sceneObjectMap.get(sceneID);
					Double      newStageX = stageX - (so.getWidth() / 2);
					showSceneFinal(sceneID, null, null, newStageX, stageY, NEW_SCENE, false);
				}
				else {warnNoScene("showScene", sceneID);}
			}
		});
	}

	/**
	 * showSceneSplitY by providing its sceneID and X and Y coordinates
	 * we will get the height of the scene and split it in half, then subtract half
	 * from the Y coordinate. This allows you to have Switcher center the scene on a point vertically.
	 * Switcher will remember the coordinates set so new calls to showScene will be in the same place.
	 *
	 * @param sceneID Integer
	 * @param stageX  Double
	 * @param stageY  Double
	 */
	public static void showSceneSplitY(Integer sceneID, Double stageX, Double stageY) {
		Platform.runLater(() -> {
			if (stageX < 0 || stageY < 0) {System.err.println("Values for X and Y in showScene must not be negative");}
			else {
				if (sceneObjectMap.containsKey(sceneID)) {
					SceneObject so        = sceneObjectMap.get(sceneID);
					Double      newStageY = stageY - (so.getHeight() / 2);
					showSceneFinal(sceneID, null, null, stageX, newStageY, NEW_SCENE, false);
				}
				else {warnNoScene("showScene", sceneID);}
			}
		});
	}

	/**
	 * showSceneSplitXY by providing its sceneID and X and Y coordinates
	 * we will get the height and width of the scene and split it in half, then subtract half
	 * from the both coordinate. This allows you to have Switcher center the scene on a point vertically and horizontally.
	 * Switcher will remember the coordinates set so new calls to showScene will be in the same place.
	 *
	 * @param sceneID Integer
	 * @param stageX  Double
	 * @param stageY  Double
	 */
	public static void showSceneSplitXY(Integer sceneID, Double stageX, Double stageY) {
		Platform.runLater(() -> {
			if (stageX < 0 || stageY < 0) {System.err.println("Values for X and Y in showScene must not be negative");}
			else {
				if (sceneObjectMap.containsKey(sceneID)) {
					SceneObject so        = sceneObjectMap.get(sceneID);
					Double      newStageX = stageX - (so.getWidth() / 2);
					Double      newStageY = stageY - (so.getHeight() / 2);
					showSceneFinal(sceneID, null, null, newStageX, newStageY, NEW_SCENE, false);
				}
				else {warnNoScene("showScene", sceneID);}
			}
		});
	}

	/**
	 * @param sceneID Integer
	 * @param width   Double
	 * @param height  Double
	 * @deprecated use showScene(sceneID, width, height)
	 */
	public static void showSceneWithSize(Integer sceneID, Double width, Double height) {
		showScene(sceneID, width, height);
	}

	private static void showSceneFinal(Integer sceneID, Double width, Double height, Double stageX, Double stageY, boolean showingNewScene, boolean showMaximized) {
		if (sceneObjectMap.containsKey(sceneID)) {
			SceneObject sceneObject = sceneObjectMap.get(sceneID);
			Platform.runLater(() -> {
				if (width != null) sceneObject.setStageWidth(width);
				if (height != null) sceneObject.setStageHeight(height);
				if (stageX != null) sceneObject.setStageX(stageX);
				if (stageY != null) sceneObject.setStageY(stageY);
				showSceneObject(sceneID, showingNewScene, showMaximized);
			});
		}
		else {warnNoScene("showScene", sceneID);}
	}

	/**
	 * setTitle Set the title for this scene and it will get applied to the stage when shown.
	 *
	 * @param sceneID Integer
	 * @param title   String
	 */
	public static void setTitle(Integer sceneID, String title) {
		if (sceneObjectMap.containsKey(sceneID)) {
			sceneObjectMap.get(sceneID).setTitle(title);
		}
	}

	/**
	 * use getStage to gain access to any of the stages that you have
	 * in Switcher so that you can configure any option of the stage
	 * that is not offered with Switcher. if you pass null you will
	 * be given the default stage that applies to any scene that has
	 * not been assigned a stage. You can optionally use the getDefaultStage()
	 * method for the same result.
	 *
	 * @param stageID unique stageID
	 * @return Stage of given stageID or the default stage if stageID is null
	 * Switcher will return null if the stageID does not exist.
	 */
	public static Stage getStage(Integer stageID) {
		if (stageID == null) return getDefaultStage();
		if (stageMap.containsKey(stageID)) return stageMap.get(stageID);
		warnNoStage("getStage", stageID);
		return null;
	}

	/**
	 * use getStageForScene to pull the stage for any of your scenes
	 * so that you can modify it as needed.
	 * <p>
	 * Here is one example of how this could be used::
	 * getStageForScene(sceneID).setTitle("Stage Title");
	 *
	 * @param sceneID the unique ID of your scene
	 * @return Stage assigned to that Scene
	 */
	public static Stage getStageForScene(Integer sceneID) {
		Stage requestedStage = null;
		for (Integer sID : sceneObjectMap.keySet()) {
			Integer sceneStageID = sceneObjectMap.get(sID)
												 .getStageID();
			if (sID.equals(sceneID)) requestedStage = stageMap.get(sceneStageID);
		}
		return requestedStage;
	}

	/**
	 * use setDefaultStage to get access to the default stage
	 * This would mainly be used so that you can set the
	 * setOnCloseRequest option of the stage to have it
	 * call whatever method you want when the stage is asked to close.
	 *
	 * @return default Stage or null if you have not added any scenes.
	 */
	public static Stage getDefaultStage() {
		if (!stageMap.containsKey(defaultStageID)) stageMap.put(defaultStageID, new Stage());
		return stageMap.get(defaultStageID);
	}

	/**
	 * use setDefaultStage to assign your own stage to Switcher, for
	 * example, in your Main method, you might want to assign the initial
	 * primaryStage to Switcher so that it integrates better with other
	 * libraries you might be using such as FXTrayIcon or any libraries
	 * that use the defaultStage.
	 * <p>
	 * This needs to be the first Switcher command you execute before
	 * adding any scenes or Switcher will create it's own default Stage.
	 *
	 * @param stage is the stage you are assigning as default.
	 */
	public static void setDefaultStage(Stage stage) {
		if (stageMap.containsKey(defaultStageID)) {System.err.println("Switcher already has a default Stage, this must be the first command executed in your code.");}
		else {stageMap.put(defaultStageID, stage);}
	}

	/**
	 * Use getScene to gain access to the Scene that Switcher creates
	 * so that you can make changes to it as needed
	 *
	 * @param sceneID the sceneID of the scene you want
	 * @return Will return null if the sceneID does not exist
	 */
	public static Scene getScene(Integer sceneID) {
		if (sceneObjectMap.containsKey(sceneID)) {
			return sceneObjectMap.get(sceneID)
								 .getScene();
		}
		else {
			warnNoScene("getScene", sceneID);
			return null;
		}
	}

	/**
	 * setHideOnLostFocus lets you configure Switcher so that when the user
	 * clicks somewhere else on their desktop, the scene will hide itself.
	 * If you just pass in true or false, the behavior automatically applies
	 * to all scenes that you have inside Switcher. You can optionally pass
	 * the sceneID into the method to apply the behavior to specific scenes.
	 *
	 * @param hideOnLostFocus true / false
	 */
	public static void setHideOnLostFocus(boolean hideOnLostFocus) {
		allHiddenOnLostFocus = hideOnLostFocus;
		for (Integer index : sceneObjectMap.keySet()) {
			sceneObjectMap.get(index).setHiddenOnLostFocus(hideOnLostFocus);
		}
	}

	/**
	 * setHideOnLostFocus lets you configure Switcher so that when the user
	 * clicks somewhere else on their desktop, the scene will hide itself.
	 * If you just pass in true or false, the behavior automatically applies
	 * to all scenes that you have inside Switcher. You can optionally pass
	 * the sceneID into the method to apply the behavior to specific scenes.
	 *
	 * @param sceneID         unique sceneID Integer
	 * @param hideOnLostFocus true / false
	 */
	public static void setHideOnLostFocus(Integer sceneID, boolean hideOnLostFocus) {
		if (sceneObjectMap.containsKey(sceneID)) {
			sceneObjectMap.get(sceneID).setHiddenOnLostFocus(hideOnLostFocus);
		}
		else {warnNoScene("setHideOnLostFocus", sceneID);}
	}

	/**
	 * Call sceneHiddenOnLostFocus to find out if Switcher is configured to
	 * hide a specific scene when it loses focus.
	 *
	 * @param sceneID unique sceneID Integer
	 * @return true/false or null if sceneID does not exist
	 */
	public static Boolean sceneHiddenOnLostFocus(Integer sceneID) {
		if (sceneObjectMap.containsKey(sceneID)) {
			return sceneObjectMap.get(sceneID).hideOnLostFocus();
		}
		else {warnNoScene("sceneHiddenOnLostFocus", sceneID);}
		return null;
	}

	/**
	 * Call allHiddenObLostFocus to find out if Switcher
	 * is configured to hide ALL Scenes when they lose focus.
	 *
	 * @return true/false or null if sceneID does not exist
	 */
	public static Boolean allHiddenOnLostFocus() {return allHiddenOnLostFocus;}

	/**
	 * Call getWindow(sceneID) to quickly get the Window of the current
	 * Scene on stage.
	 *
	 * @param sceneID unique sceneID Integer
	 * @return Window of the current Scene
	 */
	public static Window getWindow(Integer sceneID) {
		return Objects.requireNonNull(getScene(sceneID))
					  .getWindow();
	}

	/**
	 * use this method to hide a Scene when you have more than
	 * one Stage set up in Switcher.
	 *
	 * @param sceneID Integer of your unique sceneID
	 */
	public static void hide(Integer sceneID) {
		hideScene(sceneID);
	}

	/**
	 * use this method to show a hidden Scene when you have
	 * more than one Stage set up in Switcher
	 *
	 * @param sceneID Integer of your unique sceneID
	 */
	public static void unHide(Integer sceneID) {
		show(sceneID);
	}

	/**
	 * @return true if Scene is currently being shown on screen
	 * @deprecated Use visible(stageID)
	 */
	public static boolean visible() {
		boolean response = false;
		for (Integer sceneID : sceneObjectMap.keySet()) {
			if (sceneObjectMap.get(sceneID).showing()) {
				response = true;
				break;
			}
		}
		return response;
	}

	/**
	 * Use visible(stageID) to find out if Switcher is currently showing any scene at all
	 *
	 * @return true if Scene is currently being shown on screen
	 */
	public static boolean visible(Integer sceneID) {
		boolean response = false;
		if (sceneObjectMap.containsKey(sceneID)) {
			response = sceneObjectMap.get(sceneID).showing();
		}
		return response;
	}

	/**
	 * use isShowing to find out if a particular scene is the one current being displayed on
	 * the screen. This is different than using visible, since it will tell you if a specific
	 * Scene is on the screen and not just whether the stage is visible or not.
	 *
	 * @param sceneID unique ID Integer
	 * @return true if showing, false if not
	 */
	public static boolean isShowing(Integer sceneID) {
		boolean response = false;
		if (sceneObjectMap.containsKey(sceneID)) {
			SceneObject scene = sceneObjectMap.get(sceneID);
			response = scene.showing();
		}
		return response;
	}

	/**
	 * Use getVisibleWithHistoryProperty to bind to a control that invokes the showLastScene method.
	 * For example: myButton.visibleProperty.bind(Switcher.getVisibleWithHistoryProperty());
	 * Your control will then be hidden when Switcher has no more scenes in its history to pull up.
	 *
	 * @return BooleanProperty
	 */
	public static BooleanProperty getVisibleWithHistoryProperty() {return visibleWithHistoryProperty;}

	/**
	 * Use getEnabledWithHistoryProperty to bind to a control that invokes the showLastScene method.
	 * For example: myButton.disableProperty().bind(Switcher.getEnabledWithHistoryProperty());
	 * Your control will then be disabled when Switcher has no more scenes in its history to pull up.
	 *
	 * @return BooleanProperty
	 */
	public static BooleanProperty getEnabledWithHistoryProperty() {return enabledWithHistoryProperty;}

	/**
	 * As your code invokes showScene to show the different Scenes that you have added into Switcher,
	 * A history of the SceneIDs that you have been showing are stacked up in a que, so that you can
	 * invoke showLastScene to show the Scene that was last displayed. You can continue calling this
	 * method until the first getScene displayed is reached. This is similar behavior to a Back button
	 * on a web browser.
	 */
	public static void showLastScene() {
		if (history.hasHistory()) {
			Integer lastSceneID = history.getLastSceneID();
			showSceneFinal(lastSceneID, null, null, null, null, PRIOR_SCENE, false);
		}
	}

	/**
	 * Use lastSceneAvailable to find out if there was a Scene showing that you
	 * could display buy invoking showLastScene. Sometimes you might be in a
	 * situation where your currently showing scene is the first one shown,
	 * but it offers the option of going back. Use this method to make sure
	 * you can go back.
	 *
	 * @return true if there is a previous scene to go back to.
	 */
	public static boolean lastSceneAvailable() {
		return history.hasHistory();
	}

	/**
	 * @param sceneId ID of a Scene managed by Switcher
	 * @param handler {@code EventHandler} to be invoked when Scene is shown
	 * @deprecated Use runOnShown
	 */
	public static void setOnShown(Integer sceneId, EventHandler<Event> handler) {
		runOnShown(sceneId, handler);
	}

	/**
	 * Registers an {@code EventHandler} on a Scene managed by Switcher.
	 * When Switcher shows this scene, the {@code EventHandler} will be executed.
	 *
	 * @param sceneId ID of a Scene managed by Switcher
	 * @param handler {@code EventHandler} to be invoked when Scene is shown
	 */
	public static void runOnShown(Integer sceneId, EventHandler<Event> handler) {
		if (sceneObjectMap.containsKey(sceneId)) {
			sceneObjectMap.get(sceneId).setShowEvent(handler);
		}
	}

	/**
	 * Registers an {@code EventHandler} on a Scene managed by Switcher.
	 * When Switcher hides this scene, the {@code EventHandler} will be executed.
	 *
	 * @param sceneId ID of a Scene managed by Switcher
	 * @param handler {@code EventHandler} to be invoked when Scene is shown
	 */
	public static void runOnHidden(Integer sceneId, EventHandler<Event> handler) {
		if (sceneObjectMap.containsKey(sceneId)) {
			sceneObjectMap.get(sceneId).setHideEvent(handler);
		}
	}

	private static void showSceneObject(Integer sceneID, boolean showingNewScene, boolean showMaximized) {
		SceneObject sceneObject = sceneObjectMap.get(sceneID);
		if (showingNewScene) {
			history.showingNewScene(sceneID);
		}
		visibleWithHistoryProperty.setValue(history.hasHistory());
		enabledWithHistoryProperty.setValue(!history.hasHistory());
		sceneObject.showScene(showMaximized);
	}

	private static void hideScene(Integer sceneID) {
		if (sceneObjectMap.containsKey(sceneID)) {
			getSceneObject(sceneID).hideScene();
		}
		else {warnNoScene("hide(sceneID)", sceneID);}
	}

	private static SceneObject getSceneObject(Integer sceneID)              {return sceneObjectMap.getOrDefault(sceneID, null);}

	private static void warnNoScene(String callingMethod, Integer sceneID)  {System.err.println("sceneID " + sceneID + " does not exist being called from method " + callingMethod);}

	private static void warnNoStage(String callingMethod, Integer stageID)  {System.err.println("stageID " + stageID + " does not exist being called from method " + callingMethod);}

	private static void customWarning(String callingMethod, String message) {System.err.println(callingMethod + ":" + message);}

	private static Integer getRandom() {
		int min = 1000000;
		int max = 9999999;
		Integer finalInt = ThreadLocalRandom.current()
											.nextInt(min, max + 1);
		while (randomInts.contains(finalInt) || stageMap.containsKey(finalInt)) {
			finalInt = ThreadLocalRandom.current()
										.nextInt(min, max + 1);
		}
		randomInts.add(finalInt);
		return finalInt;
	}

	private static void setPrimaryStage(Stage primaryStage) {
		if (Switcher.firstRun) {
			stageMap.put(defaultStageID, primaryStage);
			Switcher.firstRun = false;
		}
	}

	private static void addSceneObject(Integer sceneID, Integer stageID, Parent parent, Double width, Double height, Scene scene) {
		if (Switcher.firstRun) {
			if (!stageMap.containsKey(defaultStageID)) stageMap.put(defaultStageID, new Stage());
			Switcher.firstRun = false;
		}
		if (parent == null) {sceneObjectMap.put(sceneID, new SceneObject(stageID, scene, width, height, allHiddenOnLostFocus));}
		else {sceneObjectMap.put(sceneID, new SceneObject(stageID, parent, width, height, allHiddenOnLostFocus));}
		sceneObjectMap.get(sceneID)
					  .setHiddenOnLostFocus(allHiddenOnLostFocus);
	}

	private static void checkForIDConflict(Integer stageID) {
		if (randomInts.contains(stageID) || stageID.equals(defaultStageID)) {
			Integer oldDefaultStageID = defaultStageID;
			defaultStageID = getRandom();
			stageMap.put(defaultStageID, stageMap.get(oldDefaultStageID));
			stageMap.remove(oldDefaultStageID);
		}
	}

	private static Integer getSceneIDFromStageID(Integer stageID) {
		Integer response = -1;
		for (Integer sceneID : sceneObjectMap.keySet()) {
			SceneObject scene = sceneObjectMap.get(sceneID);
			if (scene.getStageID().equals(stageID)) {
				response = sceneID;
				break;
			}
		}
		return response;
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

	private final Scene                   scene;
	private       String                  title             = "";
	private       Integer                 stageID;
	private       Double                  width;
	private       Double                  height;
	private       Double                  stageX            = -1.0;
	private       Double                  stageY            = -1.0;
	private       boolean                 customXY          = false;
	private       boolean                 hideOnLostFocus;
	private       boolean                 hidden            = false;
	private       EventHandler<Event>     showEvent;
	private       EventHandler<Event>     hideEvent;
	private final ChangeListener<Boolean> lostFocusListener = (observable, oldValue, newValue) -> {
		if (!newValue) {
			hideScene();
		}
	};

	SceneObject(Integer stageID, Parent parent, Double width, Double height, boolean hideOnLostFocus) {
		this.stageID         = stageID;
		this.width           = (width == null) ? getStage().getWidth() : width;
		this.height          = (height == null) ? getStage().getHeight() : height;
		this.scene           = new Scene(parent);
		this.hideOnLostFocus = hideOnLostFocus;
	}

	SceneObject(Integer stageID, Scene scene, Double width, Double height, boolean hideOnLostFocus) {
		this.stageID         = stageID;
		this.width           = (width == null) ? getStage().getWidth() : width;
		this.height          = (height == null) ? getStage().getHeight() : height;
		this.scene           = scene;
		this.hideOnLostFocus = hideOnLostFocus;
	}

	private Stage getStage() {return Objects.requireNonNull(Switcher.getStage((this.stageID)));}

	private void setStageTitle() {
		if (!title.equals("")) getStage().setTitle(title);
	}

	public void setTitle(String title)                      {this.title = title;}

	public void setShowEvent(EventHandler<Event> showEvent) {this.showEvent = showEvent;}

	public void setHideEvent(EventHandler<Event> hideEvent) {this.hideEvent = hideEvent;}

	public boolean hideOnLostFocus() {
		return hideOnLostFocus;
	}

	public void setHiddenOnLostFocus(boolean hideOnLostFocus) {
		this.hideOnLostFocus = hideOnLostFocus;
		if (Switcher.getStage(this.stageID) != null) {
			if (hideOnLostFocus) {
				Objects.requireNonNull(getStage()).focusedProperty().addListener(lostFocusListener);
			}
			else {
				Objects.requireNonNull(getStage()).focusedProperty().removeListener(lostFocusListener);
			}
		}
	}

	public Scene getScene()                 {return scene;}

	public Integer getStageID()             {return this.stageID;}

	public void setStageID(Integer stageID) {this.stageID = stageID;}

	public Double getWidth()                {return width;} // Returns width of stage

	public Double getHeight()               {return height;} // Returns height of stage

	public void setStageX(Double stageX) {
		this.stageX = stageX;
		customXY    = true;
	}

	public void setStageY(Double stageY) {
		this.stageY = stageY;
		customXY    = true;
	}

	public void setStageWidth(Double width) {
		this.width = width;
	}

	public void setStageHeight(Double height) {
		this.height = height;
	}

	public void hideScene() {
		Platform.runLater(() -> getStage().hide());
		this.hidden = true;
		if (hideEvent != null) {
			hideEvent.handle(new ActionEvent());
		}
	}

	public void showScene(boolean showMaximized) {
		setStageTitle();
		if (!hidden) {
			getStage().setWidth(width);
			getStage().setHeight(height);
			getStage().setMaximized(showMaximized);
			if (!showMaximized) {
				if (!customXY) {
					Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
					stageX = (bounds.getWidth() / 2) - (this.width / 2);
					stageY = (bounds.getHeight() / 2) - (this.height / 2);
				}
				getStage().setWidth(width);
				getStage().setHeight(height);
				getStage().setX(stageX);
				getStage().setY(stageY);
			}
			getStage().setScene(scene);
		}
		Platform.runLater(() -> {
			getStage().show();
			getStage().toFront();
			getStage().requestFocus();
		});
		this.hidden = false;
		if (showEvent != null) {
			showEvent.handle(new ActionEvent());
		}
	}

	public boolean showing() {return !hidden;}
}

/**
 * This class simply maintains the history of
 * scenes as they are shown so that Switcher can
 * easily go back to the previously shown
 * getScene when showLastScene is invoked.
 * This class is private to Switcher.
 */
class HistoryKeeper {

	private final Map<Integer, Integer> pastSceneIDs = new HashMap<>();
	private       Integer               lastSceneID  = null;
	private       int                   index;

	public HistoryKeeper() {
		index = 0;
	}

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