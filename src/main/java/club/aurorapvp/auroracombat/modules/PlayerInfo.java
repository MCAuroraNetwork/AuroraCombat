package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerInfo {

  public static void init() {
    final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    Objective objective = scoreboard.getObjective("below-name-health");

    if (objective == null) {
      objective = scoreboard.registerNewObjective("below-name-health", Criteria.DUMMY,
          MiniMessage.miniMessage().deserialize("<red>❤"));
    }

    final Objective finalObjective = objective;
    new BukkitRunnable() {
      @Override
      public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
          finalObjective.getScore(player.getName()).setScore(
              (int) (player.getHealth() + player.getAbsorptionAmount()));
        }
      }
    }.runTaskTimer(AuroraCombat.INSTANCE, 0L, 1L);
  }
}
