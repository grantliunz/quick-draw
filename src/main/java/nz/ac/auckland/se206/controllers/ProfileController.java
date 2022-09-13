package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.User;

public class ProfileController {
  @FXML Button add;
  @FXML TextField name;

  @FXML
  private void onAdd(ActionEvent event) throws Exception {
    MenuController.userList.add(new User(name.getText()));

    MenuController controller =
        (MenuController) SceneManager.getUiController(SceneManager.AppUi.MENU);
    controller.view();

    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.MENU));
  }
}
