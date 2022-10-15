package nz.ac.auckland.se206.user;

import java.io.Serializable;
import nz.ac.auckland.se206.controllers.CanvasController.GameMode;
import nz.ac.auckland.se206.words.CategorySelector.Difficulty;

public class Data implements Serializable {

  public enum Result {
    WIN,
    LOSS
  }

  private Result result;
  private Difficulty difficulty;
  private long time;
  private String word;

  private GameMode gameMode;

  Data() {}

  Data(String word, Result result, long time, Difficulty difficulty, GameMode gameMode) {
    super();
    this.word = word;
    this.result = result;
    this.time = time;
    this.difficulty = difficulty;
    this.gameMode = gameMode;
  }

  /**
   * @param result the result to set
   */
  public void setResult(Result result) {
    this.result = result;
  }

  public Result getResult() {
    return result;
  }

  /**
   * @param difficulty the difficulty to set
   */
  public void setDifficulty(Difficulty difficulty) {
    this.difficulty = difficulty;
  }

  public Difficulty getDifficulty() {
    return difficulty;
  }

  /**
   * @param time the time to set
   */
  public void setTime(long time) {
    this.time = time;
  }

  public long getTime() {
    return time;
  }

  /**
   * @param word the word to set
   */
  public void setWord(String word) {
    this.word = word;
  }

  public String getWord() {
    return word;
  }

  public void setgameMode(GameMode gameMode) {
    this.gameMode = gameMode;
  }

  public GameMode getgameMode() {
    return gameMode;
  }
}
