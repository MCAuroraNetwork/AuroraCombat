package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.modules.CombatTag;
import club.aurorapvp.auroracombat.modules.Rating;
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

  private String getResult(OfflinePlayer p, String params) {
    if (!(p.isOnline())) {
      return "Player offline";
    }

    if (params.startsWith("opponent")) {
      CombatTag recentTag = CombatTag.getRecentTag((Player) p);

      if (recentTag == null) {
        return "None";
      }

      return recentTag.getOpponent((Player) p).getName();
    }

    if (params.startsWith("rating_")) {
      Rating rating = Rating.getRating((Player) p, params.replace("rating_", ""));

      if (rating == null) {
        return "Rating not found";
      } else {
        return String.valueOf(rating.getPoints());
      }

    }

    return null;
  }
}
