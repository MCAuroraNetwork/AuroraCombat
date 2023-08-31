package club.aurorapvp.auroracombat.events.custom;

import club.aurorapvp.auroracombat.enums.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerDamagedByPlayerEvent extends Event implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  private final DamageType damageType;
  protected final EntityDamageEvent lastDamage;
  private final Player attacked;
  private final Player attacker;
  private final ItemStack weapon;
  protected boolean cancelled;

  public PlayerDamagedByPlayerEvent(
      DamageType damageType, EntityDamageEvent lastDamage, Player attacked, Player attacker, ItemStack weapon) {
    this.damageType = damageType;
    this.lastDamage = lastDamage;
    this.attacked = attacked;
    this.attacker = attacker;
    this.weapon = weapon;
  }

  public ItemStack getWeapon() {
    return weapon;
  }

  public Player getDamaged() {
    return attacked;
  }

  public Player getAttacker() {
    return attacker;
  }

  public DamageType getDamageType() {
    return damageType;
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
