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
    new BukkitRunnable() {
      final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
      final Objective objective = scoreboard.registerNewObjective("below-name", Criteria.DUMMY,
          MiniMessage.miniMessage().deserialize("<red>❤"));

      @Override
      public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
          objective.getScore(player.getName()).setScore(
              (int) (player.getHealth() + player.getAbsorptionAmount()));
        }
      }
    }.runTaskTimer(AuroraCombat.INSTANCE, 0L, 1L);
  }
}
