package club.aurorapvp.events;

import club.aurorapvp.modules.DeathMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerKilledByPlayerEvent extends PlayerDamagedByPlayerEvent implements Cancellable {
  private static final HandlerList HANDLERS = new HandlerList();
  private boolean isCancelled = false;
  private final PlayerDeathEvent deathEvent;

  public PlayerKilledByPlayerEvent(PlayerDamagedByPlayerEvent damage, PlayerDeathEvent deathEvent) {
    super(damage.getPlayer(), deathEvent.getEntity().getLastDamageCause());
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