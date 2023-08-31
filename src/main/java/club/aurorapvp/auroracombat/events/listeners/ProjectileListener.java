package club.aurorapvp.auroracombat.events.listeners;

import club.aurorapvp.auroracombat.config.Config;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class ProjectileListener implements Listener {

  private final Map<Player, Location> lastThrowLocation = new HashMap<>();

  @EventHandler
  public void onPlayerTeleport(PlayerTeleportEvent event) {
    if (!event.getCause().equals(TeleportCause.ENDER_PEARL)) {
      return;
    }

    if (!Config.get().getBoolean("misc.ender-pearl-cooldown.enabled")) {
      return;
    }

    if (lastThrowLocation.get(event.getPlayer()).distance(event.getPlayer().getLocation())
        >= Config.get().getInt("misc.ender-pearl-cooldown.max-distance")) {
      event.getPlayer().setCooldown(Material.ENDER_PEARL,
          Config.get().getInt("misc.ender-pearl-cooldown.time") * 20);
    }
  }

  @EventHandler
  public void onPearlThrow(ProjectileLaunchEvent event) {
    if (!(event.getEntity() instanceof EnderPearl)) {
      return;
    }

    if (!(event.getEntity().getShooter() instanceof Player player)) {
      return;
    }

    if (!Config.get().getBoolean("misc.ender-pearl-cooldown.enabled")) {
      return;
    }

    lastThrowLocation.put(player, player.getLocation());
  }
}
