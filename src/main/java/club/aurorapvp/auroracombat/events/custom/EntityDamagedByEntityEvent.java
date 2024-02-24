package club.aurorapvp.auroracombat.events.custom;

import club.aurorapvp.auroracombat.enums.AttackType;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EntityDamagedByEntityEvent extends Event implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  private final AttackType attackType;
  protected final EntityDamageEvent lastDamage;
  private final Entity damaged;
  private final Entity attacker;
  private final ItemStack weapon;
  protected boolean cancelled;

  public EntityDamagedByEntityEvent(
      AttackType attackType, EntityDamageEvent lastDamage, Entity damaged, Entity attacker, ItemStack weapon) {
    this.attackType = attackType;
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

  public Entity getAttacker() {
    return attacker;
  }

  public AttackType getDamageType() {
    return attackType;
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
