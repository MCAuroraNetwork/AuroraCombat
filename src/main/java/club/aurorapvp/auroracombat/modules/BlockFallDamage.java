package club.aurorapvp.auroracombat.modules;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;

public class BlockFallDamage {
  private static final Set<Player> inVulnerable = new HashSet<>();

  public static boolean shouldTakeDamage(Player player) {
    return !inVulnerable.contains(player);
  }

  public static void setInVulnerable(Player player) {
    inVulnerable.add(player);
  }

  public static void setVulnerable(Player player) {
    inVulnerable.remove(player);
  }
}
