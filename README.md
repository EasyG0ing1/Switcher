# Switcher

Library intended for use in JavaFX applications that makes adding and switching Scenes easy.
The Switcher library can be used to quickly add Scenes to your project, and offers a single
line of code method to switch out scenes on the fly without all the messy code that goes along
with managing Scenes in JavaFX. You simply assign a unique sceneID (int) to your scene,
and Switcher takes care of the rest!

There are also three [completely runnable test applications](./src/test/) under Test that
show how to use Switcher from the most common and simple ways to the Lets Get Nuts ways
which shows you how to implement different stages and assign them to your scenes and
then with grace and style, you just show the scene with a single and simple line of code.<br><br>
---
### 1.3 Changes and Additions at the end of this document

---

## Usage

From within your JavaFX application, You simply add the scene to switcher by supplying a
sceneID, Parent, width and height and it's ready to use! You do not need to ever worry
about managing the Stage, as Switcher handles that for you! But if you need to also
use different stages, we got you covered there as well!

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


## But what about MY Stages and Scenes?

If you just want to use Switcher to manage the Scenes and Stages that you build
and configure yourself, and be able to easily show them from any class in your program,
just add them like this:
```java
Switcher.addScene(sceneID, Scene, Stage);
```

And show them from any class in your application like this:

```java
Switcher.showScene(sceneID);
```

Switcher takes care of everything for you!

---

## How do I add to my project

The project is available as a Maven dependency on Central. Add the following to POM.xml

```xml
<dependency>
    <groupId>com.simtechdata</groupId>
    <artifactId>Switcher</artifactId>
    <version>1.3.3</version>
</dependency>
```

Or, if using Gradle to build, add this to your Gradle build file

```groovy
compile group: 'com.simtechdata', name: 'Switcher', version: 1.3.3
```

You can even use it from a Groovy script!

```groovy
@Grapes(
  @Grab(group='com.simtechdata', module='Switcher', version=1.3.3)
)
```



## Additional Features

Switcher has more features, making it even more useful in your applications. For example:

### History
Switcher maintains a history of scenes as you show them. This will let you implement a control
such as a Button, to just go back to the last shown Scene. You can continue going back to the
first scene that you brought up. The proper way to engage this feature, is to create your
control that will invoke showLastScene(). Because invoking the last scene will do nothing
when you are at the first scene shown, Switcher offers two different BooleanProperties that
you can bind to your controls visibleProperty or enabledProperty. Here is how you would set it up.

```java
Button button = new Button("Previous");
button.setOnAction(e-> Switcher.showLastScene());
button.disableProperty().bind(Switcher.getEnabledWithHistoryProperty());
```

This configuration will simply disable the button when the user is at the first scene shown
without any history in the que. Alternatively you could do this to hide the button when there
is no history to go to.

```java
button.visibleProperty().bind(Switcher.getVisibleWithHistoryProperty());
```

### Hiding on lost focus

With some application styles, it might be useful to automatically hide the scene when the user has
clicked off of it and onto some other window or program on their computer. When you enablke this 
feature, Switcher assigns a ChangeListener to the stage and it hides itself when it has lost focus.
You can optionally apply this behavior to selected scenes and Switcher keeps track of all that and
implements the behavior that you need. If you do not include a sceneID, Switcher will apply this
feature to all scenes that you have added to Switcher.

```java
Switcher.setHideSceneOnLostFocus(true);
Switcher.setHideSceneOnLostFocus(sceneID, true);
```

OR, you can hide the scene yourself

```java
Switcher.hide();
```

And bring it right back
```java
Switcher.unHide();
```

You can also find out if a specific scene is the currently displayed scene
```java
Switcher.isShowing(sceneID);
```

Or if Switcher is actively showing a scene at all
```java
Switcher.visible();
```

### Size and Position
By default, Switcher will always show the scene in the center of the screen. Even if the user
changes the screen resolution, each call to showScene will re-calculate the correct position.<BR>
You can alternatively decide where the scene is placed by setting the X and Y coordinates of the upper left
corner of the stage when you show the scene. You can even change the width and height of the scene that
you originally set when you added the scene.<BR>
These setting will persist with subsequent calls to showScene for that sceneID without needing to
pass these values again.

```java
Switcher.showScene(sceneID, stageX, stageY);
Switcher.showScene(sceneID, width, height, stageX, stageY);
Switcher.showSceneWithSize(sceneID, width, height);
Switcher.showSceneWithPosition(sceneID, stageX, stageY);
```

## Trigger Methods
You can now assign an event to a scene, so that when you call showScene, that event will trigger.
```java
Switcher.setOnShown(C.FORM_THREE, e->{
	myCode();
});
```


## Advanced Features

### Custom Stages

You can include with each Scene, a different stage to show the Scene on. This can be handy
if you need different stages with different StageStyles or Modality's. Just like with Scenes,
you need to provide a stageID for each different Stage that you want to assign to a given Scene.
The most basic way to do this is like this:

```java
Switcher.addScene(sceneID, stageID, Parent, width, height);
Switcher.addScene(sceneID, stageID, Parent, width, height, StageStyle);
Switcher.addScene(sceneID, stageID, Parent, width, height, Modality);
Switcher.addScene(sceneID, stageID, Parent, width, height, StageStyle, Modality);
```

Optionally, you can build your stage before hand, then add it to Switcher like this:
```java
Switcher.addStage(stageID, Stage);
```

Then you simply add Scenes and reference the stageID in the add method
```java
Switcher.addScene(sceneID, stageID, Parent, width, height);
```

Or you can later assign a Stage to a scene after you've already added the Scene
```java
Switcher.assignSceneToStage(sceneID, stageID);
```

Note: The StageStyle and Modality CANNOT be modified once the stage has been shown.
Therefore, it is necessary to set up your stages before showing your scenes.

### Removing A Scene or a Stage

If you need to, you can also remove a scene from Switcher.
```java
Switcher.removeScene(sceneID);
Switcher.removeStage(stageID);
```

If you remove a stage that has been assigned to one or more scenes, Switcher will
remove that stage assignment from those connected Scenes and will default them back
to the default Stage.

### Default Stage Configuration

As of Switcher version 1.3.2, you can assign the default Stage to Switcher. This could be
useful to keep consistency fluid with other libraries that might rely on you launch classes
primaryStage. You would need to make this your first Switcher command:


```java
Switcher.setDefaultStage(primaryStage);
```


If you're just going to use the default stage that Switcher creates when you add
a scene using the basic addScene method call (or if you manually set the default Stage),
you can optionally configure that default stages StageStyle and Modality, but this must
be done before ever calling showScene. If you only wish to set one of the two, then set
the other one to null.

```java
Switcher.configureDefaultStage(StageStyle, Modality);
```

If you only want to set one of those two options, then simply pass null into the other
one, and it will use the default that Java assigns.
```java
Switcher.configureDefaultStage(StageStyle, null);
```

### What about the stages OnCloseRequest?

I'm glad you asked, because you can gain access to any of the stages, including the
default stage so that you can assign the setOnCloseRequest method.
```java
Switcher.getStage(stageID).setOnCloseRequest(e-> closeApp());
Switcher.getDefaultStage().setOnCloseRequest(e-> closeApp());
```

##  Example Programs
In the test folder are runnable programs. You simply run the Main method
from any of the packages to see examples of different ways to use Switcher. Also, in the
Main class, I have included useful comments that explains everything relevant to that
packages implementation style.

Check out the **Lets Get Nuts** package for the most versatile way of working with Switcher.

## Supported operating systems

Any operating system that supports JavaFX will support Switcher.

---

## Projects using `Switcher`

If your project uses Switcher, let us know via Pull Request, and we'll feature your project on this README.

---
## 1.3 Changes and Additions
- ###Fixed:
  * **showScene()** method now consistently re-sizes the Scene being shown to it's defined parameters.<br><br>
- ###Added:<br>
  * **`init(primaryStage)`** method for those situations where you want to set your applications initial primaryStage as Switchers defaiult Stage.<br>
  * **`setPrimary(primaryStage)`** same as above, just a different word - options never hurt.<br>
  * **`getWindow(sceneID)`** method for a more streamlined way to attain a Scenes Window. Particularly useful for things like showDialogue() or when showing a **FileChoser** or a **DirectoryChoser**.<br>
  * **`hide(sceneID)`**  - overloaded method so that you can select which Scene you want to hide. Useful in situations where you have multiple Stages showing at the same time. By hiding a specific sceneID, Switcher will automatically get the stage that is showing that Scene then hide it. The usual hide() method works as it always has, by hiding the defaultStage.<br>
  * **`show(sceneID)`** and `unHide(sceneID)`  - overloaded method so that you can show the specific scene you previously hid. If you didn't previously hide the scene, you will get an error telling you to use the showScene method.<br>
  * **`getStageForScene(sceneID)`** - Lets you pull the Stage assigned to any of your Scenes so that you can modify them as needed.<br>

- ###Depreciated methods removed:<br>
  * **setHideSceneOnLostFocus()** - use **setHideOnLostFocus()** instead which also has options for specific sceneIDs.<br>
  * **sceneVisible()** - use **visible()** instead.<br>
  * **setSceneVisible(boolean)** - use **show()**, **hide()**, or **unHide()** as you prefer.<br>
    * The **show()** and **unHide()** methods simply makes the Stage that was hidden with the hide() method - visible again.<br>
    * **show()**, **hide()** and **unhide()** will do nothing if you have multiple Stages set up in Switcher.<br>
      * Use **show(sceneID)**, **hide(sceneID)** and **unHide(sceneID)** instead.<br>
      * As always, use **showScene(sceneID)** to display a specific Scene.<br>
---
##Coming Soon:
- Ratios! - The ability to set the size of your Scenes based on a percentage of the users window resolution. This will be great as it will dynamically size your Scenes based on the ratios you set ... no more tiny windows on those large displays or oversized Scenes on smaller resolutions. It will be particularly useful in virtual machine environments where screen sizes are all over the map. It will also take into account those ultrawide monitors so as to not go overboard if you desire that behavior.<br><br>
- ####Comments, criticisms, suggestions etc. are **ALWAYS** welcome! Create an issue or a pull request to participate in the project.