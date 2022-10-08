package nz.ac.auckland.se206.controllers;

import static nz.ac.auckland.se206.App.loadFxml;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import nz.ac.auckland.se206.scenes.SceneManager;
import nz.ac.auckland.se206.user.User;
import nz.ac.auckland.se206.words.CategorySelector.Difficulty;

public class SettingsController {
  @FXML private Button startButton;
  @FXML private ChoiceBox<Difficulty> accuracy;
  @FXML private ChoiceBox<Difficulty> words;
  @FXML private ChoiceBox<Difficulty> time;
  @FXML private ChoiceBox<Difficulty> confidence;
  private Difficulty[] difficulty = {Difficulty.E, Difficulty.M, Difficulty.H};
  User user;

  public void initialize() {
    accuracy.getItems().addAll(difficulty);
    // accuracy.setValue(Difficulty.E);
    words.getItems().addAll(difficulty);
    // words.setValue(Difficulty.E);
    time.getItems().addAll(difficulty);
    // time.setValue(Difficulty.E);
    confidence.getItems().addAll(difficulty);
    // confidence.setValue(Difficulty.E);
  }

  public void savedSettings() {
    if (user.getDifficulty().size() == 0) {
      accuracy.setValue(Difficulty.E);
      words.setValue(Difficulty.E);
      time.setValue(Difficulty.E);
      confidence.setValue(Difficulty.E);
    } else {
      accuracy.setValue(user.getDifficulty().get(0));
      words.setValue(user.getDifficulty().get(1));
      time.setValue(user.getDifficulty().get(2));
      confidence.setValue(user.getDifficulty().get(3));
    }
  }

  public void setUser(User u) {
    user = u;
  }

  @FXML
  public void onStart(ActionEvent event) throws IOException {
    updateSettings();
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    SceneManager.addUi(SceneManager.AppUi.CANVAS, loadFxml("canvas"));
    CanvasController controller =
        (CanvasController) SceneManager.getUiController(SceneManager.AppUi.CANVAS);
    controller.setUser(user);
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.CANVAS));
    controller.speak();
  }

  public void updateSettings() throws StreamReadException, DatabindException, IOException {
    ObjectMapper mapper = new ObjectMapper();

    // List of users read from json file
    List<User> userList =
        mapper.readValue(new File(".profiles/users.json"), new TypeReference<List<User>>() {});
    int count = 0;
    for (User u : userList) {
      if (user.getName().equals(u.getName())) {
        break;
      }
      count++;
    }
    ArrayList<Difficulty> userDifficulty = new ArrayList<>();
    userDifficulty.add(accuracy.getValue());
    userDifficulty.add(words.getValue());
    userDifficulty.add(time.getValue());
    userDifficulty.add(confidence.getValue());
    userList.get(count).setDifficulty(userDifficulty);
    mapper.writeValue(new File(".profiles/users.json"), userList);
    user = userList.get(count);
  }
}
