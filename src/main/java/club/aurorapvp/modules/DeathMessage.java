package club.aurorapvp.modules;

import club.aurorapvp.configs.Lang;
import club.aurorapvp.events.PlayerKilledByPlayerEvent;

public class DeathMessage {
  public DeathMessage(PlayerKilledByPlayerEvent event) {
    switch (event.getDamageCause()) {
      case ENTITY_EXPLOSION -> event.deathMessage(
          Lang.formatComponent("killed-by-player-explosion", event.getPlayer().getName(), event.getDamager().getName(),
              event.getWeaponName()));
      case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> event.deathMessage(
          Lang.formatComponent("killed-by-player-slain", event.getPlayer().getName(), event.getDamager().getName(),
              event.getWeaponName()));
    }

    if (event.getWeapon() == null) {
      event.deathMessage(
          Lang.formatComponent("killed-by-player-generic", event.getPlayer().getName(), event.getDamager().getName()));
    }
  }
}
