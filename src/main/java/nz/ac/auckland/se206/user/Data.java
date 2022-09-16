package nz.ac.auckland.se206.user;

import java.io.Serializable;
import nz.ac.auckland.se206.words.CategorySelector.Difficulty;

public class Data implements Serializable {
  // private ArrayList<String> array = new ArrayList<String>();
  // private ArrayList<String> resultA = new ArrayList<>();
  // private ArrayList<Difficulty> difficultyA = new ArrayList<>();
  // private ArrayList<Integer> timeA = new ArrayList<>();
  // private ArrayList<String> wordA = new ArrayList<>();

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

  //   /**
  //    * @return String return the result
  //    */
  //   public ArrayList<String> getResult() {
  //     return resultA;
  //   }

  //   /**
  //    * @param result the result to set
  //    */
  //   public void addResult(String result) {
  //     resultA.add(result);
  //   }

  //   /**
  //    * @return Difficulty return the difficulty
  //    */
  //   public ArrayList<Difficulty> getDifficulty() {
  //     return difficultyA;
  //   }

  //   /**
  //    * @param difficulty the difficulty to set
  //    */
  //   public void addDifficulty(Difficulty difficulty) {
  //     difficultyA.add(difficulty);
  //   }

  //   /**
  //    * @return long return the time
  //    */
  //   public ArrayList<Integer> getTime() {
  //     return timeA;
  //   }

  //   /**
  //    * @param time the time to set
  //    */
  //   public void addTime(int time) {
  //     timeA.add(time);
  //   }

  //   /**
  //    * @return String return the word
  //    */
  //   public ArrayList<String> getWord() {
  //     return wordA;
  //   }

  //   /**
  //    * @param word the word to set
  //    */
  //   public void addWord(String word) {
  //     wordA.add(word);
  //   }
  //
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
