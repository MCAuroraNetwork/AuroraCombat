package club.aurorapvp.events.listeners;

import club.aurorapvp.events.custom.PlayerDamagedByPlayerEvent;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Damage implements Listener {
  public static Map<EnderCrystal, Player> lastKilledCrystal = new HashMap<>();
  public static Map<Player, EnderCrystal> lastDamagedByCrystal = new HashMap<>();
  public static Map<Player, Block> lastDamagedByBlock = new HashMap<>();
  public static Player lastInteractedWithBlock;
  public static Block lastExplodedBlock;
  public static Map<Player, Player> lastAttackedOtherPlayer = new HashMap<>();

  @EventHandler
  public void onEntityDamage(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof EnderCrystal && event.getDamager() instanceof Player) {
      lastKilledCrystal.clear();
      lastKilledCrystal.put((EnderCrystal) event.getEntity(), (Player) event.getDamager());
    } else if (event.getEntity() instanceof Player && event.getDamager() instanceof EnderCrystal) {
      if (lastKilledCrystal.get((EnderCrystal) event.getDamager()) != event.getEntity()) {
        lastDamagedByCrystal.clear();
        lastDamagedByCrystal.put((Player) event.getEntity(), (EnderCrystal) event.getDamager());
        Bukkit.getPluginManager()
            .callEvent(new PlayerDamagedByPlayerEvent((Player) event.getEntity(), event));
      }
    } else if (event.getDamager() instanceof Player) {
      lastAttackedOtherPlayer.clear();
      lastAttackedOtherPlayer.put((Player) event.getEntity(), (Player) event.getDamager());
      Bukkit.getPluginManager()
          .callEvent(new PlayerDamagedByPlayerEvent((Player) event.getEntity(), event));
    }
  }

  @EventHandler
  public void onEntityDamage(EntityDamageEvent event) {
    if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION &&
        event.getEntity() instanceof Player) {
      if (event.getEntity().getLocation().distance(lastExplodedBlock.getLocation()) <= 10) {
        lastDamagedByBlock.put((Player) event.getEntity(), lastExplodedBlock);
      }
    }
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    if (event.getClickedBlock() != null) {
      if (event.getClickedBlock().getBlockData() instanceof RespawnAnchor respawnAnchor) {
        if (((respawnAnchor.getCharges() >= 0 &&
            event.getPlayer().getInventory().getItemInMainHand().getType() != Material.GLOWSTONE) ||
            respawnAnchor.getCharges() >= 4)) {
          lastExplodedBlock = event.getClickedBlock();
          lastInteractedWithBlock = event.getPlayer();
        }
      } else if (event.getClickedBlock() instanceof Bed) {
        if (event.getPlayer().getWorld().getEnvironment() != World.Environment.NORMAL) {
          lastExplodedBlock = event.getClickedBlock();
          lastInteractedWithBlock = event.getPlayer();
        }
      }
    }
  }
}
