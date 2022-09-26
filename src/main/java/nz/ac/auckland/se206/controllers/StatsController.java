package nz.ac.auckland.se206.controllers;

import java.util.ArrayList;
import java.util.Collections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.scenes.SceneManager;
import nz.ac.auckland.se206.user.Data;
import nz.ac.auckland.se206.user.Data.Result;
import nz.ac.auckland.se206.user.User;

public class StatsController {

  @FXML private Label headerLabel;
  @FXML private Label winListLabel;
  @FXML private Label winTimeListLabel;
  @FXML private Label lossListLabel;

  @FXML private Label bestGameLabel;

  @FXML
  private void onReturn(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.MENU));
  }

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
    }
  }

  private void populateWinLists(ArrayList<Data> gamesWon) {
    // Creates a string of words won
    StringBuilder sbWords = new StringBuilder("\n");
    StringBuilder sbTimes = new StringBuilder(gamesWon.size() + "\n");
    for (Data game : gamesWon) {
      sbWords.append(game.getWord()).append(" \n");
      sbTimes.append(" | ").append(game.getTime()).append("s\n");
    }
    // Updates the UI
    winListLabel.setText(sbWords.toString());
    winListLabel.setAlignment(Pos.TOP_RIGHT);
    winTimeListLabel.setText(sbTimes.toString());
  }

  private void populateLossList(ArrayList<Data> gamesLost) {
    StringBuilder sbWords = new StringBuilder();
    sbWords.append(gamesLost.size()).append("\n");
    for (Data game : gamesLost) {
      sbWords.append(game.getWord()).append(" \n");
    }
    lossListLabel.setText(sbWords.toString());
  }

  private ArrayList<Data> getGamesWonOrLoss(ArrayList<Data> allGames, Result result) {
    ArrayList<Data> wonGames = new ArrayList<>(allGames);
    wonGames.removeIf(data -> !data.getResult().equals(result));
    return wonGames;
  }
}
