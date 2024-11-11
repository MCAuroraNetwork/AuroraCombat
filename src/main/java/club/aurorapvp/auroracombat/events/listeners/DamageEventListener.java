package club.aurorapvp.auroracombat.events.listeners;

import static club.aurorapvp.auroracombat.events.listeners.CombatEventListener.lastDamage;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.enums.DamageType;
import club.aurorapvp.auroracombat.events.custom.EntityDamagedByEntityEvent;
import club.aurorapvp.auroracombat.events.custom.PlayerDamagedByPlayerEvent;
import club.aurorapvp.auroracombat.modules.BlockFallDamage;
import club.aurorapvp.auroracombat.util.ItemStackUtil;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.*;
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
import org.bukkit.scheduler.BukkitRunnable;

public class DamageEventListener implements Listener {

  private final HashSet<Projectile> firedProjectiles = new HashSet<>();
  private final Map<UUID, ItemStack> lastPlacedCrystal = new HashMap<>();
  private final Map<UUID, ItemStack> lastUsedBow = new HashMap<>();
  private final Map<UUID, Entity> crystalsAttacked = new ConcurrentHashMap<>();
  private final Map<UUID, Long> lastAttackedCrystal = new HashMap<>();
  private final Map<UUID, Long> lastBlockExploded = new HashMap<>();
  private final Map<BlockState, Entity> blocksExploded = new ConcurrentHashMap<>();

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onEntityDamage(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof EnderCrystal enderCrystal) {
      crystalsAttacked.put(enderCrystal.getUniqueId(), event.getDamager());

      int cpsLimit = AuroraCombat.getInstance().getConfig().getInt("misc.crystal-cps-limit");

      if (cpsLimit > 0
          && lastAttackedCrystal.containsKey(event.getDamager().getUniqueId())
          && System.currentTimeMillis() - lastAttackedCrystal.get(event.getDamager().getUniqueId())
              <= 1000 / cpsLimit) {
        event.setCancelled(true);
        return;
      }

      lastAttackedCrystal.put(event.getDamager().getUniqueId(), System.currentTimeMillis());

      new BukkitRunnable() {
        @Override
        public void run() {
          crystalsAttacked.remove(enderCrystal.getUniqueId());
        }
      }.runTaskLaterAsynchronously(AuroraCombat.getInstance(), 1);
      return;
    }

    Entity damaged = event.getEntity();

    lastDamage.put(damaged.getUniqueId(), event);

    if (event.getDamager() instanceof EnderCrystal enderCrystal) {
      new EntityOnEntityDamageBuilder()
          .setDamageType(DamageType.EXPLOSION_ENTITY)
          .setDamaged(damaged)
          .setDamager(crystalsAttacked.get(enderCrystal.getUniqueId()))
          .setWeapon(
              lastPlacedCrystal.getOrDefault(
                  crystalsAttacked.get(enderCrystal.getUniqueId()).getUniqueId(), null))
          .setEvent(event)
          .build()
          .callEvent();
      return;
    }

    if (event.getDamageSource().getDamageType().equals(org.bukkit.damage.DamageType.MOB_ATTACK)) {
      new EntityOnEntityDamageBuilder()
          .setDamageType(DamageType.MELEE)
          .setDamaged(damaged)
          .setDamager(event.getDamager())
          .setWeapon(null)
          .setEvent(event)
          .build()
          .callEvent();
      return;
    }

    if (event.getDamager() instanceof Player damager) {
      new EntityOnEntityDamageBuilder()
          .setDamageType(DamageType.MELEE)
          .setDamaged(damaged)
          .setDamager(damager)
          .setWeapon(damager.getInventory().getItemInMainHand())
          .setEvent(event)
          .build()
          .callEvent();
      return;
    }

    if (!(event.getDamager() instanceof Projectile projectile)) {
      return;
    }

    if (!(projectile.getShooter() instanceof Player damager)) {
      return;
    }

    for (Projectile firedProjectile : firedProjectiles) {
      if (projectile.equals(firedProjectile)) {
        new EntityOnEntityDamageBuilder()
            .setDamageType(DamageType.RANGED)
            .setDamaged(damaged)
            .setDamager(damager)
            .setWeapon(lastUsedBow.get(damager.getUniqueId()))
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
                effect.getType().equals(PotionEffectType.INSTANT_DAMAGE)
                    || effect.getType().equals(PotionEffectType.POISON))) {
      new EntityOnEntityDamageBuilder()
          .setDamageType(DamageType.MAGIC)
          .setDamaged(damaged)
          .setDamager(damager)
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
            .setDamageType(DamageType.EXPLOSION_BLOCK)
            .setDamaged(damaged)
            .setDamager(blocksExploded.get(blockState))
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
          event.getPlayer().getUniqueId(), event.getPlayer().getInventory().getItemInMainHand());
      return;
    }

    if (event.getClickedBlock().getBlockData() instanceof RespawnAnchor respawnAnchor) {
      if ((respawnAnchor.getCharges() > 0
              && event.getPlayer().getInventory().getItemInMainHand().getType()
                  != Material.GLOWSTONE
              && event.getPlayer().getWorld().getEnvironment() != World.Environment.NETHER)
          || respawnAnchor.getCharges() >= 4) {

        int cpsLimit = AuroraCombat.getInstance().getConfig().getInt("misc.anchor-cps-limit");

        if (cpsLimit > 0
            && lastBlockExploded.containsKey(event.getPlayer().getUniqueId())
            && System.currentTimeMillis() - lastBlockExploded.get(event.getPlayer().getUniqueId())
                <= 1000 / cpsLimit) {
          event.setCancelled(true);
          return;
        }

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
    this.lastUsedBow.put(player.getUniqueId(), player.getInventory().getItemInMainHand());
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

    if (!(event.getCause().equals(EntityDamageEvent.DamageCause.FALL))) {
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
    private DamageType damageType;
    private EntityDamageEvent event;
    private Entity damaged;
    private Entity damager;
    private ItemStack weapon;

    public EntityOnEntityDamageBuilder setDamageType(DamageType damageType) {
      this.damageType = damageType;
      return this;
    }

    public EntityOnEntityDamageBuilder setDamaged(Entity damaged) {
      this.damaged = damaged;
      return this;
    }

    public EntityOnEntityDamageBuilder setDamager(Entity damager) {
      this.damager = damager;
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
      if (this.damaged instanceof Player && this.damager instanceof Player) {
        return new PlayerDamagedByPlayerEvent(
            this.damageType, this.event, (Player) this.damaged, (Player) this.damager, this.weapon);
      }

      return new EntityDamagedByEntityEvent(
          this.damageType, this.event, this.damaged, this.damager, this.weapon);
    }
  }
}
