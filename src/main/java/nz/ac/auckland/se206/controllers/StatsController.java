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
  }

  private void populateWinLists(ArrayList<Data> gamesWon) {
    StringBuilder sbWord = new StringBuilder();
    StringBuilder sbTime = new StringBuilder();

    for (Data game : gamesWon) {
      sbWord.append(game.getWord() + " \n");
      sbTime.append("| " + game.getTime() + "\n");
    }
    winListLabel.setText(sbWord.toString());
    winTimeListLabel.setText(sbTime.toString());
  }

  private ArrayList<Data> getGamesWonOrLoss(ArrayList<Data> allGames, Result result) {
    ArrayList<Data> wonGames = new ArrayList<>(allGames);
    wonGames.removeIf(data -> !data.getResult().equals(result));
    return wonGames;
  }
}
