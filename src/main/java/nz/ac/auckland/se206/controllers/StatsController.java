package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.scenes.SceneManager;
import nz.ac.auckland.se206.user.User;

public class StatsController {

  private User user;

  @FXML private Label headerLabel;

  @FXML
  private void onReturn(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.MENU));
  }

  public void updateStats(User user) {
    this.user = user;
    headerLabel.setText(user.getName() + "'s Stats");
  }
}
