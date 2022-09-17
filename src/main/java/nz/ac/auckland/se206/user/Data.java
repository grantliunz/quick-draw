package nz.ac.auckland.se206.user;

import java.io.Serializable;
import nz.ac.auckland.se206.words.CategorySelector.Difficulty;

public class Data implements Serializable {
  private String result;
  private Difficulty difficulty;
  private long time;
  private String word;

  Data() {}

  Data(String word, String result, long time, Difficulty difficulty) {
    super();
    this.word = word;
    this.result = result;
    this.time = time;
    this.difficulty = difficulty;
  }

  /**
   * @param result the result to set
   */
  public void setResult(String result) {
    this.result = result;
  }

  public String getResult() {
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
}
