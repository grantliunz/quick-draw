package nz.ac.auckland.se206.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import nz.ac.auckland.se206.scenes.SceneManager;
import nz.ac.auckland.se206.user.User;

public class ProfileController {
  @FXML private TextField name;

  @FXML
  private void onAdd(ActionEvent event) throws Exception {
    User newUser = new User(name.getText());
    MenuController.userList.add(newUser);
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(new File("src/main/resources/users.json"), MenuController.userList);

    MenuController controller =
        (MenuController) SceneManager.getUiController(SceneManager.AppUi.MENU);
    controller.view();

    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.MENU));
  }
}
