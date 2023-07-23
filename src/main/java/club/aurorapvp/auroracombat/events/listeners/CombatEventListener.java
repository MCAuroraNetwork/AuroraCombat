package club.aurorapvp.auroracombat.events.listeners;

import club.aurorapvp.auroracombat.config.Config;
import club.aurorapvp.auroracombat.config.Lang;
import club.aurorapvp.auroracombat.events.custom.PlayerDamagedByPlayerEvent;
import club.aurorapvp.auroracombat.events.custom.PlayerKilledByPlayerEvent;
import club.aurorapvp.auroracombat.modules.BlockFallDamage;
import club.aurorapvp.auroracombat.modules.CombatTag;
import club.aurorapvp.auroracombat.modules.KillDeathTracker;
import club.aurorapvp.auroracombat.modules.Rating;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatEventListener implements Listener {
  private PlayerDamagedByPlayerEvent lastDamage;

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    if (CombatTag.isTagged(event.getPlayer())) {
      event.getPlayer().setHealth(0);
    }
  }

  @EventHandler
  public void onCommandRun(PlayerCommandPreprocessEvent event) {
    if (CombatTag.isTagged(event.getPlayer())
        && !Config.get().getBoolean("combat-tag.allow-commands")) {
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
      Bukkit.getPluginManager().callEvent(new PlayerKilledByPlayerEvent(lastDamage, event));
      lastDamage = null;
    }
  }

  @EventHandler
  public void onKilledByPlayer(PlayerKilledByPlayerEvent event) {
    for (Rating rating : Rating.getRatings()) {
      if (rating.isEnabled(event.getDead())) {
        rating.updateElo(event.getDead(), event.getKiller());

        Objects.requireNonNull(KillDeathTracker.getTracker(event.getDead())).addDeath();
        Objects.requireNonNull(KillDeathTracker.getTracker(event.getKiller())).addKill();
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
