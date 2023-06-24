package club.aurorapvp.auroracombat.events.listeners;

import club.aurorapvp.auroracombat.configs.Config;
import club.aurorapvp.auroracombat.configs.Lang;
import club.aurorapvp.auroracombat.events.custom.PlayerDamagedByPlayerEvent;
import club.aurorapvp.auroracombat.events.custom.PlayerKilledByPlayerEvent;
import club.aurorapvp.auroracombat.modules.BlockFallDamage;
import club.aurorapvp.auroracombat.modules.CombatTag;
import club.aurorapvp.auroracombat.modules.Rating;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatTagEvents implements Listener {
  private PlayerDamagedByPlayerEvent lastDamage;

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    if (CombatTag.isTagged(event.getPlayer())) {
      event.getPlayer().setHealth(0);
    }
  }

  @EventHandler
  public void onCommandRun(PlayerCommandPreprocessEvent event) {
    if (CombatTag.isTagged(event.getPlayer()) &&
        !Config.get().getBoolean("combat-tag.allow-commands")) {
      event.getPlayer().sendMessage(Lang.getComponent("commands-disabled"));

      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    CombatTag.removeTags(event.getPlayer());
    BlockFallDamage.setInVulnerable(event.getPlayer());

    CombatTag.removeTags(event.getPlayer());
    BlockFallDamage.setInVulnerable(event.getPlayer());

    if (lastDamage == null) {
      return;
    }

    if (event.getPlayer().equals(lastDamage.getDamaged())) {
      Bukkit.getPluginManager()
          .callEvent(new PlayerKilledByPlayerEvent(lastDamage, event));
      lastDamage = null;
    }
  }

  @EventHandler
  public void onKilledByPlayer(PlayerKilledByPlayerEvent event) {
    for (String type : Rating.getTypes()) {
      if (Rating.isUpdating(event.getDamaged().getLocation(), type)) {
        Rating.changeRating(event.getDamaged(), event.getAttacker(), type);
      }
    }
  }

  @EventHandler
  public void onDamagedByPlayer(PlayerDamagedByPlayerEvent event) {
    if (!event.getAttacker().equals(event.getDamaged())) {
      new CombatTag(event.getDamaged(), event.getAttacker());
      lastDamage = event;
    }
  }
}