package nz.ac.auckland.se206.controllers;

import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.scenes.SceneManager;
import nz.ac.auckland.se206.user.Data;
import nz.ac.auckland.se206.user.Data.Result;
import nz.ac.auckland.se206.user.User;

public class StatsController {

  private User user;

  @FXML private Label headerLabel;
  @FXML private Label winListLabel;
  @FXML private Label winTimeListLabel;
  @FXML private Label lossListLabel;

  @FXML
  private void onReturn(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.MENU));
  }

  public void updateStats(User user) {
    this.user = user;
    headerLabel.setText(user.getName() + "'s Stats");

    ArrayList<Data> gamesWon = getGamesWonOrLoss(user.getData(), Result.WIN);
    ArrayList<Data> gamesLost = getGamesWonOrLoss(user.getData(), Result.LOSS);

    populateWinLists(gamesWon);
    populateLossList(gamesLost);
  }

  private void populateWinLists(ArrayList<Data> gamesWon) {
    StringBuilder sbWords = new StringBuilder("\n");
    StringBuilder sbTimes = new StringBuilder(gamesWon.size() + "\n");
    for (Data game : gamesWon) {
      sbWords.append(game.getWord() + " \n");
      sbTimes.append("| " + game.getTime() + "s\n");
    }
    winListLabel.setText(sbWords.toString());
    winTimeListLabel.setText(sbTimes.toString());
  }

  private void populateLossList(ArrayList<Data> gamesLost) {
    StringBuilder sbWords = new StringBuilder();
    sbWords.append(gamesLost.size() + "\n");
    for (Data game : gamesLost) {
      sbWords.append(game.getWord() + " \n");
    }
    lossListLabel.setText(sbWords.toString());
  }

  private ArrayList<Data> getGamesWonOrLoss(ArrayList<Data> allGames, Result result) {
    ArrayList<Data> wonGames = new ArrayList<>(allGames);
    wonGames.removeIf(data -> !data.getResult().equals(result));
    return wonGames;
  }
}
