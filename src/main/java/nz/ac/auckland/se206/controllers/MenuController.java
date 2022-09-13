package nz.ac.auckland.se206.controllers;

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
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.user.User;

public class MenuController {
  @FXML private Button start;
  @FXML private HBox hboxx; // This box contains all the user profile buttons
  @FXML private Label header;
  @FXML private Button addProfile;
  @FXML private Button switchProfile;
  @FXML private Button stats;
  public static List<User> userList = new ArrayList<>();
  private User chosenUser;

  private TextToSpeech tts = new TextToSpeech();

  /**
   * This method is setup to display all the user profiles as buttons and assign actions to them
   * TODO: Add the json user files to this and finish implementation
   *
   * @throws IOException
   * @throws DatabindException
   * @throws StreamReadException
   */
  protected void view() throws StreamReadException, DatabindException, IOException {
    List<Button> b = new ArrayList<>(); // stores buttons
    ObjectMapper mapper = new ObjectMapper();
    File db = new File("src/main/resources/users.json");

    if (db.length() != 0) {
      List<User> userList2 = mapper.readValue(db, new TypeReference<List<User>>() {});
      for (User user : userList2) { // creates a button based on each user in userList
        Button button = new Button(user.getName());
        button.setOnAction( // sets what a button should do upon being pressed NOTE: may be best to
            // just
            // move to a helper func
            e -> {
              chosenUser = user;
              hboxx.setVisible(false);
              header.setText("Welcome" + " " + button.getText());
              start.setVisible(true);
              addProfile.setVisible(false);
              switchProfile.setVisible(true);
              stats.setVisible(true);
            });
        b.add(button);
      }
      hboxx.getChildren().clear();
      hboxx.getChildren().addAll(b);
    }
  }

  /**
   * Called when the gui is loaded
   *
   * @throws Exception
   */
  public void initialize() throws Exception {

    start.setVisible(false); // set start button invis
    switchProfile.setVisible(false);
    stats.setVisible(false);
    hboxx.setVisible(true);
    view(); // display current profiles
  }

  @FXML
  private void onSwitchToReady(ActionEvent event) {
    // Changes scene
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    CanvasController controller =
        (CanvasController) SceneManager.getUiController(SceneManager.AppUi.CANVAS);
    controller.setUser(chosenUser);
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

  /**
   * Upon clicking the + button to add a profile this method is called to switch to the profile
   * creation menu
   *
   * @param event
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
   * @param event
   */
  @FXML
  private void switchProfile(ActionEvent event) {
    header.setText("Who are you?");
    hboxx.setVisible(true);
    start.setVisible(false);
    addProfile.setVisible(true);
    switchProfile.setVisible(false);
    stats.setVisible(false);
  }

  @FXML
  private void displayStats(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.STATS));
  }
}
