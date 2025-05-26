package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.data.KillDeathDataHandler;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KillDeathTracker {

  private static final Map<UUID, KillDeathTracker> TRACKERS = new HashMap<>();
  private final Player player;
  private int deaths;
  private int kills;
  private int killStreak;
  private int highestKillStreak;
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
      highestKillStreak = 0;
      this.save();
    }
  }

  public static void register(Player player) {
    TRACKERS.put(player.getUniqueId(), new KillDeathTracker(player));
  }

  public static void unregister(Player player) {
    TRACKERS.remove(player.getUniqueId());
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

  public int getHighestKillStreak() {
    return this.highestKillStreak;
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
    killStreak = data.getKillstreak();
    highestKillStreak = data.getHighestKillstreak();
  }

  public void save() {
    this.data.save();
  }

  public void addDeath() {
    this.deaths++;

    if (killStreak
        >= AuroraCombat.getInstance().getConfig().getInt("misc.min-killstreak-to-announce")) {
      Bukkit.broadcast(
          AuroraCombat.getInstance()
              .getLang()
              .formatComponent("killstreak-lost", player.getName(), killStreak));

      if (AuroraCombat.getInstance().isDiscordSRVInstalled()) {
        DiscordSRV.getPlugin()
            .getOptionalTextChannel("global")
            .sendMessageEmbeds(
                new EmbedBuilder()
                    .setAuthor(
                        AuroraCombat.getInstance()
                            .getLang()
                            .getString("discord-killstreak-lost")
                            .formatted(player.getName(), killStreak),
                        null,
                        "https://cravatar.eu/helmavatar/" + player.getUniqueId())
                    .setColor(Color.BLACK)
                    .build())
            .queue();
      }
    }

    this.killStreak = 0;

    this.save();
  }

  public void addKill() {
    this.killStreak++;
    this.kills++;

    if (killStreak > highestKillStreak) {
      highestKillStreak++;
    }

    if (killStreak
            % AuroraCombat.getInstance().getConfig().getInt("misc.min-killstreak-to-announce")
        == 0) {
      Bukkit.broadcast(
          AuroraCombat.getInstance()
              .getLang()
              .formatComponent("on-killstreak", player.getName(), killStreak));

      if (AuroraCombat.getInstance().isDiscordSRVInstalled()) {
        DiscordSRV.getPlugin()
            .getOptionalTextChannel("global")
            .sendMessageEmbeds(
                new EmbedBuilder()
                    .setAuthor(
                        AuroraCombat.getInstance()
                            .getLang()
                            .getString("discord-on-killstreak")
                            .formatted(player.getName(), killStreak),
                        null,
                        "https://cravatar.eu/helmavatar/" + player.getUniqueId())
                    .setColor(Color.BLACK)
                    .build())
            .queue();
      }
    }

    this.save();
  }

  public static KillDeathTracker getTracker(Player player) {
    return TRACKERS.get(player.getUniqueId());
  }
}
