package club.aurorapvp.placeholders;

import club.aurorapvp.AuroraCombat;
import club.aurorapvp.modules.CombatTag;
import club.aurorapvp.modules.Rating;
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
    return AuroraCombat.INSTANCE.getDescription().getVersion();
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
    if (params.startsWith("opponent")) {
      if (p.isOnline()) {
        CombatTag recentTag = CombatTag.getRecentTag((Player) p);

        if (recentTag == null) {
          return "None";
        }

        return recentTag.getOpponent((Player) p).getName();
      }
    }

    if (params.startsWith("rating_")) {
      if (p.isOnline()) {
        return String.valueOf(Rating.getRating((Player) p, params.replace("rating_", "")).getPoints());
      }
    }
    return null;
  }
}
