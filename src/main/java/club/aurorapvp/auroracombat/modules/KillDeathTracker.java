package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.config.Lang;
import club.aurorapvp.auroracombat.data.KillDeathDataHandler;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;

public class KillDeathTracker {
  private static final Set<KillDeathTracker> TRACKERS = new HashSet<>();
  private final Player player;
  private int deaths;
  private int kills;
  private int killStreak;
  private final KillDeathDataHandler data;

  public KillDeathTracker(Player player) {
    this.player = player;
    data = new KillDeathDataHandler(this);

    if (this.exists()) {
      this.reload();
    } else {
      kills = 0;
      deaths = 0;
      killStreak = 0;
      this.save();
    }

    TRACKERS.add(this);
  }

  public Player getPlayer() {
    return player;
  }

  public int getDeaths() {
    return this.deaths;
  }

  public int getKills() {
    return this.kills;
  }

  // TODO kill streak milestone and loss messages
  public int getKillStreak() {
    return this.killStreak;
  }

  public double getKDR() {
    if (deaths == 0) {
      return kills;
    } else {
      return (double) kills / deaths;
    }
  }

  public boolean exists() {
    return this.data.exists();
  }

  public void reload() {
    kills = data.getKills();
    deaths = data.getDeaths();
  }

  public void save() {
    this.data.save();
  }

  public void addDeath() {
    this.deaths++;

    player.sendMessage(Lang.formatComponent("new-death", deaths, killStreak));

    this.killStreak = 0;
  }

  public void addKill() {
    this.killStreak++;
    this.kills++;

    player.sendMessage(Lang.formatComponent("new-kill", kills, killStreak));
  }

  public static void saveAll() {
    for (KillDeathTracker tracker : TRACKERS) {
      tracker.save();
    }
  }

  public static KillDeathTracker getTracker(Player player) {
    for (KillDeathTracker tracker : TRACKERS) {
      if (tracker.getPlayer() == player) {
        return tracker;
      }
    }
    return null;
  }
}
