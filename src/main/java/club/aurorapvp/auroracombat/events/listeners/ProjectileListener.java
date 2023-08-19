package club.aurorapvp.auroracombat.events.listeners;

import club.aurorapvp.auroracombat.config.Config;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;

public class ProjectileListener implements Listener {
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

    World world = event.getEntity().getWorld();
    Location position = event.getEntity().getLocation();
    Vector velocity = event.getEntity().getVelocity();
    double maxDistance = Config.get().getInt("misc.ender-pearl-cooldown.max-distance");

    for (int i = 0; i < 20 * maxDistance / 10; i++) {
      position.add(velocity);
      velocity.setY(velocity.getY() - 0.08);

      if (world.getBlockAt(position).getType() != Material.AIR) {
        break;
      }

      if (position.distance(event.getEntity().getLocation()) >= maxDistance) {
        player.setCooldown(Material.ENDER_PEARL, Config.get().getInt("misc.ender-pearl-cooldown.time") * 20);
      }
    }

    if (position.distance(event.getEntity().getLocation()) >= maxDistance) {
      player.setCooldown(Material.ENDER_PEARL, Config.get().getInt("misc.ender-pearl-cooldown.time") * 20);
    }
  }
}
