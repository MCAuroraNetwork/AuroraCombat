package club.aurorapvp.events.custom;

import club.aurorapvp.modules.DamageType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerKilledByPlayerEvent extends PlayerDamagedByPlayerEvent implements Cancellable {
  private static final HandlerList HANDLERS = new HandlerList();
  private boolean isCancelled = false;

  public PlayerKilledByPlayerEvent(DamageType damageType) {
    super(damageType);
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