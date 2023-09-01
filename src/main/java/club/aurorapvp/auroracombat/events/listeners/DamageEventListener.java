package club.aurorapvp.auroracombat.events.listeners;

import static club.aurorapvp.auroracombat.events.listeners.CombatEventListener.lastDamage;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.config.Config;
import club.aurorapvp.auroracombat.enums.DamageType;
import club.aurorapvp.auroracombat.events.custom.PlayerDamagedByPlayerEvent;
import club.aurorapvp.auroracombat.modules.BlockFallDamage;
import club.aurorapvp.auroracombat.util.ItemStackUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.kyori.adventure.text.Component;
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

public class DamageEventListener implements Listener {

  private final HashSet<Projectile> firedProjectiles = new HashSet<>();
  private final Map<Player, Component> lastPlacedCrystalName = new HashMap<>();
  private final Map<Player, ItemStack> lastUsedBow = new HashMap<>();
  private Player lastCrystalAttacker;
  private Player lastInteractedWithBlock;
  private BlockState lastExplodedBlock;

  @EventHandler
  public void onEntityDamage(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof EnderCrystal
        && event.getDamager() instanceof Player attacker) {
      this.lastCrystalAttacker = attacker;
    }

    if (!(event.getEntity() instanceof Player damaged)) {
      return;
    }

    lastDamage.put(damaged, event);

    if (event.getDamager() instanceof EnderCrystal) {
      new PlayerOnPlayerDamageBuilder()
          .setDamageType(DamageType.EXPLOSION_ENTITY)
          .setDamaged(damaged)
          .setAttacker(this.lastCrystalAttacker)
          .setWeapon(ItemStackUtil.toEndCrystalItemStack(
              lastPlacedCrystalName.get(this.lastCrystalAttacker)))
          .setEvent(event)
          .build().callEvent();
    }

    if (event.getDamager() instanceof Player attacker) {
      new PlayerOnPlayerDamageBuilder()
          .setDamageType(DamageType.MELEE)
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
        new PlayerOnPlayerDamageBuilder()
            .setDamageType(DamageType.RANGED)
            .setDamaged(damaged)
            .setAttacker(attacker)
            .setWeapon(lastUsedBow.get(attacker))
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
      new PlayerOnPlayerDamageBuilder()
          .setDamageType(DamageType.MAGIC)
          .setDamaged(damaged)
          .setAttacker(attacker)
          .setWeapon(ItemStackUtil.toItemStack(thrownPotion))
          .setEvent(event)
          .build().callEvent();
    }
  }

  @EventHandler
  public void onEntityDamage(EntityDamageByBlockEvent event) {
    if (!(event.getEntity() instanceof Player damaged)) {
      return;
    }

    lastDamage.put(damaged, event);

    if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
        && damaged.getLocation().distance(this.lastExplodedBlock.getLocation()) <= 10) {

      new PlayerOnPlayerDamageBuilder()
          .setDamageType(DamageType.EXPLOSION_BLOCK)
          .setDamaged(damaged)
          .setAttacker(this.lastInteractedWithBlock)
          .setWeapon(new ItemStack(this.lastExplodedBlock.getType()))
          .setEvent(event)
          .build().callEvent();
    }
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.getClickedBlock() == null) {
      return;
    }

    if (event.getPlayer().getInventory().getItemInMainHand().getType()
        .equals(Material.END_CRYSTAL)) {
      lastPlacedCrystalName.put(event.getPlayer(),
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
  public void onProjectileFired(ProjectileLaunchEvent event) {
    if (event.getEntity().getShooter() instanceof Player player) {
      this.firedProjectiles.add(event.getEntity());
      this.lastUsedBow.put(player, player.getInventory().getItemInMainHand());
    }
  }

  @EventHandler
  public void onProjectileHit(ProjectileHitEvent event) {
    Bukkit.getScheduler()
        .runTaskLater(AuroraCombat.INSTANCE, () -> firedProjectiles.remove(event.getEntity()), 20L);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onFallDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    if (!(event.getCause() == EntityDamageEvent.DamageCause.FALL)) {
      return;
    }

    if (Config.get().getBoolean("misc.fall-damage.enable-first")) {
      return;
    }

    if (!BlockFallDamage.shouldTakeDamage(player)) {
      event.setCancelled(true);
      BlockFallDamage.setVulnerable(player);
    }
  }

  private static class PlayerOnPlayerDamageBuilder {

    private DamageType damageType;
    private EntityDamageEvent event;
    private Player damaged;
    private Player attacker;
    private ItemStack weapon;

    public PlayerOnPlayerDamageBuilder setDamageType(DamageType damageType) {
      this.damageType = damageType;
      return this;
    }

    public PlayerOnPlayerDamageBuilder setDamaged(Player damaged) {
      this.damaged = damaged;
      return this;
    }

    public PlayerOnPlayerDamageBuilder setAttacker(Player attacker) {
      this.attacker = attacker;
      return this;
    }

    public PlayerOnPlayerDamageBuilder setWeapon(ItemStack weapon) {
      this.weapon = weapon;
      return this;
    }

    public PlayerOnPlayerDamageBuilder setEvent(EntityDamageEvent event) {
      this.event = event;
      return this;
    }

    public PlayerDamagedByPlayerEvent build() {
      return new PlayerDamagedByPlayerEvent(
          this.damageType, this.event, this.damaged, this.attacker, this.weapon);
    }
  }
}
