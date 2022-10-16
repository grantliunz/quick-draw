package nz.ac.auckland.se206.util;

import static nz.ac.auckland.se206.controllers.StatsController.getGamesWonOrLoss;

import java.util.ArrayList;
import nz.ac.auckland.se206.controllers.CanvasController.GameMode;
import nz.ac.auckland.se206.user.Data;
import nz.ac.auckland.se206.user.Data.Result;
import nz.ac.auckland.se206.user.User;

public class BadgeUtil {

  public static final String[] BADGE_DESCRIPTIONS =
      new String[] {
        "Win a game in 10 \n seconds or under",
        "Win a game in 5 \n seconds or under",
        "Win 10 games",
        "Win 50 games",
        "Win 5 games in a row",
        "Win a game with all \n the hardest settings",
        "Unlock Zen mode",
        "Win a game with every word"
      };

  /**
   * unlocks the badges depending on the user stats
   *
   * @param user Pass in a user object
   * @return a boolean array of what badges are unlocked
   */
  public static boolean[] unlockBadges(User user) {
    boolean[] badges = new boolean[8]; // List representing the badges
    ArrayList<Data> gameData = user.getData();
    ArrayList<Data> gamesWon = getGamesWonOrLoss(gameData, Result.WIN);

    for (Data data : gamesWon) {
      if (data.getTime() <= 10) { // Games won > 10 unlock badge
        badges[0] = true;
      }
    }

    for (Data data : gamesWon) {
      if (data.getTime() <= 5) { // Games won > 5 unlock badge
        badges[1] = true;
      }
    }

    if (gamesWon.size() >= 10) { // Games won > 10 unlock badge
      badges[2] = true;
    }

    if (gamesWon.size() >= 50) { // Games won > 50 unlock badge
      badges[3] = true;
    }

    int winStreak = 0;
    for (Data data : gamesWon) {
      if (data.getResult() == Result.WIN) {
        winStreak++;
      } else {
        winStreak = 0;
      }
      if (winStreak >= 5) {
        badges[4] = true;
      }
    }

    if (getGamemodeWins(gamesWon, GameMode.CLASSIC) >= 5
        && getGamemodeWins(gamesWon, GameMode.HIDDEN)
            >= 5) { // if 5 hidden games are won unlock badge
      badges[6] = true;
    }

    return badges;
  }

  /**
   * Gets the number of games won in a gamemode
   *
   * @param allWins ArrayList of all games won
   * @param gameMode Gamemode selected
   * @return a number of wins for a certain gamemode
   */
  public static int getGamemodeWins(ArrayList<Data> allWins, GameMode gameMode) {
    ArrayList<Data> wonGames = new ArrayList<>(allWins);
    wonGames.removeIf(data -> !data.getgameMode().equals(gameMode));
    return wonGames.size();
  }
}
