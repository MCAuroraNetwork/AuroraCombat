package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.data.KillDeathDataHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KillDeathTracker {

  private static final Map<UUID, KillDeathTracker> TRACKERS = new HashMap<>();
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

    TRACKERS.put(player.getUniqueId(), this);
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
    this.deaths = deaths + 1;

    if (killStreak >= AuroraCombat.getInstance().getConfig()
        .getInt("misc.min-killstreak-to-announce")) {
      Bukkit.broadcast(AuroraCombat.getInstance().getLang()
          .formatComponent("killstreak-lost", player, killStreak));
    }

    this.killStreak = 0;
  }

  public void addKill() {
    this.killStreak = killStreak + 1;
    this.kills = kills + 1;
  }

  public static void saveAll() {
    for (KillDeathTracker tracker : TRACKERS.values()) {
      tracker.save();
    }

    AuroraCombat.getInstance().getLogger().log(Level.INFO, "All kdr trackers saved");
  }

  public static KillDeathTracker getTracker(Player player) {
    return TRACKERS.getOrDefault(player.getUniqueId(), new KillDeathTracker(player));
  }
}
