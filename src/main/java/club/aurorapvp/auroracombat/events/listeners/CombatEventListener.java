package club.aurorapvp.auroracombat.events.listeners;

import club.aurorapvp.auroracombat.config.Config;
import club.aurorapvp.auroracombat.config.Lang;
import club.aurorapvp.auroracombat.events.custom.PlayerDamagedByPlayerEvent;
import club.aurorapvp.auroracombat.events.custom.PlayerKilledByPlayerEvent;
import club.aurorapvp.auroracombat.modules.BlockFallDamage;
import club.aurorapvp.auroracombat.modules.CombatTag;
import club.aurorapvp.auroracombat.modules.KillDeathTracker;
import club.aurorapvp.auroracombat.modules.Rating;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatEventListener implements Listener {

  private PlayerDamagedByPlayerEvent lastDamage;
  private final Set<Player> combatLoggers = new HashSet<>();

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    if (CombatTag.isTagged(event.getPlayer())) {
      event.getPlayer().setHealth(0);

      combatLoggers.add(event.getPlayer());
    }
  }

  @EventHandler
  public void onCommandRun(PlayerCommandPreprocessEvent event) {
    if (!CombatTag.isTagged(event.getPlayer())) {
      return;
    }

    if (Config.get().getBoolean("combat-tag.allow-commands")) {
      return;
    }

    if (event.getPlayer().hasPermission("auroracombat.bypass.commands")) {
      return;
    }

    event.getPlayer().sendMessage(Lang.getComponent("commands-disabled"));

    event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    if (combatLoggers.contains(event.getPlayer())) {
      new PlayerKilledByPlayerEvent(
          Objects.requireNonNull(CombatTag.getRecentTag(event.getPlayer()))
              .getOpponent(event.getPlayer()), event).callEvent();
    }

    CombatTag.removeTags(event.getPlayer());
    BlockFallDamage.setInVulnerable(event.getPlayer());

    CombatTag.removeTags(event.getPlayer());
    BlockFallDamage.setInVulnerable(event.getPlayer());

    if (lastDamage == null) {
      return;
    }

    if (event.getPlayer().equals(lastDamage.getDamaged())) {
      new PlayerKilledByPlayerEvent(lastDamage, event).callEvent();
      lastDamage = null;
    }
  }

  @EventHandler
  public void onKilledByPlayer(PlayerKilledByPlayerEvent event) {
    if (!CombatTag.isUntaggable(event.getDead()) && !CombatTag.isUntaggable(event.getKiller())) {
      Objects.requireNonNull(KillDeathTracker.getTracker(event.getDead())).addDeath();
      Objects.requireNonNull(KillDeathTracker.getTracker(event.getKiller())).addKill();
    }

    for (Rating rating : Rating.getRatings()) {
      if (rating.isEnabled(event.getDead())) {
        rating.updateElo(event.getDead(), event.getKiller());
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
