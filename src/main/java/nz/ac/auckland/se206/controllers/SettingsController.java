package nz.ac.auckland.se206.controllers;

import static nz.ac.auckland.se206.App.loadFxml;
import static nz.ac.auckland.se206.controllers.StatsController.getGamesWonOrLoss;
import static nz.ac.auckland.se206.util.BadgeUtil.getGamemodeWins;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import nz.ac.auckland.se206.controllers.CanvasController.GameMode;
import nz.ac.auckland.se206.scenes.SceneManager;
import nz.ac.auckland.se206.user.Data;
import nz.ac.auckland.se206.user.Data.Result;
import nz.ac.auckland.se206.user.User;
import nz.ac.auckland.se206.words.CategorySelector.Difficulty;

public class SettingsController {

  @FXML private ChoiceBox<String> accuracyChoiceBox;
  @FXML private ChoiceBox<String> wordsChoiceBox;
  @FXML private ChoiceBox<String> timeChoiceBox;
  @FXML private ChoiceBox<String> confidenceChoiceBox;

  @FXML private Button hiddenButton;
  @FXML Button zenButton;
  @FXML private Pane hiddenPane;
  @FXML private Pane zenPane;

  private List<Difficulty> difficultyList = Arrays.asList(Difficulty.values());
  private List<String> difficultyNames = Arrays.asList("Easy", "Medium", "Hard", "Master");

  private User user;

  /** this method adds all the difficulties of the user settings */
  public void initialize() {
    for (int i = 0; i < 3; i++) {
      accuracyChoiceBox.getItems().add(difficultyNames.get(i));
    }

    accuracyChoiceBox.setStyle("-fx-font-size: 20px;");
    wordsChoiceBox.getItems().addAll(difficultyNames);
    wordsChoiceBox.setStyle("-fx-font-size: 20px;");
    timeChoiceBox.getItems().addAll(difficultyNames);
    timeChoiceBox.setStyle("-fx-font-size: 20px;");
    confidenceChoiceBox.getItems().addAll(difficultyNames);
    confidenceChoiceBox.setStyle("-fx-font-size: 20px;");
  }

  /** this methods checks for saved settings and updates the labels accordingly */
  public void savedSettings() {
    // checking whether user settings have data or not
    if (user.getDifficulty().size() == 0) {
      accuracyChoiceBox.setValue("Easy");
      wordsChoiceBox.setValue("Easy");
      timeChoiceBox.setValue("Easy");
      confidenceChoiceBox.setValue("Easy");
      // given it does the settings are retrieved to be shown
    } else {
      accuracyChoiceBox.setValue(
          difficultyNames.get(difficultyList.indexOf(user.getDifficulty().get(0))));
      wordsChoiceBox.setValue(
          difficultyNames.get(difficultyList.indexOf(user.getDifficulty().get(1))));
      timeChoiceBox.setValue(
          difficultyNames.get(difficultyList.indexOf(user.getDifficulty().get(2))));
      confidenceChoiceBox.setValue(
          difficultyNames.get(difficultyList.indexOf(user.getDifficulty().get(3))));
    }
    ArrayList<Data> gameData = user.getData();
    ArrayList<Data> gamesWon = getGamesWonOrLoss(gameData, Result.WIN);

    int classicWins = getGamemodeWins(gamesWon, GameMode.CLASSIC);
    int hiddenWins = getGamemodeWins(gamesWon, GameMode.HIDDEN);
    if (classicWins < 5) {
      hiddenButton.setDisable(true);
      Tooltip t =
          new Tooltip("Win " + (5 - classicWins) + " more \n Classic Mode games\n to unlock");
      setTooltip(t, hiddenPane);
    } else {
      hiddenButton.setDisable(false);
      Tooltip.install(hiddenPane, null);
    }

    if (hiddenWins < 5) {
      zenButton.setDisable(true);
      Tooltip t = new Tooltip("Win " + (5 - hiddenWins) + " more \n Hidden Mode games\n to unlock");
      setTooltip(t, zenPane);
    } else {
      zenButton.setDisable(false);
      Tooltip.install(zenPane, null);
    }
  }

  /**
   * Method for when you hover over ZEN mode and it is not unlocked Displays the text to tell you to
   * play more games
   *
   * @param t Tooltip to be written on for the object
   * @param pane pane in which button is in
   */
  private void setTooltip(Tooltip t, Pane pane) {
    t.setShowDelay(javafx.util.Duration.millis(0));
    t.setStyle("-fx-font-size: 20");
    t.setOnShowing(
        s -> {
          Bounds bounds = pane.localToScreen(pane.getBoundsInLocal());
          t.setX(bounds.getMinX());
          t.setY(bounds.getMaxY() - t.getHeight());
        });
    Tooltip.install(pane, t);
  }

  public void setUser(User u) {
    user = u;
  }

  /**
   * Upon pressing the classic game button load the canvas with the classic settings
   *
   * @param event Button is pressed
   * @throws Exception Thrown is an exception
   */
  @FXML
  private void onStartClassic(ActionEvent event) throws Exception {
    CanvasController controller = startGame(event);
    controller.setGameMode(GameMode.CLASSIC);
    CanvasController.playSound("/sounds/mixkit-arcade-game-complete-or-approved-mission-205.mp3");
  }

  /**
   * Upon pressing the ZEN game button load the canvas with the zen mode settings
   *
   * @param event button pressure applied
   * @throws Exception An exception is thrown
   */
  @FXML
  private void onStartZen(ActionEvent event) throws Exception {
    CanvasController controller = startGame(event);
    controller.setGameMode(GameMode.ZEN);
    controller.startZen();
    CanvasController.playSound("/sounds/mixkit-arcade-game-complete-or-approved-mission-205.mp3");
  }

  /**
   * Upon pressing the hidden mode button load the canvas with the hidden mode settings
   *
   * @param event button pressing event
   * @throws Exception Error loading UI
   */
  @FXML
  private void onStartHidden(ActionEvent event) throws Exception {
    CanvasController controller = startGame(event);
    controller.setGameMode(GameMode.HIDDEN);

    CanvasController.playSound("/sounds/mixkit-arcade-game-complete-or-approved-mission-205.mp3");

    controller.startHidden();
  }

  /**
   * On pressing the home button, return to the main menu
   *
   * @param event Button pressed event moment
   */
  @FXML
  private void onReturn(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.MENU));
  }

  /**
   * Upon pressing a gamemode button start the game and load the canvas ui with the settings
   * selected
   *
   * @param event button pressing
   * @return the canvas controller settings given by the user
   * @throws Exception error switching UI
   */
  private CanvasController startGame(ActionEvent event) throws Exception {
    // updates settings so canvas changes functionality accordingly

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

  /**
   * updates the settings chosen by the user before going to canvas
   *
   * @throws IOException error reading from the user file
   */
  public void updateSettings() throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    // List of users read from json file
    List<User> userList =
        mapper.readValue(new File(".profiles/users.json"), new TypeReference<List<User>>() {});
    int count = 0;
    // finds the user from the user list to update settings list
    for (User u : userList) {
      if (user.getName().equals(u.getName())) {
        break;
      }
      count++;
    }
    // sets the difficulty settings for user and writes to json file
    ArrayList<Difficulty> userDifficulty = new ArrayList<>();
    userDifficulty.add(difficultyList.get(difficultyNames.indexOf(accuracyChoiceBox.getValue())));
    userDifficulty.add(difficultyList.get(difficultyNames.indexOf(wordsChoiceBox.getValue())));
    userDifficulty.add(difficultyList.get(difficultyNames.indexOf(timeChoiceBox.getValue())));
    userDifficulty.add(difficultyList.get(difficultyNames.indexOf(confidenceChoiceBox.getValue())));
    userList.get(count).setDifficulty(userDifficulty);
    mapper.writeValue(new File(".profiles/users.json"), userList);
    user = userList.get(count);
  }
}
