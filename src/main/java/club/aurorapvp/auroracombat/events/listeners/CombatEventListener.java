package club.aurorapvp.auroracombat.events.listeners;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.events.custom.PlayerDamagedByPlayerEvent;
import club.aurorapvp.auroracombat.events.custom.PlayerKilledByPlayerEvent;
import club.aurorapvp.auroracombat.modules.BlockFallDamage;
import club.aurorapvp.auroracombat.modules.CombatTag;
import club.aurorapvp.auroracombat.modules.KillDeathTracker;
import club.aurorapvp.auroracombat.modules.Rating;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatEventListener implements Listener {

  public static final Map<UUID, Event> lastDamage = new HashMap<>();
  private final Set<Player> combatLoggers = new HashSet<>();

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    if (CombatTag.isTagged(event.getPlayer())) {
      event.getPlayer().setHealth(0);

      combatLoggers.add(event.getPlayer());
    }
  }

  @EventHandler
      (priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onCommandRun(PlayerCommandPreprocessEvent event) {
    if (!CombatTag.isTagged(event.getPlayer())) {
      return;
    }

    boolean commandMatched = false;

    if (!AuroraCombat.getInstance().getConfig().getBoolean("combat-tag.commands.allow-commands")) {
      for (String command : AuroraCombat.getInstance().getConfig().getStringList("combat-tag.commands.whitelisted")) {
        if (event.getMessage().equals("/" + command)) {
          commandMatched = true;
          break;
        }
      }
    }

    if (commandMatched) {
      return;
    }

    if (event.getPlayer().hasPermission("auroracombat.bypass.commands")) {
      return;
    }

    event.getPlayer().sendMessage(AuroraCombat.getInstance().getLang().getComponent("commands-disabled"));

    event.setCancelled(true);
  }

  @EventHandler
      (priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDeath(PlayerDeathEvent event) {
    if (combatLoggers.contains(event.getPlayer())) {
      new PlayerKilledByPlayerEvent(
          Objects.requireNonNull(CombatTag.getRecentTag(event.getPlayer()))
              .getOpponent(event.getPlayer()), event).callEvent();
    }

    CombatTag.removeTags(event.getPlayer());
    BlockFallDamage.setInvulnerable(event.getPlayer());

    CombatTag.removeTags(event.getPlayer());
    BlockFallDamage.setInvulnerable(event.getPlayer());

    if (lastDamage.get(event.getPlayer().getUniqueId()) == null) {
      return;
    }

    if (!(lastDamage.get(
        event.getPlayer().getUniqueId()) instanceof PlayerDamagedByPlayerEvent damagedByPlayerEvent)) {
      return;
    }

    if (event.getPlayer().equals(damagedByPlayerEvent.getDamaged())
        && !damagedByPlayerEvent.isCancelled()) {
      new PlayerKilledByPlayerEvent(damagedByPlayerEvent, event).callEvent();
      lastDamage.remove(event.getPlayer().getUniqueId());
    }
  }

  @EventHandler
      (priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onKilledByPlayer(PlayerKilledByPlayerEvent event) {
    if (!CombatTag.isUntaggable(event.getDead()) && !CombatTag.isUntaggable(event.getKiller())) {
      Objects.requireNonNull(KillDeathTracker.getTracker(event.getDead())).addDeath();
      Objects.requireNonNull(KillDeathTracker.getTracker(event.getKiller())).addKill();

      event.getDead()
          .playSound(Sound.sound().type(org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL).build());
      event.getKiller()
          .playSound(Sound.sound().type(org.bukkit.Sound.ENTITY_ARROW_HIT_PLAYER).build());
    }

    for (Rating rating : Rating.getRatings()) {
      if (rating.isEnabled(event.getDead()) && rating.isEnabled(event.getKiller())) {
        rating.updateElo(event.getDead(), event.getKiller());
      }
    }
  }

  @EventHandler
      (priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDamagedByPlayer(PlayerDamagedByPlayerEvent event) {
    if (event.getAttacker().equals(event.getDamaged())) {
      return;
    }

    new CombatTag(event.getDamaged(), event.getAttacker());
    lastDamage.put(event.getDamaged().getUniqueId(), event);
  }
}
