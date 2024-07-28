package club.aurorapvp.auroracombat.events.listeners;

import static club.aurorapvp.auroracombat.events.listeners.CombatEventListener.lastDamage;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.enums.AttackType;
import club.aurorapvp.auroracombat.events.custom.EntityDamagedByEntityEvent;
import club.aurorapvp.auroracombat.events.custom.PlayerDamagedByPlayerEvent;
import club.aurorapvp.auroracombat.modules.BlockFallDamage;
import club.aurorapvp.auroracombat.util.ItemStackUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class DamageEventListener implements Listener {

  private final HashSet<Projectile> firedProjectiles = new HashSet<>();
  private final Map<Entity, ItemStack> lastPlacedCrystal = new HashMap<>();
  private final Map<Entity, ItemStack> lastUsedBow = new HashMap<>();
  private final Map<Entity, Entity> crystalsAttacked = new ConcurrentHashMap<>();
  private final Map<BlockState, Entity> blocksExploded = new ConcurrentHashMap<>();

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onEntityDamage(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof EnderCrystal enderCrystal) {
      crystalsAttacked.put(enderCrystal, event.getDamager());

      new BukkitRunnable() {
        @Override
        public void run() {
          crystalsAttacked.remove(enderCrystal);
        }
      }.runTaskLaterAsynchronously(AuroraCombat.getInstance(), 1);
      return;
    }

    Entity damaged = event.getEntity();

    lastDamage.put(damaged.getUniqueId(), event);

    if (event.getDamager() instanceof EnderCrystal enderCrystal) {
      new EntityOnEntityDamageBuilder()
          .setDamageType(AttackType.EXPLOSION_ENTITY)
          .setDamaged(damaged)
          .setAttacker(crystalsAttacked.get(enderCrystal))
          .setWeapon(lastPlacedCrystal.getOrDefault(crystalsAttacked.get(enderCrystal), null))
          .setEvent(event)
          .build()
          .callEvent();
      return;
    }

    if (event.getDamageSource().getDamageType() == DamageType.MOB_ATTACK) {
      new EntityOnEntityDamageBuilder()
          .setDamageType(AttackType.MELEE)
          .setDamaged(damaged)
          .setAttacker(event.getDamager())
          .setWeapon(null)
          .setEvent(event)
          .build()
          .callEvent();
      return;
    }

    if (event.getDamager() instanceof Player attacker) {
      new EntityOnEntityDamageBuilder()
          .setDamageType(AttackType.MELEE)
          .setDamaged(damaged)
          .setAttacker(attacker)
          .setWeapon(attacker.getInventory().getItemInMainHand())
          .setEvent(event)
          .build()
          .callEvent();
      return;
    }

    if (!(event.getDamager() instanceof Projectile projectile)) {
      return;
    }

    if (!(projectile.getShooter() instanceof Player attacker)) {
      return;
    }

    for (Projectile firedProjectile : firedProjectiles) {
      if (projectile == firedProjectile) {
        new EntityOnEntityDamageBuilder()
            .setDamageType(AttackType.RANGED)
            .setDamaged(damaged)
            .setAttacker(attacker)
            .setWeapon(lastUsedBow.get(attacker))
            .setEvent(event)
            .build()
            .callEvent();
        return;
      }
    }

    if (!(projectile instanceof ThrownPotion thrownPotion)) {
      return;
    }

    if (thrownPotion.getEffects().stream()
        .anyMatch(
            effect ->
                effect.getType() == PotionEffectType.INSTANT_DAMAGE
                    || effect.getType() == PotionEffectType.POISON)) {
      new EntityOnEntityDamageBuilder()
          .setDamageType(AttackType.MAGIC)
          .setDamaged(damaged)
          .setAttacker(attacker)
          .setWeapon(ItemStackUtil.toItemStack(thrownPotion))
          .setEvent(event)
          .build()
          .callEvent();
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onEntityDamage(EntityDamageByBlockEvent event) {
    Entity damaged = event.getEntity();

    lastDamage.put(damaged.getUniqueId(), event);

    if (event.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
      return;
    }

    BlockState explosive = event.getDamagerBlockState();

    if (explosive == null) {
      return;
    }

    for (BlockState blockState : blocksExploded.keySet()) {
      if (explosive.equals(blockState)) {
        new EntityOnEntityDamageBuilder()
            .setDamageType(AttackType.EXPLOSION_BLOCK)
            .setDamaged(damaged)
            .setAttacker(blocksExploded.get(blockState))
            .setWeapon(new ItemStack(blockState.getType()))
            .setEvent(event)
            .build()
            .callEvent();
        return;
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.getClickedBlock() == null) {
      return;
    }

    if (event
        .getPlayer()
        .getInventory()
        .getItemInMainHand()
        .getType()
        .equals(Material.END_CRYSTAL)) {
      lastPlacedCrystal.put(
          event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand());
      return;
    }

    if (event.getClickedBlock().getBlockData() instanceof RespawnAnchor respawnAnchor) {
      if ((respawnAnchor.getCharges() > 0
              && event.getPlayer().getInventory().getItemInMainHand().getType()
                  != Material.GLOWSTONE
              && event.getPlayer().getWorld().getEnvironment() != World.Environment.NETHER)
          || respawnAnchor.getCharges() >= 4) {
        blocksExploded.put(event.getClickedBlock().getState(), event.getPlayer());

        new BukkitRunnable() {
          @Override
          public void run() {
            blocksExploded.remove(event.getClickedBlock().getState());
          }
        }.runTaskLaterAsynchronously(AuroraCombat.getInstance(), 1);
        return;
      }
    }

    if (event.getClickedBlock().getBlockData() instanceof Bed
        && event.getPlayer().getWorld().getEnvironment() != World.Environment.NORMAL) {
      blocksExploded.put(event.getClickedBlock().getState(), event.getPlayer());

      new BukkitRunnable() {
        @Override
        public void run() {
          blocksExploded.remove(event.getClickedBlock().getState());
        }
      }.runTaskLaterAsynchronously(AuroraCombat.getInstance(), 1);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onProjectileFired(ProjectileLaunchEvent event) {
    if (!(event.getEntity().getShooter() instanceof Player player)) {
      return;
    }

    this.firedProjectiles.add(event.getEntity());
    this.lastUsedBow.put(player, player.getInventory().getItemInMainHand());
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onProjectileHit(ProjectileHitEvent event) {
    Bukkit.getScheduler()
        .runTaskLater(
            AuroraCombat.getInstance(), () -> firedProjectiles.remove(event.getEntity()), 20L);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onFallDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    if (!(event.getCause() == EntityDamageEvent.DamageCause.FALL)) {
      return;
    }

    if (AuroraCombat.getInstance().getConfig().getBoolean("misc.fall-damage.enable-first")) {
      return;
    }

    if (!BlockFallDamage.shouldTakeDamage(player)) {
      event.setCancelled(true);
      BlockFallDamage.setVulnerable(player);
    }
  }

  private static class EntityOnEntityDamageBuilder {
    private AttackType attackType;
    private EntityDamageEvent event;
    private Entity damaged;
    private Entity attacker;
    private ItemStack weapon;

    public EntityOnEntityDamageBuilder setDamageType(AttackType attackType) {
      this.attackType = attackType;
      return this;
    }

    public EntityOnEntityDamageBuilder setDamaged(Entity damaged) {
      this.damaged = damaged;
      return this;
    }

    public EntityOnEntityDamageBuilder setAttacker(Entity attacker) {
      this.attacker = attacker;
      return this;
    }

    public EntityOnEntityDamageBuilder setWeapon(ItemStack weapon) {
      this.weapon = weapon;
      return this;
    }

    public EntityOnEntityDamageBuilder setEvent(EntityDamageEvent event) {
      this.event = event;
      return this;
    }

    public EntityDamagedByEntityEvent build() {
      if (this.damaged instanceof Player && this.attacker instanceof Player) {
        return new PlayerDamagedByPlayerEvent(
            this.attackType,
            this.event,
            (Player) this.damaged,
            (Player) this.attacker,
            this.weapon);
      }

      return new EntityDamagedByEntityEvent(
          this.attackType, this.event, this.damaged, this.attacker, this.weapon);
    }
  }
}
