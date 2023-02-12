package club.aurorapvp.events;

import club.aurorapvp.listeners.Events;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerKilledByPlayerEvent extends Event implements Cancellable {
  private static final HandlerList HANDLERS = new HandlerList();
  private boolean isCancelled = false;
  private final Player player;
  private final Player killer;
  private final Object weapon;
  public PlayerKilledByPlayerEvent(PlayerDamagedByPlayerEvent damage) {
    this.player = damage.getPlayer();
    this.weapon = damage.getWeapon();
    this.killer = damage.getDamager();
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

  public boolean killedBySelf() {
    return player == killer;
  }

  public Player getPlayer() {
    return player;
  }

  public Player getKiller() {
    return killer;
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