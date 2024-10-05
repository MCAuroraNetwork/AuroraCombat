package club.aurorapvp.auroracombat.modules;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

public class AntiPearlPhase {
  public static boolean isLocationPermitted(Player player, Location to) {
    BoundingBox box = player.getBoundingBox().clone();

    box.shift(new Location(player.getWorld(), 0, 0, 0).subtract(box.getCenterX(), box.getCenterY(), box.getCenterZ()));

    box.shift(to);

    Set<Block> blocks = getAdjacentBlocks(to);

    blocks.addAll(getAdjacentBlocks(to.add(0, 1, 0)));

    for (Block block : blocks) {
      if (box.overlaps(block.getBoundingBox())) {
        return false;
      }
    }

    return true;
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
      {0, 1, 0},
      {0, -1, 0},
      {0, 0, 1},
      {0, 0, -1}
    };

    for (int[] direction : directions) {
      Block adjacent = targetBlock.getRelative(direction[0], direction[1], direction[2]);
      blocks.add(adjacent);
    }

    return blocks;
  }
}
