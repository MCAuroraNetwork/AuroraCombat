package club.aurorapvp.auroracombat.events.listeners;

import static club.aurorapvp.auroracombat.events.listeners.CombatEventListener.lastDamage;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.enums.AttackType;
import club.aurorapvp.auroracombat.events.custom.EntityDamagedByEntityEvent;
import club.aurorapvp.auroracombat.events.custom.PlayerDamagedByPlayerEvent;
import club.aurorapvp.auroracombat.modules.BlockFallDamage;
import club.aurorapvp.auroracombat.util.ItemStackUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
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
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class DamageEventListener implements Listener {

  private final HashSet<Projectile> firedProjectiles = new HashSet<>();
  private final Map<UUID, Component> lastPlacedCrystalName = new HashMap<>();
  private final Map<UUID, ItemStack> lastUsedBow = new HashMap<>();
  private Entity lastCrystalAttacker;
  private Entity lastInteractedWithBlock;
  private BlockState lastExplodedBlock;

  @EventHandler
      (priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onEntityDamage(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof EnderCrystal) {
      this.lastCrystalAttacker = event.getEntity();
    }

    Entity damaged = event.getEntity();

    lastDamage.put(damaged.getUniqueId(), event);

    if (event.getDamager() instanceof EnderCrystal) {
      new EntityOnPlayerDamageBuilder()
          .setDamageType(AttackType.EXPLOSION_ENTITY)
          .setDamaged(damaged)
          .setAttacker(this.lastCrystalAttacker)
          .setWeapon(ItemStackUtil.toEndCrystalItemStack(
              lastPlacedCrystalName.get(this.lastCrystalAttacker.getUniqueId())))
          .setEvent(event)
          .build().callEvent();
    }

    if (event.getDamageSource().getDamageType() == DamageType.MOB_ATTACK) {
      new EntityOnPlayerDamageBuilder()
          .setDamageType(AttackType.MELEE)
          .setDamaged(damaged)
          .setAttacker(event.getDamager())
          .setWeapon(null)
          .setEvent(event)
          .build().callEvent();
    }

    if (event.getDamager() instanceof Player attacker) {
      new EntityOnPlayerDamageBuilder()
          .setDamageType(AttackType.MELEE)
          .setDamaged(damaged)
          .setAttacker(attacker)
          .setWeapon(attacker.getInventory().getItemInMainHand())
          .setEvent(event)
          .build().callEvent();
    }

    if (!(event.getDamager() instanceof Projectile projectile)) {
      return;
    }

    if (!(projectile.getShooter() instanceof Player attacker)) {
      return;
    }

    for (Projectile firedProjectile : firedProjectiles) {
      if (projectile == firedProjectile) {
        new EntityOnPlayerDamageBuilder()
            .setDamageType(AttackType.RANGED)
            .setDamaged(damaged)
            .setAttacker(attacker)
            .setWeapon(lastUsedBow.get(attacker.getUniqueId()))
            .setEvent(event)
            .build().callEvent();
      }
    }

    if (!(projectile instanceof ThrownPotion thrownPotion)) {
      return;
    }

    if (thrownPotion.getEffects().stream()
        .anyMatch(
            effect ->
                effect.getType() == PotionEffectType.HARM
                    || effect.getType() == PotionEffectType.POISON)) {
      new EntityOnPlayerDamageBuilder()
          .setDamageType(AttackType.MAGIC)
          .setDamaged(damaged)
          .setAttacker(attacker)
          .setWeapon(ItemStackUtil.toItemStack(thrownPotion))
          .setEvent(event)
          .build().callEvent();
    }
  }

  @EventHandler
      (priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onEntityDamage(EntityDamageByBlockEvent event) {
    Entity damaged = event.getEntity();

    lastDamage.put(damaged.getUniqueId(), event);

    if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
        && damaged.getLocation().distance(this.lastExplodedBlock.getLocation()) <= 10) {

      new EntityOnPlayerDamageBuilder()
          .setDamageType(AttackType.EXPLOSION_BLOCK)
          .setDamaged(damaged)
          .setAttacker(this.lastInteractedWithBlock)
          .setWeapon(new ItemStack(this.lastExplodedBlock.getType()))
          .setEvent(event)
          .build().callEvent();
    }
  }

  @EventHandler
      (priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.getClickedBlock() == null) {
      return;
    }

    if (event.getPlayer().getInventory().getItemInMainHand().getType()
        .equals(Material.END_CRYSTAL)) {
      lastPlacedCrystalName.put(event.getPlayer().getUniqueId(),
          event.getPlayer().getInventory().getItemInMainHand().displayName());
    }

    if (event.getClickedBlock().getBlockData() instanceof RespawnAnchor respawnAnchor) {
      if ((respawnAnchor.getCharges() > 0
          && event.getPlayer().getInventory().getItemInMainHand().getType()
          != Material.GLOWSTONE)
          || respawnAnchor.getCharges() >= 4) {
        this.lastExplodedBlock = event.getClickedBlock().getState();
        this.lastInteractedWithBlock = event.getPlayer();
      }
    }

    if (event.getClickedBlock().getBlockData() instanceof Bed
        && event.getPlayer().getWorld().getEnvironment() != World.Environment.NORMAL) {
      this.lastExplodedBlock = event.getClickedBlock().getState();
      this.lastInteractedWithBlock = event.getPlayer();
    }
  }

  @EventHandler
      (priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onProjectileFired(ProjectileLaunchEvent event) {
    if (event.getEntity().getShooter() instanceof Player player) {
      this.firedProjectiles.add(event.getEntity());
      this.lastUsedBow.put(player.getUniqueId(), player.getInventory().getItemInMainHand());
    }
  }

  @EventHandler
      (priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onProjectileHit(ProjectileHitEvent event) {
    Bukkit.getScheduler()
        .runTaskLater(AuroraCombat.getInstance(), () -> firedProjectiles.remove(event.getEntity()),
            20L);
  }

  @EventHandler
      (priority = EventPriority.HIGHEST, ignoreCancelled = true)
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

  private static class EntityOnPlayerDamageBuilder {

    private AttackType attackType;
    private EntityDamageEvent event;
    private Entity damaged;
    private Entity attacker;
    private ItemStack weapon;

    public EntityOnPlayerDamageBuilder setDamageType(AttackType attackType) {
      this.attackType = attackType;
      return this;
    }

    public EntityOnPlayerDamageBuilder setDamaged(Entity damaged) {
      this.damaged = damaged;
      return this;
    }

    public EntityOnPlayerDamageBuilder setAttacker(Entity attacker) {
      this.attacker = attacker;
      return this;
    }

    public EntityOnPlayerDamageBuilder setWeapon(ItemStack weapon) {
      this.weapon = weapon;
      return this;
    }

    public EntityOnPlayerDamageBuilder setEvent(EntityDamageEvent event) {
      this.event = event;
      return this;
    }

    public EntityDamagedByEntityEvent build() {
      if (this.damaged instanceof Player && this.attacker instanceof Player) {
        return new PlayerDamagedByPlayerEvent(this.attackType, this.event, (Player) this.damaged,
            (Player) this.attacker, this.weapon);
      }

      return new EntityDamagedByEntityEvent(this.attackType, this.event, this.damaged,
          this.attacker, this.weapon);
    }
  }
}
