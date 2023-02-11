package club.aurorapvp.events;

import club.aurorapvp.listeners.Events;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerDamagedByPlayerEvent extends Event implements Cancellable {
  private static final HandlerList HANDLERS = new HandlerList();
  private boolean isCancelled = false;
  private final Player player;
  private Player damager;
  private Object weapon;
  public PlayerDamagedByPlayerEvent(Player p, EntityDamageEvent damage) {
    this.player = p;

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
      } default -> setCancelled(true);
    }
  }

  public Object getWeapon() {
    return weapon;
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
