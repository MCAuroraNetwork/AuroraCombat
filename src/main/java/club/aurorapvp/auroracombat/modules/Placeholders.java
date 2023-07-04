package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {

  @Override
  public @NotNull String getIdentifier() {
    return "auroracombat";
  }

  @Override
  public @NotNull String getAuthor() {
    return "Villagers654";
  }

  @Override
  public @NotNull String getVersion() {
    return AuroraCombat.INSTANCE.getPluginMeta().getVersion();
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public String onRequest(OfflinePlayer p, @NotNull String params) {
    return getResult(p, params);
  }

  private String getResult(OfflinePlayer player, String params) {
    if (!(player instanceof Player p)) {
      return "Player offline";
    }

    if (params.startsWith("opponent")) {
      CombatTag recentTag = CombatTag.getRecentTag(p);

      if (recentTag == null) {
        return "None";
      }

      return recentTag.getOpponent(p).getName();
    }

    if (params.startsWith("rating_")) {
      Rating rating = Rating.getRating(p, params.replace("rating_", ""));

      if (rating == null) {
        return "Rating not found";
      } else {
        return String.valueOf(rating.getPoints());
      }

    }

    if (params.startsWith("kills")) {
      KillDeathTracker tracker = KillDeathTracker.getTracker(p);

      assert tracker != null;
      return String.valueOf(tracker.getKills());
    }

    if (params.startsWith("deaths")) {
      KillDeathTracker tracker = KillDeathTracker.getTracker(p);

      assert tracker != null;
      return String.valueOf(tracker.getDeaths());
    }

    if (params.startsWith("kdr")) {
      KillDeathTracker tracker = KillDeathTracker.getTracker(p);

      assert tracker != null;
      return String.valueOf(Math.round(tracker.getKDR() * 1000d) / 1000d);
    }

    if (params.startsWith("killstreak")) {
      KillDeathTracker tracker = KillDeathTracker.getTracker(p);

      assert tracker != null;
      return String.valueOf(tracker.getKillStreak());
    }

    return null;
  }
}
