package club.aurorapvp.modules;

import club.aurorapvp.configs.Lang;
import club.aurorapvp.events.custom.PlayerKilledByPlayerEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class DeathMessage implements Listener {
  @EventHandler
  public void onKilled(PlayerKilledByPlayerEvent event) {
    switch (event.getDamageType()) {
      case MELEE -> {
        ItemStack weapon = (ItemStack) event.getWeapon();

        event.deathMessage(
            Lang.formatComponent("death-message.killed-by-player-slain",
                event.getDamaged(), event.getDamager(), weapon.displayName()));
      }
      case EXPLOSION_ENTITY -> {
        Entity weapon = (Entity) event.getWeapon();

        event.deathMessage(
            Lang.formatComponent("death-message.killed-by-player-explosion",
                event.getDamaged(), event.getDamager(), weapon.teamDisplayName()));
      }
      case EXPLOSION_BLOCK -> {
        Block weapon = (Block) event.getWeapon();

        event.deathMessage(
            Lang.formatComponent("death-message.killed-by-player-explosion",
                event.getDamaged(), event.getDamager(), weapon.getType().toString()));
      }
      case RANGED -> {
        Projectile weapon = (Projectile) event.getWeapon();

        event.deathMessage(
            Lang.formatComponent("death-message.killed-by-player-shot",
                event.getDamaged(), event.getDamager(), weapon.teamDisplayName()));
      }
      default -> event.deathMessage(
          Lang.formatComponent("death-message.killed-by-player-generic",
              event.getDamaged(), event.getDamager()));
    }
  }
}
