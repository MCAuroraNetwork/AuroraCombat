package club.aurorapvp.auroracombat.events.listeners;

import club.aurorapvp.auroracombat.modules.PearlCooldown;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ProjectileListener implements Listener {

  @EventHandler
      (priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerTeleport(PlayerTeleportEvent event) {
    PearlCooldown.onPlayerTeleport(event);
  }

  @EventHandler
      (priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPearlThrow(ProjectileLaunchEvent event) {
    PearlCooldown.onPearlThrow(event);
  }
}
