package club.aurorapvp.events.listeners;

import club.aurorapvp.events.custom.PlayerDamagedByPlayerEvent;
import club.aurorapvp.events.custom.PlayerKilledByPlayerEvent;
import club.aurorapvp.modules.DeathMessage;
import club.aurorapvp.modules.Rating;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatTag implements Listener {
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    if (club.aurorapvp.modules.CombatTag.isTagged(event.getPlayer())) {
      event.getPlayer().setHealth(0);
      club.aurorapvp.modules.CombatTag.removeTags(event.getPlayer());
    }
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Bukkit.getPluginManager().callEvent(
        new PlayerDamagedByPlayerEvent(event.getPlayer(), event.getPlayer().getLastDamageCause(),
            event));
    club.aurorapvp.modules.CombatTag.removeTags(event.getPlayer());
  }

  @EventHandler
  public void onKilledByPlayer(PlayerKilledByPlayerEvent event) {
    Rating.changeRating(event.getPlayer(), event.getDamager());
    new DeathMessage(event);
  }

  @EventHandler
  public void onDamagedByPlayer(PlayerDamagedByPlayerEvent event) {
    if (!event.damagedBySelf() && !event.getPlayer().isDead()) {
      new club.aurorapvp.modules.CombatTag(event.getPlayer(), event.getDamager());
    }
  }
}
