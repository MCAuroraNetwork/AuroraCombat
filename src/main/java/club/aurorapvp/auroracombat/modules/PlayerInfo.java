package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.config.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerInfo {

  public static void init() {
    new BukkitRunnable() {
      @Override
      public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
          Scoreboard scoreboard = p.getScoreboard();
          Objective objective = scoreboard.getObjective(DisplaySlot.BELOW_NAME);

          if (objective == null) {
            objective =
                scoreboard.registerNewObjective(
                    "below-name",
                    Criteria.DUMMY,
                    Lang.formatComponent("player-health-and-ping", p.getPing()));
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
          } else {
            objective.displayName(Lang.formatComponent("player-health-and-ping", p.getPing()));
          }

          String playerName = p.getName();
          objective.getScore(playerName).setScore((int) p.getHealth());
          p.setScoreboard(scoreboard);
        }
      }
    }.runTaskTimer(AuroraCombat.INSTANCE, 0L, 20L).getTaskId();
  }
}
