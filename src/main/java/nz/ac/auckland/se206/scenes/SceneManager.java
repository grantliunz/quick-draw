package nz.ac.auckland.se206.scenes;

import java.io.IOException;
import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class SceneManager {

  public enum AppUi {
    MENU,
    CANVAS,
    PROFILE,
    STATS,
    SETTINGS,
    LEADERBOARD
  }

  private static HashMap<AppUi, ViewData> sceneMap = new HashMap<>();

  public static void addUi(AppUi appUi, FXMLLoader loader) throws IOException {
    sceneMap.put(appUi, new ViewData(loader.load(), loader.getController()));
  }

  public static Parent getUiRoot(AppUi appUi) {
    return sceneMap.get(appUi).getUiRoot();
  }

  public static Object getUiController(AppUi appUi) {
    return sceneMap.get(appUi).getController();
  }
}
