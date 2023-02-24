package club.aurorapvp.events.listeners;

import club.aurorapvp.configs.Config;
import club.aurorapvp.modules.BlockFallDamage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class FallDamage implements Listener {
  @EventHandler
  public void onFallDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player p)) {
      return;
    }

    if (!(event.getCause() == EntityDamageEvent.DamageCause.FALL)) {
      return;
    }

    if (Config.get().getBoolean("misc.fall-damage.enable-first")) {
      return;
    }

    if (!BlockFallDamage.shouldTakeDamage(p)) {
      event.setCancelled(true);
      BlockFallDamage.setVulnerable(p);
    }
  }
}
