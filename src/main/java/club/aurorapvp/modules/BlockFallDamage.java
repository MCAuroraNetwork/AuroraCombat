package club.aurorapvp.modules;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;

public class BlockFallDamage {
  private static final Set<Player> inVulnerable = new HashSet<>();

  public static boolean shouldTakeDamage(Player p) {
    return !inVulnerable.contains(p);
  }

  public static void setInVulnerable(Player p) {
    inVulnerable.add(p);
  }

  public static void setVulnerable(Player p) {
    inVulnerable.remove(p);
  }
}
