package club.aurorapvp.auroracombat.events.custom;

import club.aurorapvp.auroracombat.modules.DeathMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerKilledByPlayerEvent extends PlayerDamagedByPlayerEvent {
  private static final HandlerList HANDLERS = new HandlerList();
  private final PlayerDeathEvent deathEvent;
  private final Player killer;

  public PlayerKilledByPlayerEvent(PlayerDamagedByPlayerEvent event, PlayerDeathEvent deathEvent) {
    super(event.getDamageType(), event.getDamaged(), event.getAttacker(), event.getWeapon());
    this.deathEvent = deathEvent;
    this.killer = event.getAttacker();
    new DeathMessage(this);
  }

  @SuppressWarnings("unused")
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  public Player getDead() {
    return deathEvent.getEntity();
  }

  public Player getKiller() {
    return killer;
  }

  public void deathMessage(Component message) {
    deathEvent.deathMessage(message);
  }

  @SuppressWarnings("unused")
  public PlayerDeathEvent getDeathEvent() {
    return deathEvent;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLERS;
  }
}
