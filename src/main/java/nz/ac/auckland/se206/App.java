package nz.ac.auckland.se206;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nz.ac.auckland.se206.scenes.SceneManager;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {

  public static void main(final String[] args) {
    launch();
  }

  /**
   * Returns the node associated to the input file. The method expects that the file is located in
   * "src/main/resources/fxml".
   *
   * @param fxml The name of the FXML file (without extension).
   * @return The node of the input file.
   */
  public static FXMLLoader loadFxml(final String fxml) {
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   */
  @Override
  public void start(final Stage stage) throws Exception {
    // Loads all initial scenes
    SceneManager.addUi(SceneManager.AppUi.MENU, loadFxml("menu"));
    SceneManager.addUi(SceneManager.AppUi.PROFILE, loadFxml("profile"));
    SceneManager.addUi(SceneManager.AppUi.STATS, loadFxml("stats"));
    SceneManager.addUi(SceneManager.AppUi.SETTINGS, loadFxml("settings"));
    SceneManager.addUi(SceneManager.AppUi.LEADERBOARD, loadFxml("leaderboard"));

    // Creates a profiles directory
    File dir = new File(".profiles");
    if (!dir.exists()) {
      dir.mkdirs();
    }

    // Shows scene
    final Scene scene = new Scene(SceneManager.getUiRoot(SceneManager.AppUi.MENU));
    stage.setScene(scene);
    stage.setTitle("Speedy Sketchers");
    stage.show();
    stage.setOnCloseRequest(
        e -> {
          Platform.exit();
          System.exit(0);
        });
  }
}
