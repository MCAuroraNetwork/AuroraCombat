package club.aurorapvp.auroracombat.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CombatTagRemovedEvent extends Event {

  private static final HandlerList HANDLERS = new HandlerList();
  private final Player playerOne;
  private final Player playerTwo;

  public CombatTagRemovedEvent(Player playerOne, Player playerTwo) {
    this.playerOne = playerOne;
    this.playerTwo = playerTwo;
  }

  public Player getPlayerOne() {
    return playerOne;
  }

  public Player getPlayerTwo() {
    return playerTwo;
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
