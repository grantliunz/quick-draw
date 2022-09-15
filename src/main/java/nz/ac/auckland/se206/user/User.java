package nz.ac.auckland.se206.user;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
  private String name;
  private int gamesWon = 0;
  private int gamesLost = 0;
  private ArrayList<String> words = new ArrayList<>();

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

  public void addWord(String word) {
    words.add(word);
  }

  public ArrayList<String> getWords() {
    return words;
  }
}
