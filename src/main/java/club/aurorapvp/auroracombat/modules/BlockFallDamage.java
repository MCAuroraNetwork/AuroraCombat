package club.aurorapvp.auroracombat.modules;

import java.util.HashMap;
import org.bukkit.entity.Player;

public class BlockFallDamage {
  private static final HashMap<Player, Boolean> inVulnerable = new HashMap<>();

  public static boolean shouldTakeDamage(Player player) {
    return !inVulnerable.getOrDefault(player, true);
  }

  public static void setInVulnerable(Player player) {
    inVulnerable.put(player, true);
  }

  public static void setVulnerable(Player player) {
    inVulnerable.remove(player);
  }
}
