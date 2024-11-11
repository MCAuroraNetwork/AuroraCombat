package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.enums.DamageType;
import club.aurorapvp.auroracombat.events.custom.PlayerKilledByPlayerEvent;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

public class DeathMessage {

  public DeathMessage(PlayerKilledByPlayerEvent event) {
    if (event.getDamageType().equals(DamageType.COMBAT_LOG)) {
      event.deathMessage(
          AuroraCombat.getInstance()
              .getLang()
              .formatComponent(
                  "death-message.combat-logged",
                  event.getDead().getName(),
                  event.getKiller().getName()));
      return;
    }

    ItemStack weapon = event.getWeapon();

    if (weapon == null) {
      event.deathMessage(
          AuroraCombat.getInstance()
              .getLang()
              .formatComponent(
                  "death-message.killed-by-player-generic",
                  event.getDamaged().getName(),
                  event.getDamager().getName()));
      return;
    }

    HoverEvent<HoverEvent.ShowItem> hover = weapon.asHoverEvent();

    Component weaponName = weapon.displayName();

    if (Objects.equals(weaponName.color(), TextColor.color(Color.WHITE.asRGB()))) {
      weaponName = weaponName.color(NamedTextColor.AQUA);
    }

    weaponName = weaponName.decoration(TextDecoration.ITALIC, false);

    switch (event.getDamageType()) {
      case MELEE ->
          event.deathMessage(
              AuroraCombat.getInstance()
                  .getLang()
                  .formatComponent(
                      "death-message.killed-by-player-slain",
                      event.getDamaged().getName(),
                      event.getDamager().getName(),
                      MiniMessage.miniMessage().serialize(weaponName))
                  .hoverEvent(hover));
      case EXPLOSION_ENTITY, EXPLOSION_BLOCK ->
          event.deathMessage(
              AuroraCombat.getInstance()
                  .getLang()
                  .formatComponent(
                      "death-message.killed-by-player-explosion",
                      event.getDamaged().getName(),
                      event.getDamager().getName(),
                      MiniMessage.miniMessage().serialize(weaponName))
                  .hoverEvent(hover));
      case RANGED ->
          event.deathMessage(
              AuroraCombat.getInstance()
                  .getLang()
                  .formatComponent(
                      "death-message.killed-by-player-shot",
                      event.getDamaged().getName(),
                      event.getDamager().getName(),
                      MiniMessage.miniMessage().serialize(weaponName))
                  .hoverEvent(hover));
      case MAGIC ->
          event.deathMessage(
              AuroraCombat.getInstance()
                  .getLang()
                  .formatComponent(
                      "death-message.killed-by-player-magic",
                      event.getDamaged().getName(),
                      event.getDamager().getName(),
                      MiniMessage.miniMessage().serialize(weaponName))
                  .hoverEvent(hover));
      default ->
          event.deathMessage(
              AuroraCombat.getInstance()
                  .getLang()
                  .formatComponent(
                      "death-message.killed-by-player-generic",
                      event.getDamaged().getName(),
                      event.getDamager().getName()));
    }
  }
}
