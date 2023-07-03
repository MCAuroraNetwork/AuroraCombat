package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.config.Lang;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class PlayerInfo {
  private static final Set<PlayerInfo> PLAYER_INFOS = new HashSet<>();
  private final Player p;
  private final int taskId;

  public PlayerInfo(Player p) {
    this.p = p;
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    Scoreboard board = manager.getNewScoreboard();
    Team team = board.registerNewTeam("playerInfo");
    team.addPlayer(p);

    taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(AuroraCombat.INSTANCE, () -> team.suffix(
        Lang.formatComponent("player-health-and-ping", p.getHealth(), p.getPing())), 0L, 20L);

    PLAYER_INFOS.add(this);
  }

  public Player getPlayer() {
    return p;
  }

  public void cancelTask() {
    Bukkit.getScheduler().cancelTask(taskId);
  }

  public static void removePlayer(Player p) {
    for (PlayerInfo info : PLAYER_INFOS) {
      if (info.getPlayer() == p) {
        info.cancelTask();
        PLAYER_INFOS.remove(info);
        return;
      }
    }
  }
}
