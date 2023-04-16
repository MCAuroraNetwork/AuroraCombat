package club.aurorapvp.modules;

import static club.aurorapvp.AuroraCombat.COMPONENT_SERIALIZER;

import club.aurorapvp.configs.Lang;
import club.aurorapvp.events.custom.PlayerKilledByPlayerEvent;
import club.aurorapvp.util.MaterialUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

public class DeathMessage {

  public DeathMessage(PlayerKilledByPlayerEvent event) {
    switch (event.getDamageType()) {
      case MELEE -> {
        ItemStack weapon = (ItemStack) event.getWeapon();

        event.deathMessage(
            Lang.formatComponent("death-message.killed-by-player-slain",
                event.getDamaged().getName(), event.getDamager().getName(),
                COMPONENT_SERIALIZER.serialize(weapon.displayName())));
      }
      case EXPLOSION_ENTITY -> {
        Entity weapon = (Entity) event.getWeapon();

        event.deathMessage(
            Lang.formatComponent("death-message.killed-by-player-explosion",
                event.getDamaged().getName(), event.getDamager().getName(),
                COMPONENT_SERIALIZER.serialize(weapon.teamDisplayName())));
      }
      case EXPLOSION_BLOCK -> {
        Block weapon = (Block) event.getWeapon();

        event.deathMessage(
            Lang.formatComponent("death-message.killed-by-player-explosion",
                event.getDamaged().getName(), event.getDamager().getName(),
                MaterialUtil.getFriendlyName(weapon.getType())));
      }
      case RANGED -> {
        Projectile weapon = (Projectile) event.getWeapon();

        event.deathMessage(
            Lang.formatComponent("death-message.killed-by-player-shot",
                event.getDamaged().getName(), event.getDamager().getName(),
                COMPONENT_SERIALIZER.serialize(weapon.teamDisplayName())));
      }
      default -> event.deathMessage(
          Lang.formatComponent("death-message.killed-by-player-generic",
              event.getDamaged().getName(), event.getDamager().getName()));
    }
  }
}
