package club.aurorapvp.events.custom;

import club.aurorapvp.modules.DeathMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerKilledByPlayerEvent extends PlayerDamagedByPlayerEvent implements Cancellable {
  private static final HandlerList HANDLERS = new HandlerList();
  private final PlayerDeathEvent deathEvent;
  private boolean isCancelled = false;

  public PlayerKilledByPlayerEvent(PlayerDamagedByPlayerEvent event, PlayerDeathEvent deathEvent) {
    super(event.getDamageType(), event.getDamaged(), event.getAttacker(), event.getWeapon());
    this.deathEvent = deathEvent;
    new DeathMessage(this);
  }

  public void deathMessage(Component message) {
    deathEvent.deathMessage(message);
  }

  @SuppressWarnings("unused")
  public PlayerDeathEvent getDeathEvent() {
    return deathEvent;
  }

  @Override
  public boolean isCancelled() {
    return this.isCancelled;
  }

  @Override
  public void setCancelled(boolean isCancelled) {
    this.isCancelled = isCancelled;
  }

  @SuppressWarnings("unused")
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLERS;
  }
}