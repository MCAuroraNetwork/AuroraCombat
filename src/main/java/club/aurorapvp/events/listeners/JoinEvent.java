package club.aurorapvp.events.listeners;

import club.aurorapvp.modules.BlockFallDamage;
import club.aurorapvp.modules.Rating;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class JoinEvent implements Listener {
  @EventHandler
  public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
    BlockFallDamage.setInVulnerable(event.getPlayer());
    Rating.setupPlayer(event.getPlayer());
  }
}
