package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.flags.CombatTagFlags;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class CombatTag {

  private static final Map<UUID, LinkedList<CombatTag>> tags = new HashMap<>();
  private static final Map<UUID, Boolean> taggablePlayers = new HashMap<>();
  private final Player playerOne;
  private final Player playerTwo;
  private final BossBar[] playerOneBar = new BossBar[1];
  private final BossBar[] playerTwoBar = new BossBar[1];
  private Timer timer;
  private BukkitTask countdownTask;
  private BukkitTask bossbarTask;

  public CombatTag(Player tagged, Player opponent) {
    this.playerOne = tagged;
    this.playerTwo = opponent;

    if (CombatTag.isUntaggable(tagged) || CombatTag.isUntaggable(opponent)) {
      return;
    }

    if (CombatTag.getTag(tagged, opponent) != null) {
      Objects.requireNonNull(getTag(tagged, opponent)).resetTimer();
    } else {
      tagged.sendMessage(
          AuroraCombat.getInstance().getLang().formatComponent(
              "tagged", opponent.getName(),
              AuroraCombat.getInstance().getConfig().getInt("combat-tag.duration")));
      opponent.sendMessage(
          AuroraCombat.getInstance().getLang().formatComponent(
              "tagged", tagged.getName(),
              AuroraCombat.getInstance().getConfig().getInt("combat-tag.duration")));

      if (!tags.containsKey(tagged.getUniqueId())) {
        tags.put(tagged.getUniqueId(), new LinkedList<>());
      }

      if (!tags.containsKey(opponent.getUniqueId())) {
        tags.put(opponent.getUniqueId(), new LinkedList<>());
      }

      tags.get(tagged.getUniqueId()).add(this);
      tags.get(opponent.getUniqueId()).add(this);

      this.startTimer();
    }
  }

  public static void removeTags(Player player) {
    for (CombatTag tag : CombatTag.getTags(player)) {
      tag.removeTag();
    }
  }

  public static LinkedList<CombatTag> getTags(Player player) {
    return tags.getOrDefault(player.getUniqueId(), new LinkedList<>());
  }

  public static CombatTag getRecentTag(Player player) {
    return tags.get(player.getUniqueId()).getFirst();
  }

  public static CombatTag getTag(Player p1, Player p2) {
    if (!tags.containsKey(p1.getUniqueId()) || !tags.containsKey(p2.getUniqueId())) {
      return null;
    }

    for (CombatTag tag : tags.get(p1.getUniqueId())) {
      if (tag.getPlayerOne() == p2 || tag.getPlayerTwo() == p2) {
        return tag;
      }
    }

    return null;
  }

  public static boolean isTagged(Player player) {
    return tags.containsKey(player.getUniqueId()) && !tags.get(player.getUniqueId()).isEmpty();
  }

  public static void setTaggable(Player player, boolean taggable) {
    taggablePlayers.put(player.getUniqueId(), taggable);
  }

  public static boolean isUntaggable(Player player) {
    boolean taggable = taggablePlayers.get(player.getUniqueId());

    if (taggable && AuroraCombat.getInstance().isWorldGuardInstalled()) {
      RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
      RegionQuery query = container.createQuery();
      ApplicableRegionSet set =
          query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));

      if (set != null) {
        for (ProtectedRegion region : set.getRegions()) {
          return Objects.equals(region.getFlag(CombatTagFlags.TAGS_ENABLED),
              State.DENY);
        }
      }
    }

    return true;
  }

  public Player getPlayerOne() {
    return playerOne;
  }

  public Player getPlayerTwo() {
    return playerTwo;
  }

  public Player getOpponent(Player player) {
    if (player == playerOne) {
      return playerTwo;
    } else {
      return playerOne;
    }
  }

  public void startTimer() {
    CombatTag tag = this;

    timer = new Timer();

    timer.schedule(
        new TimerTask() {
          @Override
          public void run() {
            tag.removeTag();
            this.cancel();
          }
        },
        AuroraCombat.getInstance().getConfig().getInt("combat-tag.duration") * 1000L);

    final int totalSeconds = AuroraCombat.getInstance().getConfig().getInt("combat-tag.duration");
    final int executionTimes = 30;
    final double delay = (double) totalSeconds / executionTimes;

    if (countdownTask != null) {
      countdownTask.cancel();
    }

    countdownTask =
        new BukkitRunnable() {
          int counter = 0;

          @Override
          public void run() {
            int greenBars = (int) Math.round(
                (double) (executionTimes - counter) / executionTimes * 30);
            int redBars = 30 - greenBars;

            Component green = Component.text("|".repeat(greenBars))
                .color(NamedTextColor.GREEN);

            Component red = Component.text("|".repeat(redBars))
                .color(NamedTextColor.RED);

            Component playerOneName = playerOne.name().decorate(TextDecoration.BOLD)
                .color(NamedTextColor.RED);

            Component playerTwoName = playerTwo.name().decorate(TextDecoration.BOLD)
                .color(NamedTextColor.RED);

            playerOne.sendActionBar(playerTwoName.appendSpace().append(green.append(red)));
            playerTwo.sendActionBar(playerOneName.appendSpace().append(green.append(red)));

            if ((counter += 1) == executionTimes) {
              this.cancel();
            }
          }
        }.runTaskTimer(AuroraCombat.getInstance(), 0, (long) (delay * 20));

    if (playerOneBar[0] == null) {
      playerOneBar[0] =
          BossBar.bossBar(
              playerTwo.displayName().decorate(TextDecoration.BOLD).color(NamedTextColor.YELLOW)
                  .appendSpace().append(
                      Component.text(
                              (int) (playerTwo.getHealth() + playerTwo.getAbsorptionAmount()) + "❤")
                          .color(NamedTextColor.RED))
                  .appendSpace().append(
                      Component.text(playerTwo.getPing() + "ms").color(NamedTextColor.AQUA)),
              1.0f, Color.RED, Overlay.PROGRESS);

      playerOne.showBossBar(playerOneBar[0]);
    }

    if (playerTwoBar[0] == null) {
      playerTwoBar[0] =
          BossBar.bossBar(
              playerOne.displayName().decorate(TextDecoration.BOLD).color(NamedTextColor.YELLOW)
                  .appendSpace().append(
                      Component.text(
                              (int) (playerOne.getHealth() + playerOne.getAbsorptionAmount()) + "❤")
                          .color(NamedTextColor.RED))
                  .appendSpace().append(
                      Component.text(playerOne.getPing() + "ms").color(NamedTextColor.AQUA)),
              1.0f, Color.RED, Overlay.PROGRESS);

      playerTwo.showBossBar(playerTwoBar[0]);
    }

    bossbarTask = new BukkitRunnable() {
      @Override
      public void run() {
        playerOneBar[0].name(
                playerTwo.displayName().decorate(TextDecoration.BOLD).color(NamedTextColor.YELLOW)
                    .appendSpace().append(
                        Component.text(
                                (int) (playerTwo.getHealth() + playerTwo.getAbsorptionAmount()) + "❤")
                            .color(NamedTextColor.RED))
                    .appendSpace().append(
                        Component.text(playerTwo.getPing() + "ms").color(NamedTextColor.AQUA)))
            .progress(
                (float) Math.min((playerTwo.getHealth() + playerTwo.getAbsorptionAmount()) / 20,
                    1.0f));

        playerTwoBar[0].name(
            playerOne.displayName().decorate(TextDecoration.BOLD).color(NamedTextColor.YELLOW)
                .appendSpace().append(
                    Component.text(
                            (int) (playerOne.getHealth() + playerOne.getAbsorptionAmount()) + "❤")
                        .color(NamedTextColor.RED))
                .appendSpace().append(
                    Component.text(playerOne.getPing() + "ms").color(NamedTextColor.AQUA))).progress(
            (float) Math.min((playerOne.getHealth() + playerOne.getAbsorptionAmount()) / 20,
                1.0f));
      }
    }.runTaskTimer(AuroraCombat.getInstance(), 1L, 1L);
  }

  public void resetTimer() {
    timer.cancel();

    if (!bossbarTask.isCancelled() && bossbarTask != null) {
      bossbarTask.cancel();
    }

    if (!countdownTask.isCancelled() && countdownTask != null) {
      countdownTask.cancel();
    }

    this.startTimer();
  }

  public void removeTag() {
    timer.cancel();

    if (!bossbarTask.isCancelled() && bossbarTask != null) {
      bossbarTask.cancel();
    }

    if (!countdownTask.isCancelled() && countdownTask != null) {
      countdownTask.cancel();
    }

    tags.remove(this.getPlayerOne().getUniqueId()).remove(this);
    tags.remove(this.getPlayerTwo().getUniqueId()).remove(this);

    playerOne.sendMessage(
        AuroraCombat.getInstance().getLang().formatComponent("tag-removed", playerTwo.getName()));
    playerTwo.sendMessage(
        AuroraCombat.getInstance().getLang().formatComponent("tag-removed", playerOne.getName()));
    playerOne.sendActionBar(AuroraCombat.getInstance().getLang()
        .formatComponent("tag-removed-action-bar", playerTwo.getName()));
    playerTwo.sendActionBar(AuroraCombat.getInstance().getLang()
        .formatComponent("tag-removed-action-bar", playerOne.getName()));

    if (playerOneBar[0] != null && playerTwoBar[0] != null) {
      playerOne.hideBossBar(playerOneBar[0]);
      playerTwo.hideBossBar(playerTwoBar[0]);
    }
  }
}
