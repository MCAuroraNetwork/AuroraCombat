package club.aurorapvp.events.listeners;

import club.aurorapvp.AuroraCombat;
import club.aurorapvp.configs.Config;
import club.aurorapvp.enums.DamageType;
import club.aurorapvp.events.custom.PlayerDamagedByPlayerEvent;
import club.aurorapvp.modules.BlockFallDamage;
import java.util.LinkedList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDamageEvents implements Listener {
  private static Player lastCrystalDamager;
  private static final LinkedList<Projectile> lastFiredProjectiles = new LinkedList<>();
  private static Player lastInteractedWithBlock;
  private static BlockState lastExplodedBlock;

  @EventHandler
  public void onEntityDamage(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof EnderCrystal &&
        event.getDamager() instanceof Player damager) {
      lastCrystalDamager = damager;
    }

    if (!(event.getEntity() instanceof Player damaged)) {
      return;
    }

    if (event.getDamager() instanceof Projectile projectile) {
      for (Projectile firedProjectile : lastFiredProjectiles) {
        if (projectile == firedProjectile) {
          Bukkit.getPluginManager().callEvent(
              new PlayerDamagedByPlayerEvent(DamageType.RANGED, damaged,
                  (Player) projectile.getShooter(),
                  event.getDamager()));
        }
      }
    }

    if (event.getDamager() instanceof EnderCrystal damager) {
      Bukkit.getPluginManager()
          .callEvent(new PlayerDamagedByPlayerEvent(DamageType.EXPLOSION_ENTITY, damaged,
              lastCrystalDamager, damager));
    }

    if (event.getDamager() instanceof Player damager) {
      Bukkit.getPluginManager()
          .callEvent(new PlayerDamagedByPlayerEvent(DamageType.MELEE, damaged, damager,
              damager.getInventory().getItemInMainHand()));
    }
  }

  // TODO Potion damage
  @EventHandler
  public void onEntityDamage(EntityDamageByBlockEvent event) {
    if (!(event.getEntity() instanceof Player damaged)) {
      return;
    }

    if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION &&
        damaged.getLocation().distance(lastExplodedBlock.getLocation()) <= 10) {

      Bukkit.getPluginManager()
          .callEvent(new PlayerDamagedByPlayerEvent(DamageType.EXPLOSION_BLOCK, damaged,
              lastInteractedWithBlock, lastExplodedBlock));
    }
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.getClickedBlock() == null) {
      return;
    }

    if (event.getClickedBlock().getBlockData() instanceof RespawnAnchor respawnAnchor) {
      if ((respawnAnchor.getCharges() > 0 &&
          event.getPlayer().getInventory().getItemInMainHand().getType() != Material.GLOWSTONE) ||
          respawnAnchor.getCharges() >= 4) {
        lastExplodedBlock = event.getClickedBlock().getState();
        lastInteractedWithBlock = event.getPlayer();
      }
    }

    if (event.getClickedBlock().getBlockData() instanceof Bed &&
        event.getPlayer().getWorld().getEnvironment() != World.Environment.NORMAL) {
      lastExplodedBlock = event.getClickedBlock().getState();
      lastInteractedWithBlock = event.getPlayer();
    }
  }

  @EventHandler
  public void onProjectileFired(ProjectileLaunchEvent event) {
    if (event.getEntity().getShooter() instanceof Player p) {
      ItemStack weapon = p.getInventory().getItemInMainHand();
      if (lastFiredProjectiles.size() >= 3 || !weapon.containsEnchantment(Enchantment.MULTISHOT)) {
        lastFiredProjectiles.clear();
      }

      lastFiredProjectiles.add(event.getEntity());
    }
  }

  @EventHandler
  public void onFallDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player p)) {
      return;
    }

    if (!(event.getCause() == EntityDamageEvent.DamageCause.FALL)) {
      return;
    }

    if (Config.get().getBoolean("misc.fall-damage.enable-first")) {
      return;
    }

    if (!BlockFallDamage.shouldTakeDamage(p)) {
      event.setCancelled(true);
      BlockFallDamage.setVulnerable(p);
    }
  }
}
