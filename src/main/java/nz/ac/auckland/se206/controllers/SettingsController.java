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
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import nz.ac.auckland.se206.controllers.CanvasController.GameMode;
import nz.ac.auckland.se206.scenes.SceneManager;
import nz.ac.auckland.se206.user.User;
import nz.ac.auckland.se206.words.CategorySelector.Difficulty;

public class SettingsController {

  // @FXML private Button startButton;
  @FXML private ChoiceBox<String> accuracyChoiceBox;
  @FXML private ChoiceBox<String> wordsChoiceBox;
  @FXML private ChoiceBox<String> timeChoiceBox;
  @FXML private ChoiceBox<String> confidenceChoiceBox;
  @FXML private Button zenButton;
  @FXML private Button classicButton;
  @FXML private Button hiddenButton;
  //  private Difficulty[] difficulty = {Difficulty.E, Difficulty.M, Difficulty.H, Difficulty.Ma};

  private final Map<String, Difficulty> difficulties =
      Map.of(
          "Easy", Difficulty.E,
          "Medium", Difficulty.M,
          "Hard", Difficulty.H,
          "Master", Difficulty.Ma);

  private final Map<Difficulty, String> difficultyNames =
      Map.of(
          Difficulty.E, "Easy",
          Difficulty.M, "Medium",
          Difficulty.H, "Hard",
          Difficulty.Ma, "Master");

  User user;

  public void initialize() {
    for (int i = 0; i < 3; i++) {
      accuracyChoiceBox.getItems().add(difficulties.keySet().toArray()[i].toString());
    }
    // accuracy.getItems().addAll(difficulty);
    // accuracy.setValue(Difficulty.E);
    wordsChoiceBox.getItems().addAll(difficulties.keySet());
    // words.setValue(Difficulty.E);
    timeChoiceBox.getItems().addAll(difficulties.keySet());
    // time.setValue(Difficulty.E);
    confidenceChoiceBox.getItems().addAll(difficulties.keySet());
    // confidence.setValue(Difficulty.E);
  }

  public void savedSettings() {
    if (user.getDifficulty().size() == 0) {
      accuracyChoiceBox.setValue("Easy");
      wordsChoiceBox.setValue("Easy");
      timeChoiceBox.setValue("Easy");
      confidenceChoiceBox.setValue("Easy");
    } else {
      accuracyChoiceBox.setValue(difficultyNames.get(user.getDifficulty().get(0)));
      wordsChoiceBox.setValue(difficultyNames.get(user.getDifficulty().get(1)));
      timeChoiceBox.setValue(difficultyNames.get(user.getDifficulty().get(2)));
      confidenceChoiceBox.setValue(difficultyNames.get(user.getDifficulty().get(3)));
    }
  }

  public void setUser(User u) {
    user = u;
  }

  @FXML
  public void onStart(ActionEvent event) throws Exception {
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

  @FXML
  private void onStartClassic(ActionEvent event) throws Exception {
    CanvasController controller = startGame(event);
    controller.setGameMode(GameMode.CLASSIC);
    controller.speak();
  }

  @FXML
  private void onStartZen(ActionEvent event) throws Exception {
    CanvasController controller = startGame(event);
    controller.setGameMode(GameMode.ZEN);
    controller.speak();
    controller.startZen();
  }

  @FXML
  private void onStartHidden(ActionEvent event) throws Exception {
    CanvasController controller = startGame(event);
    controller.setGameMode(GameMode.HIDDEN);
    controller.searchDefinition();
  }

  private CanvasController startGame(ActionEvent event) throws Exception {
    updateSettings();
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    SceneManager.addUi(SceneManager.AppUi.CANVAS, loadFxml("canvas"));
    CanvasController controller =
        (CanvasController) SceneManager.getUiController(SceneManager.AppUi.CANVAS);
    controller.setUser(user);
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.CANVAS));
    return controller;
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
    userDifficulty.add(difficulties.get(accuracyChoiceBox.getValue()));
    userDifficulty.add(difficulties.get(wordsChoiceBox.getValue()));
    userDifficulty.add(difficulties.get(timeChoiceBox.getValue()));
    userDifficulty.add(difficulties.get(confidenceChoiceBox.getValue()));
    userList.get(count).setDifficulty(userDifficulty);
    mapper.writeValue(new File(".profiles/users.json"), userList);
    user = userList.get(count);
  }
}
