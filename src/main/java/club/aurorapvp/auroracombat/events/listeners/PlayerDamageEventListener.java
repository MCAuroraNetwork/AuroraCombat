package club.aurorapvp.auroracombat.events.listeners;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.config.Config;
import club.aurorapvp.auroracombat.enums.DamageType;
import club.aurorapvp.auroracombat.events.custom.PlayerDamagedByPlayerEvent;
import club.aurorapvp.auroracombat.modules.BlockFallDamage;
import club.aurorapvp.auroracombat.util.ItemStackUtil;
import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class PlayerDamageEventListener implements Listener {
  private final HashSet<Projectile> firedProjectiles = new HashSet<>();
  private Player lastCrystalAttacker;
  private Player lastInteractedWithBlock;
  private BlockState lastExplodedBlock;

  @EventHandler
  public void onEntityDamage(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof EnderCrystal &&
        event.getDamager() instanceof Player attacker) {
      this.lastCrystalAttacker = attacker;
    }

    if (!(event.getEntity() instanceof Player damaged)) {
      return;
    }

    if (event.getDamager() instanceof
        EnderCrystal enderCrystal) {
      Bukkit.getPluginManager().callEvent(
          new PlayerOnPlayerDamageBuilder()
              .damageType(DamageType.EXPLOSION_ENTITY)
              .damaged(damaged)
              .attacker(this.lastCrystalAttacker)
              .weapon(ItemStackUtil.toItemStack(enderCrystal))
              .build());
    }

    if (event.getDamager() instanceof
        Player attacker) {
      Bukkit.getPluginManager().callEvent(
          new PlayerOnPlayerDamageBuilder()
              .damageType(DamageType.MELEE)
              .damaged(damaged)
              .attacker(attacker)
              .weapon(attacker.getInventory().getItemInMainHand())
              .build());
    }

    if (!(event.getDamager() instanceof Projectile projectile)) {
      return;
    }

    if (!(projectile.getShooter() instanceof Player attacker)) {
      return;
    }

    for (Projectile firedProjectile : firedProjectiles) {
      if (projectile == firedProjectile) {
        Bukkit.getPluginManager().callEvent(
            new PlayerOnPlayerDamageBuilder()
                .damageType(DamageType.RANGED)
                .damaged(damaged)
                .attacker(attacker)
                .weapon(ItemStackUtil.toItemStack(projectile))
                .build());
      }
    }

    if (!(projectile instanceof ThrownPotion thrownPotion)) {
      return;
    }

    if (thrownPotion.getEffects().stream().anyMatch(
        effect -> effect.getType() == PotionEffectType.HARM ||
            effect.getType() == PotionEffectType.POISON)) {
      Bukkit.getPluginManager().callEvent(
          new PlayerOnPlayerDamageBuilder()
              .damageType(DamageType.MAGIC)
              .damaged(damaged)
              .attacker(attacker)
              .weapon(ItemStackUtil.toItemStack(thrownPotion))
              .build());
    }
  }

  @EventHandler
  public void onEntityDamage(EntityDamageByBlockEvent event) {
    if (!(event.getEntity() instanceof Player damaged)) {
      return;
    }

    if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION &&
        damaged.getLocation().distance(this.lastExplodedBlock.getLocation()) <= 10) {

      Bukkit.getPluginManager().callEvent(
          new PlayerOnPlayerDamageBuilder()
              .damageType(DamageType.EXPLOSION_BLOCK)
              .damaged(damaged)
              .attacker(this.lastInteractedWithBlock)
              .weapon(new ItemStack(this.lastExplodedBlock.getType()))
              .build());
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
        this.lastExplodedBlock = event.getClickedBlock().getState();
        this.lastInteractedWithBlock = event.getPlayer();
      }
    }

    if (event.getClickedBlock().getBlockData() instanceof Bed &&
        event.getPlayer().getWorld().getEnvironment() != World.Environment.NORMAL) {
      this.lastExplodedBlock = event.getClickedBlock().getState();
      this.lastInteractedWithBlock = event.getPlayer();
    }
  }

  @EventHandler
  public void onProjectileFired(ProjectileLaunchEvent event) {
    if (event.getEntity().getShooter() instanceof Player) {
      this.firedProjectiles.add(event.getEntity());
    }
  }

  @EventHandler
  public void onProjectileHit(ProjectileHitEvent event) {
    Bukkit.getScheduler()
        .runTaskLater(AuroraCombat.INSTANCE, () -> firedProjectiles.remove(event.getEntity()), 20L);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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

  private static class PlayerOnPlayerDamageBuilder {
    private DamageType damageType;
    private Player damaged;
    private Player attacker;
    private ItemStack weapon;

    public PlayerOnPlayerDamageBuilder damageType(DamageType damageType) {
      this.damageType = damageType;
      return this;
    }

    public PlayerOnPlayerDamageBuilder damaged(Player damaged) {
      this.damaged = damaged;
      return this;
    }

    public PlayerOnPlayerDamageBuilder attacker(Player attacker) {
      this.attacker = attacker;
      return this;
    }

    public PlayerOnPlayerDamageBuilder weapon(ItemStack weapon) {
      this.weapon = weapon;
      return this;
    }

    public PlayerDamagedByPlayerEvent build() {
      return new PlayerDamagedByPlayerEvent(this.damageType, this.damaged, this.attacker,
          this.weapon);
    }
  }
}
