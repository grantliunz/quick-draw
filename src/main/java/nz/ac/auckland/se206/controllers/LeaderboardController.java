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
import nz.ac.auckland.se206.scenes.SceneManager;
import nz.ac.auckland.se206.user.User;

public class LeaderboardController {

  @FXML private Button homeButton;
  @FXML private Label leaderboard;
  @FXML private Label leaderboardScore;

  /**
   * Updates the leaderboard each time it is viewed Reads from users accumlative score and displays
   * it
   *
   * @throws StreamReadException error when reading from a stream
   * @throws DatabindException this is databind exception
   * @throws IOException invalid input or output
   */
  public void updateLeaderboard() throws StreamReadException, DatabindException, IOException {
    List<User> userList = getUserList();
    List<User> sortedList = sortUserList(userList);
    populateLeaderboard(sortedList);
  }

  /**
   * Switches back to the home screen ui
   *
   * @param event Button press event
   */
  @FXML
  private void onSwitchToHome(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.MENU));
  }

  /**
   * this method just sets the labels and prints the leaderboard
   *
   * @param sortedList List of users
   */
  private void populateLeaderboard(List<User> sortedList) {
    StringBuilder sbUser = new StringBuilder();
    StringBuilder sbScore = new StringBuilder();
    // goes through the sorted array and sets the users and their scores
    for (User u : sortedList) {
      sbUser.append(u.getName()).append(" \n");
      sbScore.append(u.getScore()).append(" points\n");
    }
    // Updates the UI
    leaderboard.setText(sbUser.toString());
    leaderboardScore.setText(sbScore.toString());
  }

  /**
   * This method retrieves a user list
   *
   * @return A list of users
   * @throws StreamReadException error when reading from a stream
   * @throws DatabindException this is databind exception
   * @throws IOException invalid input or output
   */
  private List<User> getUserList() throws StreamReadException, DatabindException, IOException {
    ObjectMapper mapper = new ObjectMapper();

    // List of users read from json file
    List<User> userList =
        mapper.readValue(new File(".profiles/users.json"), new TypeReference<List<User>>() {});
    return userList;
  }

  /**
   * this is a method to sort the user list by scores
   *
   * @param userList the userlist read by the json
   * @return userList the list returned is a sorted list
   */
  private List<User> sortUserList(List<User> userList) {
    int len = userList.size();
    if (len < 2) {
      return userList;
    }
    int mid = len / 2;
    // create new subarray lists left and right of pivot
    List<User> l = new ArrayList<User>();
    List<User> r = new ArrayList<User>();
    // get left of middle element
    for (int i = 0; i < mid; i++) {
      l.add(userList.get(i));
    }
    // get right of middle element
    for (int i = mid; i < len; i++) {
      r.add(userList.get(i));
    }
    // recursively sort left and right arraylists
    sortUserList(l);
    sortUserList(r);
    userList = merge(l, r, userList);
    return userList;
  }

  /**
   * this method is a sub function of the mergesort algorithm
   *
   * @param l left part of the array
   * @param r right part of the array
   * @param userList list of users read by json
   * @return returns list of sorted users
   */
  private List<User> merge(List<User> l, List<User> r, List<User> userList) {
    // initializes the required indexes
    int lenL = l.size();
    int lenR = r.size();
    int i = 0;
    int j = 0;
    int k = 0;
    // runs through both arrays and sets the sorted list
    while (i < lenL && j < lenR) {
      if (l.get(i).getScore() >= r.get(j).getScore()) {
        userList.set(k, l.get(i));
        i++;
      } else {
        userList.set(k, r.get(j));
        j++;
      }
      k++;
    }
    // in case array is still populated set to the end of sorted array
    while (i < lenL) {
      userList.set(k, l.get(i));
      i++;
      k++;
    }
    while (j < lenR) {
      userList.set(k, r.get(j));
      j++;
      k++;
    }
    return userList;
  }
}
