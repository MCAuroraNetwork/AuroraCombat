package club.aurorapvp.auroracombat.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CombatTagEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player tagged;
    private final Player tagger;

    public CombatTagEvent(Player tagged, Player opponent) {
        this.tagged = tagged;
        this.tagger = opponent;
    }

    public Player getTagged() {
        return tagged;
    }

    public Player getTagger() {
        return tagger;
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
