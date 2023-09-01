package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.config.Lang;
import club.aurorapvp.auroracombat.events.custom.PlayerKilledByPlayerEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;

public class DeathMessage {

  public DeathMessage(PlayerKilledByPlayerEvent event) {
    ItemStack weapon = event.getWeapon();
    HoverEvent<HoverEvent.ShowItem> hover = weapon.asHoverEvent();

    Component weaponName = weapon.displayName().decoration(TextDecoration.ITALIC, false);

    switch (event.getDamageType()) {
      case MELEE -> event.deathMessage(
          Lang.formatComponent(
                  "death-message.killed-by-player-slain",
                  event.getDamaged().getName(),
                  event.getAttacker().getName(),
                  MiniMessage.miniMessage().serialize(weaponName))
              .hoverEvent(hover));
      case EXPLOSION_ENTITY, EXPLOSION_BLOCK -> event.deathMessage(
          Lang.formatComponent(
                  "death-message.killed-by-player-explosion",
                  event.getDamaged().getName(),
                  event.getAttacker().getName(),
                  MiniMessage.miniMessage().serialize(weaponName))
              .hoverEvent(hover));
      case RANGED -> event.deathMessage(
          Lang.formatComponent(
                  "death-message.killed-by-player-shot",
                  event.getDamaged().getName(),
                  event.getAttacker().getName(),
                  MiniMessage.miniMessage().serialize(weaponName.colorIfAbsent(NamedTextColor.AQUA)))
              .hoverEvent(hover));
      case MAGIC -> event.deathMessage(
          Lang.formatComponent(
                  "death-message.killed-by-player-magic",
                  event.getDamaged().getName(),
                  event.getAttacker().getName(),
                  MiniMessage.miniMessage().serialize(weaponName))
              .hoverEvent(hover));
      case COMBAT_LOG ->
          event.deathMessage(Lang.formatComponent("combat-logged", event.getKiller().getName()));
      default -> event.deathMessage(
          Lang.formatComponent(
                  "death-message.killed-by-player-generic",
                  event.getDamaged().getName(),
                  event.getAttacker().getName(),
                  MiniMessage.miniMessage().serialize(weaponName))
              .hoverEvent(hover));
    }
  }
}
