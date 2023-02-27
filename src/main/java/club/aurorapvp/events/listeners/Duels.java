package club.aurorapvp.events.listeners;

import club.aurorapvp.events.custom.DuelEndEvent;
import club.aurorapvp.modules.Rating;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Duels implements Listener {
  @EventHandler
  public void onDuelEnd(DuelEndEvent event) {
    Rating.changeRating(event.getWinner(), event.getLoser(), "duels");
  }
}
