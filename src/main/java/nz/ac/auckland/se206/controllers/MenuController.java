package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.speech.TextToSpeech;

import java.util.ArrayList;
import java.util.List;

public class MenuController {
@FXML
private HBox hboxx;
@FXML
private Button test;
  private TextToSpeech tts = new TextToSpeech();
@FXML
  public void view(ActionEvent event){
    List<Button> buttonlist = new ArrayList<>();
    buttonlist.add(new Button("Monkey"));
    buttonlist.add(new Button("Monkey"));
    buttonlist.add(new Button("Monkey"));
    //hboxx.getChildren().clear();
  System.out.println("balls");
    hboxx.getChildren().addAll(buttonlist);
  }
  @FXML
  private void onSwitchToReady(ActionEvent event) {
    // Changes scene
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.CANVAS));

    javafx.concurrent.Task<Void> task =
        new javafx.concurrent.Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // Uses Text to speech to speak given lines
            tts.speak(
                "Welcome to speed sketchers",
                "You will have sixty seconds to draw",
                "Press the button on the canvas to start drawing");
            return null;
          }
        };
    // Delegates speaking task to new thread to prevent blocking of GUI
    Thread thread = new Thread(task);
    thread.start();
  }
  @FXML
    private void onAdd(ActionEvent event){
      Button button = (Button) event.getSource();
      Scene sceneButtonIsIn = button.getScene();
      sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.PROFILE));
  }
}
