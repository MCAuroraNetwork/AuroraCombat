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
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatEventListener implements Listener {

  public static final Map<UUID, Event> lastDamage = new HashMap<>();
  private final Set<UUID> combatLoggers = new HashSet<>();

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    if (!CombatTag.isTagged(event.getPlayer())) {
      return;
    }

    combatLoggers.add(event.getPlayer().getUniqueId());

    event.getPlayer().setHealth(0);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onUseElytra(EntityToggleGlideEvent event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    if (!event.isGliding()) {
      return;
    }

    if (AuroraCombat.getInstance().getConfig().getBoolean("combat-tag.allow-elytras")) {
      return;
    }

    if (!CombatTag.isTagged(player)) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onCommandRun(PlayerCommandPreprocessEvent event) {
    if (!CombatTag.isTagged(event.getPlayer())) {
      return;
    }

    boolean commandMatched = false;

    if (AuroraCombat.getInstance().getConfig().getBoolean("combat-tag.commands.allow-commands")) {
      return;
    }

    for (String command :
        AuroraCombat.getInstance().getConfig().getStringList("combat-tag.commands.whitelisted")) {
      if (event.getMessage().equals("/" + command)) {
        commandMatched = true;
        break;
      }
    }

    if (commandMatched) {
      return;
    }

    if (event.getPlayer().hasPermission("auroracombat.bypass.commands")) {
      return;
    }

    event
        .getPlayer()
        .sendMessage(AuroraCombat.getInstance().getLang().getComponent("commands-disabled"));

    event.setCancelled(true);
  }

  // TODO fix NPE here
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDeath(PlayerDeathEvent event) {
    if (combatLoggers.remove(event.getPlayer().getUniqueId())) {
      Player killer =
          Objects.requireNonNull(CombatTag.getRecentTag(event.getPlayer()))
              .getOpponent(event.getPlayer());

      CombatTag.removeTags(event.getPlayer());
      BlockFallDamage.setInvulnerable(event.getPlayer());

      new PlayerKilledByPlayerEvent(killer, event).callEvent();
      return;
    }

    CombatTag.removeTags(event.getPlayer());
    BlockFallDamage.setInvulnerable(event.getPlayer());

    if (lastDamage.get(event.getPlayer().getUniqueId()) == null) {
      return;
    }

    if (!(lastDamage.get(event.getPlayer().getUniqueId())
        instanceof PlayerDamagedByPlayerEvent damagedByPlayerEvent)) {
      return;
    }

    if (event.getPlayer().equals(damagedByPlayerEvent.getDamaged())
        && !damagedByPlayerEvent.isCancelled()) {
      new PlayerKilledByPlayerEvent(damagedByPlayerEvent, event).callEvent();
      lastDamage.remove(event.getPlayer().getUniqueId());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onKilledByPlayer(PlayerKilledByPlayerEvent event) {
    Player dead = event.getDead();
    Player killer = event.getKiller();

    Objects.requireNonNull(KillDeathTracker.getTracker(dead)).addDeath();
    Objects.requireNonNull(KillDeathTracker.getTracker(killer)).addKill();

    Map<Rating, Integer> updatedRatings = new HashMap<>();

    for (Rating rating : Rating.getRatings()) {
      if (rating.isEnabled(dead) && rating.isEnabled(killer)) {
        updatedRatings.put(rating, rating.updateElo(dead, killer));
      }
    }

    StringBuilder killerMessage = new StringBuilder();

    for (Rating rating : updatedRatings.keySet()) {
      killerMessage
          .append("\n")
          .append(rating.getFriendlyName())
          .append(": +")
          .append(updatedRatings.get(rating));
    }

    killerMessage
        .append("\nKillstreak: ")
        .append(KillDeathTracker.getTracker(killer).getKillStreak());

    StringBuilder deadMessage = new StringBuilder();

    for (Rating rating : updatedRatings.keySet()) {
      deadMessage
          .append("\n")
          .append(rating.getFriendlyName())
          .append(": -")
          .append(updatedRatings.get(rating));
    }

    dead.sendMessage(
        AuroraCombat.getInstance()
            .getLang()
            .formatComponent("you-were-killed-by", killer.getName(), deadMessage.toString()));

    killer.sendMessage(
        AuroraCombat.getInstance()
            .getLang()
            .formatComponent("you-killed", dead.getName(), killerMessage.toString()));

    event
        .getDead()
        .playSound(Sound.sound().type(org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL).build());
    event
        .getKiller()
        .playSound(Sound.sound().type(org.bukkit.Sound.ENTITY_ARROW_HIT_PLAYER).build());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDamagedByPlayer(PlayerDamagedByPlayerEvent event) {
    if (event.getAttacker().equals(event.getDamaged())) {
      return;
    }

    if (AuroraCombat.getInstance().getConfig().getBoolean("combat-tag.enable")) {
      new CombatTag(event.getDamaged(), event.getAttacker());
    }

    lastDamage.put(event.getDamaged().getUniqueId(), event);
  }
}
