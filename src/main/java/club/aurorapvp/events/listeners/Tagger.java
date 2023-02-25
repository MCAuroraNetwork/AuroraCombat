package club.aurorapvp.events.listeners;

import club.aurorapvp.events.custom.PlayerDamagedByPlayerEvent;
import club.aurorapvp.events.custom.PlayerKilledByPlayerEvent;
import club.aurorapvp.modules.CombatTag;
import club.aurorapvp.modules.Rating;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Tagger implements Listener {
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
  }

  @EventHandler
  public void onKilledByPlayer(PlayerKilledByPlayerEvent event) {
    Rating.changeRating(event.getDamaged(), event.getDamager());
  }

  @EventHandler
  public void onDamagedByPlayer(PlayerDamagedByPlayerEvent event) {
    new CombatTag(event.getDamaged(), event.getDamager());
  }
}
