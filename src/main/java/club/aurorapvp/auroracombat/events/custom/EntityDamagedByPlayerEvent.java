package club.aurorapvp.auroracombat.events.custom;

import club.aurorapvp.auroracombat.enums.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EntityDamagedByPlayerEvent extends Event implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  private final DamageType damageType;
  protected final EntityDamageEvent lastDamage;
  private final Entity damaged;
  private final Player attacker;
  private final ItemStack weapon;
  protected boolean cancelled;

  public EntityDamagedByPlayerEvent(
      DamageType damageType, EntityDamageEvent lastDamage, Entity damaged, Player attacker, ItemStack weapon) {
    this.damageType = damageType;
    this.lastDamage = lastDamage;
    this.damaged = damaged;
    this.attacker = attacker;
    this.weapon = weapon;
  }

  public ItemStack getWeapon() {
    return weapon;
  }

  public Entity getDamaged() {
    return damaged;
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
