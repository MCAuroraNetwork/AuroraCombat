package club.aurorapvp.events.listeners;

import club.aurorapvp.events.custom.PlayerDamagedByPlayerEvent;
import club.aurorapvp.events.custom.PlayerKilledByPlayerEvent;
import club.aurorapvp.modules.BlockFallDamage;
import club.aurorapvp.modules.CombatTag;
import club.aurorapvp.modules.Rating;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatTagEvents implements Listener {
  private PlayerDamagedByPlayerEvent lastDamage;

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    if (CombatTag.isTagged(event.getPlayer())) {
      event.getPlayer().setHealth(0);
      CombatTag.removeTags(event.getPlayer());
    }
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    CombatTag.removeTags(event.getPlayer());
    BlockFallDamage.setInVulnerable(event.getPlayer());

    CombatTag.removeTags(event.getPlayer());
    BlockFallDamage.setInVulnerable(event.getPlayer());

    if (lastDamage == null) {
      return;
    }

    if (event.getPlayer().equals(lastDamage.getDamaged())) {
      Bukkit.getPluginManager()
          .callEvent(new PlayerKilledByPlayerEvent(lastDamage, event));
      lastDamage = null;
    }
  }

  @EventHandler
  public void onKilledByPlayer(PlayerKilledByPlayerEvent event) {
    for (String type : Rating.getTypes()) {
      if (Rating.isUpdating(type)) {
        Rating.changeRating(event.getDamaged(), event.getDamager(), type);
      }
    }
  }

  @EventHandler
  public void onDamagedByPlayer(PlayerDamagedByPlayerEvent event) {
    if (!event.getDamager().equals(event.getDamaged())) {
      new CombatTag(event.getDamaged(), event.getDamager());
      lastDamage = event;
    }
  }
}