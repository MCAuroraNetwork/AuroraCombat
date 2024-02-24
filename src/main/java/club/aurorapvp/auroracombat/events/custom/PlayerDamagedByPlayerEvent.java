package club.aurorapvp.auroracombat.events.custom;

import club.aurorapvp.auroracombat.enums.AttackType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerDamagedByPlayerEvent extends EntityDamagedByEntityEvent implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  private final Player damaged;
  private final Player attacker;
  protected boolean cancelled;

  public PlayerDamagedByPlayerEvent(
      AttackType attackType, EntityDamageEvent lastDamage, Player damaged, Player attacker, ItemStack weapon) {
    super(attackType, lastDamage, damaged, attacker, weapon);

    this.damaged = damaged;
    this.attacker = attacker;
  }

  public Player getAttacker() {
    return attacker;
  }

  public Player getDamaged() {
    return damaged;
  }

  @SuppressWarnings("unused")
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLERS;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancel) {
    cancelled = cancel;

    lastDamage.setCancelled(cancel);
  }
}
