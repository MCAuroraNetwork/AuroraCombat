package club.aurorapvp.auroracombat.events.listeners;

import club.aurorapvp.auroracombat.AuroraCombat;
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
import org.bukkit.scheduler.BukkitRunnable;

public class ProjectileListener implements Listener {

  private final Map<Player, Location> lastThrowLocation = new HashMap<>();
  private final Map<Player, Boolean> onCooldown = new HashMap<>();

  @EventHandler
  public void onPlayerTeleport(PlayerTeleportEvent event) {
    if (!event.getCause().equals(TeleportCause.ENDER_PEARL)) {
      return;
    }

    if (!AuroraCombat.getInstance().getConfig().getBoolean("misc.ender-pearl-cooldown.enabled")) {
      return;
    }

    if (onCooldown.getOrDefault(event.getPlayer(), false)) {
      event.setCancelled(true);
      return;
    }

    if (lastThrowLocation.get(event.getPlayer()).distance(event.getTo())
        >= AuroraCombat.getInstance().getConfig().getInt("misc.ender-pearl-cooldown.max-distance")) {
      int ticks = AuroraCombat.getInstance().getConfig().getInt("misc.ender-pearl-cooldown.time") * 20;

      event.getPlayer().setCooldown(Material.ENDER_PEARL, ticks);

      onCooldown.put(event.getPlayer(), true);

      new BukkitRunnable() {
        @Override
        public void run() {
          onCooldown.put(event.getPlayer(), false);
        }
      }.runTaskLater(AuroraCombat.getInstance(), ticks);
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

    if (!AuroraCombat.getInstance().getConfig().getBoolean("misc.ender-pearl-cooldown.enabled")) {
      return;
    }

    if (onCooldown.getOrDefault(player, false)) {
      event.setCancelled(true);
      return;
    }

    lastThrowLocation.put(player, player.getLocation());
  }
}
