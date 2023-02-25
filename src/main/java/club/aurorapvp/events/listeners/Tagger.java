package club.aurorapvp.events.listeners;

import club.aurorapvp.AuroraCombat;
import club.aurorapvp.events.custom.DuelEndEvent;
import club.aurorapvp.events.custom.PlayerDamagedByPlayerEvent;
import club.aurorapvp.events.custom.PlayerKilledByPlayerEvent;
import club.aurorapvp.modules.CombatTag;
import club.aurorapvp.modules.Duel;
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
    if (AuroraCombat.isAuroraDuelsInstalled()) {
      if (Duel.inDuel(event.getDamaged()) || Duel.inDuel(event.getDamager())) {
        return;
      }
    }
    Rating.changeRating(event.getDamaged(), event.getDamager(), "default");
  }

  @EventHandler
  public void onDuelEnd(DuelEndEvent event) {
    Rating.changeRating(event.getWinner(), event.getLoser(), "duels");
  }

  @EventHandler
  public void onDamagedByPlayer(PlayerDamagedByPlayerEvent event) {
    if (event.getDamager() != event.getDamaged()) {
      new CombatTag(event.getDamaged(), event.getDamager());
    }
  }
}
