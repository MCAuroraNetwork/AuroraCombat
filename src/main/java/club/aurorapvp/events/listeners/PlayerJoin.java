package club.aurorapvp.events.listeners;

import club.aurorapvp.modules.Rating;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Rating.setupPlayer(event.getPlayer());
  }
}
