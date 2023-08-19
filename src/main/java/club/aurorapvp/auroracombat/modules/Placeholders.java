package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
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
  public String onRequest(OfflinePlayer player, @NotNull String params) {
    return getResult(player, params);
  }

  private String getResult(OfflinePlayer offlinePlayer, String params) {
    if (!(offlinePlayer instanceof Player player)) {
      return "Player offline";
    }

    if (params.startsWith("opponent")) {
      CombatTag recentTag = CombatTag.getRecentTag(player);

      if (recentTag == null) {
        return "None";
      }

      return recentTag.getOpponent(player).getName();
    }

    if (params.startsWith("rating_")) {
      Rating rating = Rating.getRating(params.replace("rating_", ""));

      if (rating == null) {
        return "Rating not found";
      } else {
        return String.valueOf(rating.getScore(player).getPoints());
      }
    }

    if (params.startsWith("killstreak")) {
      KillDeathTracker tracker = KillDeathTracker.getTracker(player);

      assert tracker != null;
      return String.valueOf(tracker.getKillStreak());
    }

    if (params.startsWith("kills")) {
      KillDeathTracker tracker = KillDeathTracker.getTracker(player);

      assert tracker != null;
      return String.valueOf(tracker.getKills());
    }

    if (params.startsWith("deaths")) {
      KillDeathTracker tracker = KillDeathTracker.getTracker(player);

      assert tracker != null;
      return String.valueOf(tracker.getDeaths());
    }

    if (params.startsWith("kdr")) {
      KillDeathTracker tracker = KillDeathTracker.getTracker(player);

      assert tracker != null;
      return String.valueOf(Math.round(tracker.getKDR() * 1000d) / 1000d);
    }

    return null;
  }
}
