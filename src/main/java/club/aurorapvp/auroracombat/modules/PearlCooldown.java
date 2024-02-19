package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class PearlCooldown {

  private static final Map<UUID, Location> lastThrowLocation = new HashMap<>();
  private static final Map<UUID, Boolean> onCooldown = new HashMap<>();
  private static final Map<UUID, BukkitTask> tasks = new HashMap<>();

  public static void onPlayerTeleport(PlayerTeleportEvent event) {
    if (!event.getCause().equals(TeleportCause.ENDER_PEARL)) {
      return;
    }

    if (!event.getPlayer().getWorld().equals(event.getTo().getWorld())) {
      return;
    }

    if (!AuroraCombat.getInstance().getConfig().getBoolean("misc.ender-pearl-cooldown.enabled")) {
      return;
    }

    if (onCooldown.getOrDefault(event.getPlayer().getUniqueId(), false)) {
      if (AuroraCombat.getInstance().getConfig()
          .getBoolean("misc.ender-pearl-cooldown.only-active-when-tagged") && !CombatTag.isTagged(
          event.getPlayer())) {
        return;
      }

      if (lastThrowLocation.containsKey(event.getPlayer().getUniqueId())
          && lastThrowLocation.get(event.getPlayer().getUniqueId()).distance(event.getTo())
          < AuroraCombat.getInstance().getConfig()
          .getInt("misc.ender-pearl-cooldown.max-distance")) {
        return;
      }

      event.setCancelled(true);

      event.getPlayer()
          .sendMessage(AuroraCombat.getInstance().getLang().getComponent("no-running"));

      return;
    }

    if (lastThrowLocation.containsKey(event.getPlayer().getUniqueId())
        && lastThrowLocation.get(event.getPlayer().getUniqueId()).distance(event.getTo())
        >= AuroraCombat.getInstance().getConfig()
        .getInt("misc.ender-pearl-cooldown.max-distance")) {
      int ticks =
          AuroraCombat.getInstance().getConfig().getInt("misc.ender-pearl-cooldown.time") * 20;

      onCooldown.put(event.getPlayer().getUniqueId(), true);

      if (tasks.containsKey(event.getPlayer().getUniqueId())) {
        tasks.get(event.getPlayer().getUniqueId()).cancel();
      }

      tasks.put(event.getPlayer().getUniqueId(), new BukkitRunnable() {
        @Override
        public void run() {
          onCooldown.put(event.getPlayer().getUniqueId(), false);
        }
      }.runTaskLater(AuroraCombat.getInstance(), ticks));
    }
  }

  public static void onPearlThrow(ProjectileLaunchEvent event) {
    if (!(event.getEntity() instanceof EnderPearl)) {
      return;
    }

    if (!(event.getEntity().getShooter() instanceof Player player)) {
      return;
    }

    if (!AuroraCombat.getInstance().getConfig().getBoolean("misc.ender-pearl-cooldown.enabled")
        || (AuroraCombat.getInstance().getConfig()
        .getBoolean("misc.ender-pearl-cooldown.only-active-when-tagged") && !CombatTag.isTagged(
        player))) {
      return;
    }

    if (onCooldown.getOrDefault(player.getUniqueId(), false)) {
      event.setCancelled(true);
      return;
    }

    lastThrowLocation.put(player.getUniqueId(), player.getLocation());
  }
}
