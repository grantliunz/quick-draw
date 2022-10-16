package nz.ac.auckland.se206.controllers;

import static nz.ac.auckland.se206.util.BadgeUtil.BADGE_DESCRIPTIONS;
import static nz.ac.auckland.se206.util.BadgeUtil.unlockBadges;

import java.util.ArrayList;
import java.util.Collections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import nz.ac.auckland.se206.scenes.SceneManager;
import nz.ac.auckland.se206.user.Data;
import nz.ac.auckland.se206.user.Data.Result;
import nz.ac.auckland.se206.user.User;

public class StatsController {

  public static ArrayList<Data> getGamesWonOrLoss(ArrayList<Data> allGames, Result result) {
    ArrayList<Data> wonGames = new ArrayList<>(allGames);
    wonGames.removeIf(data -> !data.getResult().equals(result));
    return wonGames;
  }

  @FXML private Label headerLabel;
  @FXML private Label winListLabel;
  @FXML private Label winTimeListLabel;
  @FXML private Label lossListLabel;

  @FXML private Label bestGameLabel;

  @FXML private Label gamesWonLabel;
  @FXML private Label gamesLostLabel;

  @FXML private GridPane badgeGrid;
  @FXML private Label scoreLabel;

  @FXML
  private void onReturn(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.MENU));
  }

  /**
   * updates the stats of the user chosen and updates labels
   *
   * @param user User object
   */
  public void updateStats(User user) {
    // Changes the header to show user name
    headerLabel.setText(user.getName() + "'s Stats");

    // Updates stored stats
    ArrayList<Data> gamesWon = getGamesWonOrLoss(user.getData(), Result.WIN);
    ArrayList<Data> gamesLost = getGamesWonOrLoss(user.getData(), Result.LOSS);

    // Refreshes the UI
    populateWinLists(gamesWon);
    populateLossList(gamesLost);

    // Show fasted game
    if (!gamesWon.isEmpty()) {
      Data bestGame = Collections.min(gamesWon, (a, b) -> (int) (a.getTime() - b.getTime()));
      bestGameLabel.setText("Best Game: " + bestGame.getWord() + " | " + bestGame.getTime() + "s");
    } else {
      bestGameLabel.setText("");
    }

    boolean[] badges = unlockBadges(user);
    hideBadges(badges);
    scoreLabel.setText("Score: " + user.getScore());
  }

  /**
   * Add all won game words of the user to the wins list
   *
   * @param gamesWon Arraylist of won games and their time
   */
  private void populateWinLists(ArrayList<Data> gamesWon) {
    // Creates a string of words won
    StringBuilder sbWords = new StringBuilder();
    StringBuilder sbTimes = new StringBuilder();
    gamesWonLabel.setText("Games Won: " + gamesWon.size());
    for (Data game : gamesWon) {
      sbWords.append(game.getWord()).append(" \n");
      sbTimes.append(" | ").append(game.getTime()).append("s\n");
    }
    // Updates the UI
    winListLabel.setText(sbWords.toString());
    winListLabel.setAlignment(Pos.TOP_RIGHT);
    winTimeListLabel.setText(sbTimes.toString());
  }

  /**
   * Add all lost game words to the lost games list
   *
   * @param gamesLost Arraylist of games words lost by the user
   */
  private void populateLossList(ArrayList<Data> gamesLost) {
    StringBuilder sbWords = new StringBuilder();
    gamesLostLabel.setText("Games Lost: " + gamesLost.size());
    for (Data game : gamesLost) {
      sbWords.append(game.getWord()).append(" \n");
    }
    lossListLabel.setText(sbWords.toString());
  }

  /**
   * Hide badge if a user has not met the requirements to unlock them and display that they are
   * locked. Displays the badges
   *
   * @param badges boolean array of whether they have unlcked the badges based on stats
   */
  private void hideBadges(boolean[] badges) {
    ObservableList<Node> children = badgeGrid.getChildren();
    for (int i = 0; i < children.size(); i++) {
      Node node = children.get(i);
      if (node instanceof ImageView imageView) {
        Image image;
        Tooltip t;
        if (!badges[i]) { // if the badge is locked then add a tool tip and hide the badge
          image = new Image("/images/badges/locked.png");
          t = new Tooltip("LOCKED:\n " + BADGE_DESCRIPTIONS[i]);
        } else { // otherwise display the badge
          image = new Image("/images/badges/badge" + i + ".png");
          t = new Tooltip(BADGE_DESCRIPTIONS[i]);
        }
        imageView.setImage(image);
        imageView.setPickOnBounds(true);
        t.setShowDelay(javafx.util.Duration.millis(0));
        t.setStyle("-fx-font-size: 20"); // set tooltip font size
        int finalI = i;
        t.setOnShowing(
            s -> {
              Bounds bounds = imageView.localToScreen(imageView.getBoundsInLocal());
              t.setX(bounds.getMinX());
              if (finalI < 4) {
                t.setY(bounds.getMaxY());
              } else {
                t.setY(bounds.getMinY() - t.getHeight());
              }
            });
        Tooltip.install(imageView, t);
      }
    }
  }
}
