package club.aurorapvp.auroracombat.events.listeners;

import club.aurorapvp.auroracombat.AuroraCombat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

// TODO this should be in a module class, listeners are for listening only
public class PhaseListener implements Listener {

  private static final Map<Player, Location> safeLocations = new ConcurrentHashMap<>();

  @EventHandler(priority = EventPriority.HIGH)
  public void onPhase(PlayerMoveEvent event) {
    Player player = event.getPlayer();

    if (player.isSwimming()) {
      return;
    }

    if (player.getAllowFlight()
        || event.getTo().getWorld().getUID() != event.getFrom().getWorld().getUID()
        || player.getVehicle() != null) {
      return;
    }

    if (AuroraCombat.getInstance().getConfig().getBoolean("misc.pearl-phase-allow-for-nether-roof")
        && event.getPlayer().getLocation().getWorld().getEnvironment() == World.Environment.NETHER
        && event.getTo().getY() >= 122
        && event.getTo().getY() <= 127) {
      return;
    }

    if (player.getLocation().getY() % 1 != 0 || event.getTo().getY() % 1 != 0) {
      return;
    }

    final BoundingBox box = getBoundingBox(event);

    if (isInSolidBlock(box, event.getTo().getWorld())) {
      event.setTo(safeLocations.get(player));
      return;
    }

    safeLocations.put(event.getPlayer(), event.getFrom().clone());
  }

  @NotNull
  private static BoundingBox getBoundingBox(PlayerMoveEvent event) {
    final double minX = Math.min(event.getFrom().getX(), event.getTo().getX()),
        minY = Math.min(event.getFrom().getY(), event.getTo().getY()),
        minZ = Math.min(event.getFrom().getZ(), event.getTo().getZ()),
        maxX = Math.max(event.getFrom().getX(), event.getTo().getX()),
        maxY = Math.max(event.getFrom().getY(), event.getTo().getY()) + 0.9,
        maxZ = Math.max(event.getFrom().getZ(), event.getTo().getZ());

    return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerTeleport(PlayerTeleportEvent event) {
    if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
      return;
    }

    if (AuroraCombat.getInstance().getConfig().getBoolean("misc.pearl-phase-allow-for-nether-roof")
        && event.getPlayer().getLocation().getWorld().getEnvironment() == World.Environment.NETHER
        && event.getTo().getY() >= 122
        && event.getTo().getY() <= 127) {
      return;
    }

    new BukkitRunnable() {
      @Override
      public void run() {
        if (!isInSolidBlock(event.getPlayer().getBoundingBox(), event.getPlayer().getWorld())) {
          safeLocations.put(event.getPlayer(), event.getPlayer().getLocation().clone());

          return;
        }

        event
            .getPlayer()
            .teleportAsync(findSafeLocation(event.getPlayer(), event.getTo()))
            .thenRun(() -> safeLocations.put(event.getPlayer(), event.getPlayer().getLocation()));
      }
    }.runTaskLater(AuroraCombat.getInstance(), 1L);
  }

  public static Location findSafeLocation(Player player, Location location) {
    for (Block block : getAdjacentBlocks(location)) {
      if (!block.isSolid()) {
        return block.getLocation().add(0.5, 0, 0.5);
      }
    }
    return safeLocations.get(player);
  }

  public static Set<Block> getAdjacentBlocks(Location location) {
    Set<Block> blocks = new HashSet<>();

    if (location == null || location.getWorld() == null) {
      return blocks;
    }

    Block targetBlock = location.getBlock();
    blocks.add(targetBlock);

    int[][] directions = {
      {1, 0, 0},
      {-1, 0, 0},
      {0, 0, 1},
      {0, 0, -1}
    };

    for (int[] direction : directions) {
      Block adjacent = targetBlock.getRelative(direction[0], direction[1], direction[2]);
      blocks.add(adjacent);
    }

    return blocks;
  }

  public static boolean isInSolidBlock(BoundingBox box, World world) {
    int x1 = (int) Math.floor(box.getMinX());
    int y1 = (int) Math.floor(box.getMinY());
    int z1 = (int) Math.floor(box.getMinZ());
    int x2 = (int) Math.ceil(box.getMaxX());
    int y2 = (int) Math.ceil(box.getMaxY());
    int z2 = (int) Math.ceil(box.getMaxZ());

    for (int x = x1; x <= x2; ++x) {
      for (int y = y1; y <= y2; ++y) {
        for (int z = z1; z <= z2; ++z) {
          Block block = new Location(world, x, y, z).getBlock();
          if (!block.isPassable() && block.isSolid()) {
            BoundingBox blockBox = block.getBoundingBox();
            if (blockBox.overlaps(box)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }
}
