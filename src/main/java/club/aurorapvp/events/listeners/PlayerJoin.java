package club.aurorapvp.events.listeners;

import club.aurorapvp.AuroraCombat;
import club.aurorapvp.modules.BlockFallDamage;
import club.aurorapvp.modules.Rating;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    BlockFallDamage.setInVulnerable(event.getPlayer());

    if (AuroraCombat.isAuroraDuelsInstalled()) {
      Rating.setupRating("duels");
    }

    Rating.setupPlayer(event.getPlayer());
  }
}
