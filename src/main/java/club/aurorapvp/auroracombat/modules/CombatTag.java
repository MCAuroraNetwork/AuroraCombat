package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.config.Config;
import club.aurorapvp.auroracombat.config.Lang;
import club.aurorapvp.auroracombat.flags.CombatTagFlags;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class CombatTag {

  private static final Set<CombatTag> tags = new HashSet<>();
  private static final Map<Player, Boolean> taggablePlayers = new HashMap<>();
  private final Player playerOne;
  private final Player playerTwo;
  private final BossBar[] playerOneBar = new BossBar[1];
  private final BossBar[] playerTwoBar = new BossBar[1];
  private Timer timer;
  private BukkitTask countdownTask;
  private Long timeStarted;
  private BukkitRunnable bossbarTask;

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
          Lang.formatComponent(
              "tagged", opponent.getName(), Config.get().getInt("combat-tag.duration")));
      opponent.sendMessage(
          Lang.formatComponent(
              "tagged", tagged.getName(), Config.get().getInt("combat-tag.duration")));

      tags.add(this);

      this.startTimer();
    }
  }

  public static void removeTags(Player player) {
    for (CombatTag tag : getTags(player)) {
      tag.removeTag();
    }
  }

  public static CombatTag[] getTags(Player player) {
    List<CombatTag> tagList = new ArrayList<>();

    for (CombatTag tag : tags) {
      if ((tag.getPlayerOne() == player || tag.getPlayerTwo() == player)) {
        tagList.add(tag);
      }
    }

    CombatTag[] combatTags = new CombatTag[tagList.size()];

    for (int i = 0; i < tagList.size(); i++) {
      combatTags[i] = tagList.get(i);
    }

    return combatTags;
  }

  public static CombatTag getRecentTag(Player player) {
    HashMap<Integer, CombatTag> tagTimes = new HashMap<>();
    List<Integer> times = new ArrayList<>();

    for (CombatTag tag : tags) {
      if ((tag.getPlayerOne() == player || tag.getPlayerTwo() == player)) {
        times.add(tag.getTimeRemaining());
        tagTimes.put(tag.getTimeRemaining(), tag);
      }
    }

    Collections.sort(times);

    if (!times.isEmpty()) {
      return tagTimes.get(times.get(0));
    }
    return null;
  }

  public static CombatTag getTag(Player p1, Player p2) {
    for (CombatTag tag : tags) {
      if ((tag.getPlayerOne() == p1 || tag.getPlayerOne() == p2)
          && (tag.getPlayerTwo() == p1 || tag.getPlayerTwo() == p2)) {
        return tag;
      }
    }

    return null;
  }

  public static boolean isTagged(Player player) {
    for (CombatTag tag : tags) {
      if (tag.getPlayerOne() == player || tag.getPlayerTwo() == player) {
        return true;
      }
    }
    return false;
  }

  public static void setTaggable(Player player, boolean taggable) {
    taggablePlayers.put(player, taggable);
  }

  public static boolean isUntaggable(Player player) {
    boolean taggable = taggablePlayers.get(player);

    if (taggable && AuroraCombat.isWorldGuardInstalled()) {
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
    timeStarted = System.currentTimeMillis();
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
        Config.get().getInt("combat-tag.duration") * 1000L);

    final int totalSeconds = Config.get().getInt("combat-tag.duration");
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
        }.runTaskTimer(AuroraCombat.INSTANCE, 0, (long) (delay * 20));

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
    };
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

  public int getTimeRemaining() {
    return (int) (timeStarted + 15000 - System.currentTimeMillis());
  }

  public void removeTag() {
    timer.cancel();

    if (!bossbarTask.isCancelled() && bossbarTask != null) {
      bossbarTask.cancel();
    }

    if (!countdownTask.isCancelled() && countdownTask != null) {
      countdownTask.cancel();
    }

    tags.remove(this);

    playerOne.sendMessage(Lang.formatComponent("tag-removed", playerTwo.getName()));
    playerTwo.sendMessage(Lang.formatComponent("tag-removed", playerOne.getName()));
    playerOne.sendActionBar(Lang.formatComponent("tag-removed-action-bar", playerTwo.getName()));
    playerTwo.sendActionBar(Lang.formatComponent("tag-removed-action-bar", playerOne.getName()));

    if (playerOneBar[0] != null && playerTwoBar[0] != null) {
      playerOne.hideBossBar(playerOneBar[0]);
      playerTwo.hideBossBar(playerTwoBar[0]);
    }
  }
}
