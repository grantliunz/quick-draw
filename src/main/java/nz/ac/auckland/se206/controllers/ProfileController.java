package nz.ac.auckland.se206.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import nz.ac.auckland.se206.scenes.SceneManager;
import nz.ac.auckland.se206.scenes.SceneManager.AppUi;
import nz.ac.auckland.se206.user.User;

public class ProfileController {

  @FXML private TextField name;

  /**
   * Upon pressing the add button if the name is blank give an error
   *
   * @param event on button pressing
   * @throws Exception the inputted name is blank
   */
  @FXML
  private void onAdd(ActionEvent event) throws Exception {

    // Gives the user an error is attempting to create profile with no name
    if (name.getText().isBlank()) {
      Alert a = new Alert(AlertType.ERROR);
      a.setHeaderText("Please enter a name");
      a.show();
      return;
    }

    // Gets user data from json file
    ObjectMapper mapper = new ObjectMapper();
    File file = new File(".profiles/users.json");
    if (file.length() != 0) {
      MenuController.userList = mapper.readValue(file, new TypeReference<List<User>>() {});
    }
    // Creates new users and saves
    User newUser = new User(name.getText());
    MenuController.userList.add(newUser);
    mapper.writeValue(new File(".profiles/users.json"), MenuController.userList);

    // Changes scene back to menu
    MenuController controller =
        (MenuController) SceneManager.getUiController(SceneManager.AppUi.MENU);
    controller.view();

    name.clear();
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.MENU));
  }

  /**
   * If the cancel button is pressed go back to the menu ui
   *
   * @param event button is pressed event
   */
  @FXML
  private void onCancel(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.MENU));
  }
}
