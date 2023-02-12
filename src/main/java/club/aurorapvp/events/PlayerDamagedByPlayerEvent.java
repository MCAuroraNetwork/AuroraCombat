package club.aurorapvp.events;

import club.aurorapvp.AuroraCombat;
import club.aurorapvp.listeners.Events;
import club.aurorapvp.modules.CombatTag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerDamagedByPlayerEvent extends Event implements Cancellable {
  private static final HandlerList HANDLERS = new HandlerList();
  private boolean isCancelled = false;
  private final EntityDamageEvent.DamageCause damageCause;
  private final Player player;
  private Player damager;
  private Object weapon;
  private final EntityDamageEvent damage;

  public PlayerDamagedByPlayerEvent(Player p, EntityDamageEvent damage) {
    this.player = p;
    this.damage = damage;
    this.damageCause = damage.getCause();

    if (p.isDead()) {
      setCancelled(true);
      return;
    }

    switch (damage.getCause()) {
      case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> {
        Player damager = Events.lastAttackedOtherPlayer.get(p);

        if (damager != null) {
          this.damager = damager;
          this.weapon = damager.getInventory().getItemInMainHand();
        }
      }

      case ENTITY_EXPLOSION -> {
        if (Events.lastDamagedByCrystal.containsKey(p)) {
          EnderCrystal crystalKiller = Events.lastDamagedByCrystal.get(p);
          if (Events.lastKilledCrystal.containsKey(crystalKiller)) {
            this.damager = Events.lastKilledCrystal.get(crystalKiller);
            this.weapon = crystalKiller;
          }
        }
      }

      case BLOCK_EXPLOSION -> {
        if (Events.lastDamagedByBlock.containsKey(p)) {
          this.damager = Events.lastInteractedWithBlock;
          this.weapon = Events.lastExplodedBlock;
        }
      }
      default -> setCancelled(true);
    }
  }

  public PlayerDamagedByPlayerEvent(Player p, EntityDamageEvent damage, PlayerDeathEvent death) {
    PlayerDamagedByPlayerEvent event = new PlayerDamagedByPlayerEvent(p, damage);
    this.player = p;
    this.damage = event.getDamage();
    this.damager = event.getDamager();
    this.damageCause = event.getDamageCause();
    this.weapon = event.getWeapon();

    if (CombatTag.isTagged(p)) {
      Player p1 = CombatTag.getRecentTag(p).getTagged();
      Player p2 = CombatTag.getRecentTag(p).getOpponent();

      if (p == p1) {
        damager = p2;
      } else {
        damager = p1;
      }
    }

    Bukkit.getPluginManager().callEvent(new PlayerKilledByPlayerEvent(this, death));
  }

  public PlayerDamagedByPlayerEvent(PlayerDamagedByPlayerEvent damage) {
    this.player = damage.getPlayer();
    this.damage = damage.getDamage();
    this.damager = damage.getDamager();
    this.damageCause = damage.getDamageCause();
    this.weapon = damage.getWeapon();
  }

  public EntityDamageEvent.DamageCause getDamageCause() {
    return damageCause;
  }

  public Object getWeapon() {
    return weapon;
  }

  public EntityDamageEvent getDamage() {
    return damage;
  }

  public String getWeaponName() {
    if (weapon instanceof EnderCrystal) {
      return ((EnderCrystal) weapon).getName();
    } else if (weapon instanceof Block) {
      return ((Block) weapon).getType().name().replace("_", " ").toLowerCase();
    } else if (weapon instanceof ItemStack) {
      return AuroraCombat.COMPONENT_SERIALIZER.serialize(((ItemStack) weapon).getItemMeta().displayName());
    } else {
      return null;
    }
  }

  public Material getWeaponType() {
    if (weapon instanceof EnderCrystal) {
      return Material.END_CRYSTAL;
    } else if (weapon instanceof Block) {
      return ((Block) weapon).getType();
    } else if (weapon instanceof ItemStack) {
      return ((ItemStack) weapon).getType();
    } else if (weapon instanceof Material) {
      return (Material) weapon;
    }
    return null;
  }

  public boolean damagedBySelf() {
    return player == damager;
  }

  public Player getPlayer() {
    return player;
  }

  public Player getDamager() {
    return damager;
  }

  @Override
  public boolean isCancelled() {
    return this.isCancelled;
  }

  @Override
  public void setCancelled(boolean isCancelled) {
    this.isCancelled = isCancelled;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLERS;
  }
}
