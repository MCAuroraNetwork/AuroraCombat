package club.aurorapvp.events.listeners;

import club.aurorapvp.AuroraCombat;
import club.aurorapvp.events.custom.PlayerDamagedByPlayerEvent;
import club.aurorapvp.modules.BlockFallDamage;
import club.aurorapvp.modules.DamageType;
import java.util.LinkedList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDamage implements Listener {
  public static Player attacker;
  public static Player attacked;
  public static Object weapon;
  private static Player lastCrystalDamager;
  private static final LinkedList<Projectile> lastFiredProjectiles = new LinkedList<>();
  private static Player lastInteractedWithBlock;
  private static Block lastExplodedBlock;

  public static void setDamageInformation(Player damager, Player damaged, Object attackWeapon) {
    attacker = damager;
    attacked = damaged;
    weapon = attackWeapon;
  }

  @EventHandler
  public void onEntityDamage(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof EnderCrystal && event.getDamager() instanceof Player damager) {
      lastCrystalDamager = damager;
    }

    if (!(event.getEntity() instanceof Player damaged)) {
      return;
    }

    if (event.getDamager() instanceof Projectile projectile) {
      for (Projectile firedProjectile : lastFiredProjectiles) {
        if (projectile == firedProjectile) {
          setDamageInformation((Player) projectile.getShooter(), damaged,
              event.getDamager());

          Bukkit.getPluginManager().callEvent(new PlayerDamagedByPlayerEvent(DamageType.RANGED));
        }
      }
    }

    if (event.getDamager() instanceof EnderCrystal damager) {
      setDamageInformation(lastCrystalDamager, damaged, damager);

      Bukkit.getPluginManager()
          .callEvent(new PlayerDamagedByPlayerEvent(DamageType.EXPLOSION));
    }

    if (event.getDamager() instanceof Player damager) {
      setDamageInformation(damager, damaged, damager.getInventory().getItemInMainHand());

      Bukkit.getPluginManager()
          .callEvent(new PlayerDamagedByPlayerEvent(DamageType.MELEE));
    }
  }

  // TODO Potion damage
  @EventHandler
  public void onEntityDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player damaged)) {
      return;
    }

    if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION &&
        damaged.getLocation().distance(lastExplodedBlock.getLocation()) <= 10) {
      setDamageInformation(lastInteractedWithBlock, damaged, lastExplodedBlock);

      Bukkit.getPluginManager()
          .callEvent(new PlayerDamagedByPlayerEvent(DamageType.EXPLOSION));
    }
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    if (event.getClickedBlock() == null) {
      return;
    }

    if (event.getClickedBlock() instanceof RespawnAnchor respawnAnchor) {
      if ((respawnAnchor.getCharges() >= 0 &&
          event.getPlayer().getInventory().getItemInMainHand().getType() != Material.GLOWSTONE) ||
          respawnAnchor.getCharges() >= 4) {
        lastExplodedBlock = event.getClickedBlock();
        lastInteractedWithBlock = event.getPlayer();
      }
    }

    if (event.getClickedBlock() instanceof Bed &&
        event.getPlayer().getWorld().getEnvironment() != World.Environment.NORMAL) {
      lastExplodedBlock = event.getClickedBlock();
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
}
