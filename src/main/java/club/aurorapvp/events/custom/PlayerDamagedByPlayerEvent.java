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
  private final Player attacked;
  private final Player attacker;
  private final Object weapon;
  private boolean isCancelled = false;

  public PlayerDamagedByPlayerEvent(DamageType damageType, Player attacked, Player attacker, Object weapon) {
    this.damageType = damageType;
    this.attacked = attacked;
    this.attacker = attacker;
    this.weapon = weapon;
  }

  public Object getWeapon() {
    return weapon;
  }

  public Player getDamaged() {
    return attacked;
  }

  public Player getDamager() {
    return attacker;
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
