package club.aurorapvp.events.custom;

import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerKilledByPlayerEvent extends PlayerDamagedByPlayerEvent implements Cancellable {
  private static final HandlerList HANDLERS = new HandlerList();
  private boolean isCancelled = false;
  private final PlayerDeathEvent deathEvent;

  public PlayerKilledByPlayerEvent(PlayerDamagedByPlayerEvent damage, PlayerDeathEvent deathEvent) {
    super(damage);
    this.deathEvent = deathEvent;
  }

  public void deathMessage(Component message) {
    deathEvent.deathMessage(message);
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