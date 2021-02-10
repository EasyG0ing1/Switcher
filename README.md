# Switcher

Library intended for use in JavaFX applications that makes adding and switching Scenes easy.
The Switcher class can be used to quickly add Scenes to your project, and offers easy to 
use methods to switch out scenes on the fly without all the messy code that goes along
with managing Scenes in JavaFX. You simply assign a unique sceneID (int) to your scene,
and Switcher takes care of the rest!

Check out the [runnable test application](./src/test/java/com/simtechdata/switcher/MultiSceneWithPreviousTest.java) in the test directory for an example of how Switcher works.

---

## Usage

From within your JavaFX application, You simply need to define a sceneID number, then
add it to Switcher with the Parent, and the width and height of the Scene (with optional
parameters) and it's ready to use! You do not need to ever worry about managing the
Stage, as Switcher handles that for you!

```java
int sceneID = 1;
AnchorPane ap = new AnchorPane();
Switcher.addScene(sceneID,ap,200,200);
```

Then, when you're ready to show your scene, you simply
```java
Switcher.showScene(sceneID);
```

And the rest is handled for you! Even if your scenes have different dimensions,
Switcher will properly handle the Stage to accommodate the scenes settings. 

---

## How do I add to my project

The project is available as a Maven dependency on Central. Add the following to POM.xml

```xml
<dependency>
  <groupId>com.simtechdata.switcher</groupId>
  <artifactId>Switcher</artifactId>
  <version>1.0.0</version>
</dependency>
```


## Additional Features

Switcher has more features, making it even more useful in your applications. For example:

### History
Switcher maintains a history of scenes as you show them. This will let you implement a control
such as a Button, to just go back to the last shown Scene. You can continue going back to the 
first scene that you brought up. The proper way to engage this feature, is to create your 
control that will invoke showLastScene(). Because invoking the last scene will do nothing
when you are at the first scene shown, Switcher offers to different BooleanProperties that
you can bind to your controls visibleProperty or enabledProperty. Here is how you would set it up.

```java
Button button = new Button("Previous");
button.setOnAction(e-> Switcher.showLastScene());
button.disableProperty().bind(Switcher.getNoHistoryProperty());
```

This configuration will simply disable the button when the user is at the first scene shown
without any history in the que. Alternatively you could do this to hide the button when there
is no history to go to.

```java
button.visibleProperty().bind(Switcher.getHasHistoryProperty());
```

### Hiding on lost focus

With some application styles, it might be useful to automatically hide the scene when the user has 
clicked off of it and onto some other window or program on their computer. You can enable a
thread that monitors for lost focus on the stage and when it loses focus, it hides itself.

```java
Switcher.setHideSceneOnLostFocus(true);
```

OR, you can hide the scene yourself

```java
Switcher.setSceneVisible(false);
```

And bring it right back by setting it true;

You can also get the current status of those settings
```java
Switcher.sceneVisible();
Switcher.sceneHiddenOnLostFocus();
```

### Size and Position
By default, Switcher will always show the scene in the center of the screen. However, you
can decide where the scene is placed by selecting the X and Y coordinates of the upper left
corner of the scene when you show the scene, OR, you can even change the width and height
that you set when you added the scene.

```java
Switcher.showScene(sceneID, stageX, stageY);
Switcher.showScene(sceneID, width, height, stageX, stageY);
```

### Style and Modality

You can set the style of the stage or its modality when you add the scene to Switcher.

```java
Switcher.addScene(sceneID, parent, width, height, Modality.WINDOW_MODAL, StageStyle.TRANSPARENT);
Switcher.addScene(sceneID, parent, width, height, Modality.WINDOW_MODAL);
Switcher.addScene(sceneID, parent, width, height, StageStyle.TRANSPARENT);
```

These are just some of the ways you can define the scene when you add it to Switcher. Check your 
IDEs code completion for a complete list of options. 

### Removing A Scene

If you need to, you can also remove a scene from Switcher.

```java
Switcher.removeScene(sceneID);
```

###  Example Programs
Check the Test folder for some example applications that use Switcher. I added CommonUsage.java 
to show you how I typically use Switcher. This project was an evolution of sorts over time. 
I wrote it to make this process easy and CommonUsage will show you how I typically use this library.
The other test app, MultiSceneWithPreviousTest, will demonstrate how to switch between three different
scenes, and also use the showLastScene() method.

## Supported operating systems

Any operating system that supports JavaFX will support Switcher.

---

## Projects using `Switcher`

If your project uses Switcher, let us know via Pull Request, and we'll feature your project on this README.
