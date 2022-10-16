package nz.ac.auckland.se206.user;

import java.io.Serializable;
import java.util.ArrayList;
import nz.ac.auckland.se206.controllers.CanvasController.GameMode;
import nz.ac.auckland.se206.user.Data.Result;
import nz.ac.auckland.se206.words.CategorySelector.Difficulty;

public class User implements Serializable {

  private String name;
  private int gamesWon = 0;
  private int gamesLost = 0;
  private int score = 0;
  private ArrayList<Data> stats = new ArrayList<>();
  private ArrayList<Difficulty> difficulty = new ArrayList<>();

  public User(String name) {
    this.name = name;
  }

  public User() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getScore() {
    return this.score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getGamesWon() {
    return gamesWon;
  }

  public void setGamesWon(int gamesWon) {
    this.gamesWon = gamesWon;
  }

  public int getGamesLost() {
    return gamesLost;
  }

  public void setGamesLost(int gamesLost) {
    this.gamesLost = gamesLost;
  }

  public void addData(
      String word, Result result, long time, Difficulty difficulty, GameMode gameMode) {
    Data data = new Data(word, result, time, difficulty, gameMode);
    stats.add(data);
  }

  public ArrayList<Data> getData() {
    return stats;
  }

  public void setDifficulty(ArrayList<Difficulty> difficulty) {
    this.difficulty = difficulty;
  }

  public ArrayList<Difficulty> getDifficulty() {
    return difficulty;
  }
}
