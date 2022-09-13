package nz.ac.auckland.se206.user;

import java.io.Serializable;

public class User implements Serializable {
  private String name;
  private int gamesWon = 0;
  private int gamesLost = 0;

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
}
