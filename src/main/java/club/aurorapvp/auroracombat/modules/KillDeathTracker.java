package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.config.Config;
import club.aurorapvp.auroracombat.config.Lang;
import club.aurorapvp.auroracombat.data.KillDeathDataHandler;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KillDeathTracker {
  private static final Map<Player, KillDeathTracker> TRACKERS = new HashMap<>();
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

    TRACKERS.put(player, this);
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

    if (killStreak >= Config.get().getInt("misc.min-killstreak-to-announce")) {
      Bukkit.broadcast(Lang.formatComponent("killstreak-lost", player, killStreak));
    }

    this.killStreak = 0;
  }

  public void addKill() {
    this.killStreak++;
    this.kills++;

    player.sendMessage(Lang.formatComponent("new-kill", kills, killStreak));
  }

  public static void saveAll() {
    for (KillDeathTracker tracker : TRACKERS.values()) {
      tracker.save();
    }
  }

  public static KillDeathTracker getTracker(Player player) {
    return TRACKERS.getOrDefault(player, new KillDeathTracker(player));
  }
}
