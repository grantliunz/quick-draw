package nz.ac.auckland.se206.util;

import static nz.ac.auckland.se206.controllers.StatsController.getGamesWonOrLoss;

import java.util.ArrayList;
import nz.ac.auckland.se206.controllers.CanvasController.GameMode;
import nz.ac.auckland.se206.user.Data;
import nz.ac.auckland.se206.user.Data.Result;
import nz.ac.auckland.se206.user.User;

public class BadgeUtil {

  public static boolean[] unlockBadges(User user) {
    boolean[] badges = new boolean[8];
    ArrayList<Data> gameData = user.getData();
    ArrayList<Data> gamesWon = getGamesWonOrLoss(gameData, Result.WIN);

    for (Data data : gamesWon) {
      if (data.getTime() <= 10) {
        badges[0] = true;
      }
    }

    for (Data data : gamesWon) {
      if (data.getTime() <= 5) {
        badges[1] = true;
      }
    }

    if (gamesWon.size() >= 10) {
      badges[2] = true;
    }

    if (gamesWon.size() >= 50) {
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

    if (getGamemodeWins(gamesWon, GameMode.CLASSIC).size() >= 5
        && getGamemodeWins(gamesWon, GameMode.HIDDEN).size() >= 5) {
      badges[6] = true;
    }

    return badges;
  }

  public static ArrayList<Data> getGamemodeWins(ArrayList<Data> allWins, GameMode gameMode) {
    ArrayList<Data> wonGames = new ArrayList<>(allWins);
    wonGames.removeIf(data -> !data.getgameMode().equals(gameMode));
    return wonGames;
  }
}
