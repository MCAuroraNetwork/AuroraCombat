package club.aurorapvp.modules;

import club.aurorapvp.configs.Lang;
import club.aurorapvp.events.custom.PlayerKilledByPlayerEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;

public class DeathMessage {

  public DeathMessage(PlayerKilledByPlayerEvent event) {
    ItemStack weapon = event.getWeapon();
    HoverEvent<HoverEvent.ShowItem> hover = weapon.asHoverEvent();

    switch (event.getDamageType()) {
      case MELEE -> event.deathMessage(
          Lang.formatComponent("death-message.killed-by-player-slain",
                  event.getDamaged().getName(),
                  event.getDamager().getName(),
                  MiniMessage.miniMessage().serialize(weapon.displayName()))
              .hoverEvent(hover));
      case EXPLOSION_ENTITY, EXPLOSION_BLOCK -> event.deathMessage(
          Lang.formatComponent("death-message.killed-by-player-explosion",
                  event.getDamaged().getName(),
                  event.getDamager().getName(),
                  MiniMessage.miniMessage().serialize(weapon.displayName()))
              .hoverEvent(hover));
      case RANGED -> event.deathMessage(
          Lang.formatComponent("death-message.killed-by-player-shot",
                  event.getDamaged().getName(),
                  event.getDamager().getName(),
                  MiniMessage.miniMessage().serialize(weapon.displayName()))
              .hoverEvent(hover));
      case MAGIC -> event.deathMessage(
          Lang.formatComponent("death-message.killed-by-player-magic",
                  event.getDamaged().getName(),
                  event.getDamager().getName(),
                  MiniMessage.miniMessage().serialize(weapon.displayName()))
              .hoverEvent(hover));
      default -> event.deathMessage(
          Lang.formatComponent("death-message.killed-by-player-generic",
                  event.getDamaged().getName(),
                  event.getDamager().getName(),
                  MiniMessage.miniMessage().serialize(weapon.displayName()))
              .hoverEvent(hover));
    }
  }
}
