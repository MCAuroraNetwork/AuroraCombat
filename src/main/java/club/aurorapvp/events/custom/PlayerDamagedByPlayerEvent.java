package club.aurorapvp.events.custom;

import club.aurorapvp.enums.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerDamagedByPlayerEvent extends Event implements Cancellable {
  private static final HandlerList HANDLERS = new HandlerList();
  private final DamageType damageType;
  private boolean isCancelled = false;

  public PlayerDamagedByPlayerEvent(DamageType damageType) {
    this.damageType = damageType;
  }

  public Player getDamaged() {
    return damageType.getAttacked();
  }

  public Player getDamager() {
    return damageType.getAttacker();
  }

  public DamageType getDamageType() {
    return damageType;
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
