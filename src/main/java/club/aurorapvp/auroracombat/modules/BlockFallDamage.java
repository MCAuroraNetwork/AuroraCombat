package club.aurorapvp.auroracombat.modules;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.entity.Player;

public class BlockFallDamage {

  private static final HashMap<UUID, Boolean> invulnerable = new HashMap<>();

  public static boolean shouldTakeDamage(Player player) {
    return !invulnerable.get(player.getUniqueId());
  }

  public static void setInvulnerable(Player player) {
    invulnerable.put(player.getUniqueId(), true);
  }

  public static void setVulnerable(Player player) {
    invulnerable.put(player.getUniqueId(), false);
  }

  public static void unregister(Player player) {
    invulnerable.remove(player.getUniqueId());
  }
}
