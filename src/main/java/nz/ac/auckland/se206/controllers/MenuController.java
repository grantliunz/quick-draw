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
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import nz.ac.auckland.se206.scenes.SceneManager;
import nz.ac.auckland.se206.scenes.SceneManager.AppUi;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.user.User;

public class MenuController {

  public static List<User> userList = new ArrayList<>();

  @FXML private Button startButton;

  @FXML private HBox profilesHbox; // This box contains all the user profile buttons
  @FXML private Label headerLabel;
  @FXML private Button addProfileButton;
  @FXML private Button switchProfileButton;
  @FXML private Button displayStatsButton;
  @FXML private Button leaderboardButton;
  private User chosenUser;
  private TextToSpeech tts = new TextToSpeech();

  /**
   * This method is setup to display all the user profiles as buttons and assign actions to them
   * TODO: Add the json user files to this and finish implementation
   *
   * @throws IOException error thrown in case json has issues
   * @throws DatabindException error thrown in case json has issues
   * @throws StreamReadException error thrown in case json has issues
   */
  protected void view() throws StreamReadException, DatabindException, IOException {
    List<Button> b = new ArrayList<>(); // stores buttons
    ObjectMapper mapper = new ObjectMapper();
    File db = new File(".profiles/users.json");
    if (db.length() != 0) {
      List<User> userList2 = mapper.readValue(db, new TypeReference<List<User>>() {});
      for (User user : userList2) { // creates a button based on each user in userList
        Button button = new Button(user.getName());
        // sets what a button should do upon being pressed NOTE: may be best to just
        // move to a
        // helper function
        button.setOnAction(
            e -> {
              chosenUser = user;
              StatsController statsController =
                  (StatsController) SceneManager.getUiController(AppUi.STATS);
              statsController.updateStats(user);
              profilesHbox.setVisible(false);
              headerLabel.setText("Welcome" + " " + button.getText() + "!");
              startButton.setVisible(true);

              addProfileButton.setVisible(false);
              switchProfileButton.setVisible(true);
              displayStatsButton.setVisible(true);
              leaderboardButton.setVisible(true);
            });
        button.getStyleClass().add("select-profile-button");
        b.add(button);
      }
      profilesHbox.getChildren().clear();
      profilesHbox.getChildren().addAll(b);
    }
  }

  protected void updateUser(User user) {
    chosenUser = user;
  }

  /** Called when the gui is loaded */
  public void initialize() throws Exception {

    startButton.setVisible(false); // set start button invis
    // zenButton.setVisible(false); // set start button invis

    switchProfileButton.setVisible(false);
    displayStatsButton.setVisible(false);
    profilesHbox.setVisible(true);
    view(); // display current profiles
  }

  /**
   * Upon pressing the start button this takes you to the settings page where you choose your
   * gamemode
   *
   * @param event button press event
   * @throws Exception this is an exception
   */
  @FXML
  private void onStart(ActionEvent event) throws Exception {
    startGame(event);
    javafx.concurrent.Task<Void> task =
        new javafx.concurrent.Task<Void>() {
          @Override
          protected Void call() {
            // Uses Text to speech to speak given lines
            tts.speak("choose your settings");
            return null;
          }
        };
    // Delegates speaking task to new thread to prevent blocking of GUI
    Thread thread = new Thread(task);
    thread.start();
  }

  /**
   * Upon starting go to the settings page and take in preffered settings
   *
   * @param event button is pressed then action occurs
   * @return Settings controller
   * @throws Exception Some profound exception case is thrown out when the controller is invalid
   */
  private SettingsController startGame(ActionEvent event) throws Exception {
    // Changes scene
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();

    // Make a new canvas
    SceneManager.addUi(SceneManager.AppUi.CANVAS, loadFxml("settings"));
    SettingsController controller =
        (SettingsController) SceneManager.getUiController(SceneManager.AppUi.SETTINGS);
    controller.setUser(chosenUser);
    controller.savedSettings();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.SETTINGS));
    return controller;
  }

  /**
   * Upon clicking the + button to add a profile this method is called to switch to the profile
   * creation menu
   *
   * @param event button press action
   */
  @FXML
  private void onAdd(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.PROFILE));
  }

  /**
   * This method is called when the user wants to switch profiles which takes them back to the
   * initial profile select menu
   *
   * @param event event of the button being pressed
   */
  @FXML
  private void onSwitchProfile(ActionEvent event) {
    headerLabel.setText("Who's Playing?");
    // Update buttons
    profilesHbox.setVisible(true);
    startButton.setVisible(false);

    addProfileButton.setVisible(true);
    switchProfileButton.setVisible(false);
    displayStatsButton.setVisible(false);
  }

  /**
   * This method is called when the user wants to view their stats
   *
   * @param event the button click event
   */
  @FXML
  private void onDisplayStats(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.STATS));
  }

  /**
   * Upon pressing the leaderboard button, the leaderboard ui is opened
   *
   * @param event button press action
   * @throws IOException invalid output stream printed
   */
  @FXML
  private void onSwitchToLeaderboard(ActionEvent event) throws IOException {
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    SceneManager.addUi(SceneManager.AppUi.LEADERBOARD, loadFxml("leaderboard"));
    LeaderboardController controller =
        (LeaderboardController) SceneManager.getUiController(SceneManager.AppUi.LEADERBOARD);
    controller.updateLeaderboard();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.LEADERBOARD));
  }
}
