package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.events.custom.CombatTagEvent;
import club.aurorapvp.auroracombat.flags.CombatTagFlags;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.*;
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
      TAGS.get(playerOne.getUniqueId()).add(this);
      TAGS.get(playerTwo.getUniqueId()).add(this);

      this.startTimer();

      new CombatTagEvent(tagged, opponent).callEvent();
    }
  }

  public static void register(Player player) {
    TAGS.put(player.getUniqueId(), new LinkedList<>());
  }

  public static void unregister(Player player) {
    TAGS.remove(player.getUniqueId());
  }

  public static void removeTags(Player player) {
    if (CombatTag.getTags(player).isEmpty()) {
      return;
    }

    for (CombatTag tag : new LinkedList<>(CombatTag.getTags(player))) {
      tag.removeTag();
    }
  }

  public static LinkedList<CombatTag> getTags(Player player) {
    return TAGS.get(player.getUniqueId());
  }

  public static CombatTag getRecentTag(Player player) {
    return TAGS.get(player.getUniqueId()).getFirst();
  }

  public static CombatTag getTag(Player playerOne, Player playerTwo) {
    for (CombatTag tag : TAGS.get(playerOne.getUniqueId())) {
      if (tag.getOpponent(playerOne).equals(playerTwo)) {
        return tag;
      }
    }

    return null;
  }

  public static boolean isTagged(Player player) {
    return !TAGS.get(player.getUniqueId()).isEmpty();
  }

  public static void setTaggable(Player player, boolean taggable) {
    TAGGABLE_PLAYERS.put(player.getUniqueId(), taggable);
  }

  public static boolean isUntaggable(Player player) {
    boolean taggable = TAGGABLE_PLAYERS.get(player.getUniqueId());

    if (taggable && AuroraCombat.getInstance().isWorldGuardInstalled()) {
      RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
      RegionQuery query = container.createQuery();
      ApplicableRegionSet set =
          query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));

      if (set != null) {
        for (ProtectedRegion region : set.getRegions()) {
          return Objects.equals(region.getFlag(CombatTagFlags.TAGS_ENABLED), State.DENY);
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
    if (player.equals(playerOne)) {
      return playerTwo;
    } else {
      return playerOne;
    }
  }

  public void startTimer() {
    final CombatTag tag = this;

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
            int greenBars =
                (int) Math.round((double) (executionTimes - counter) / executionTimes * 30);
            int redBars = 30 - greenBars;

            Component green = Component.text("|".repeat(greenBars)).color(NamedTextColor.GREEN);

            Component red = Component.text("|".repeat(redBars)).color(NamedTextColor.RED);

            Component playerOneName =
                playerOne.name().decorate(TextDecoration.BOLD).color(NamedTextColor.RED);

            Component playerTwoName =
                playerTwo.name().decorate(TextDecoration.BOLD).color(NamedTextColor.RED);

            playerOne.sendActionBar(playerTwoName.appendSpace().append(green.append(red)));
            playerTwo.sendActionBar(playerOneName.appendSpace().append(green.append(red)));

            if ((counter += 1) == executionTimes) {
              this.cancel();
            }
          }
        }.runTaskTimer(AuroraCombat.getInstance(), 0, (long) (delay * 20));

    playerOneBar[0] = BossBar.bossBar(Component.text(), 0, Color.WHITE, Overlay.PROGRESS);

    playerTwoBar[0] = BossBar.bossBar(Component.text(), 0, Color.WHITE, Overlay.PROGRESS);

    bossbarTask =
        new BukkitRunnable() {
          @Override
          public void run() {
            updatebar(playerOneBar[0], playerTwo);

            updatebar(playerTwoBar[0], playerOne);
          }
        }.runTaskTimer(AuroraCombat.getInstance(), 0L, 1L);
  }

  private void updatebar(BossBar bar, Player player) {
    bar.name(
            player
                .displayName()
                .decorate(TextDecoration.BOLD)
                .color(NamedTextColor.YELLOW)
                .appendSpace()
                .append(
                    Component.text((int) (player.getHealth() + player.getAbsorptionAmount()) + "❤")
                        .color(NamedTextColor.RED))
                .appendSpace()
                .append(Component.text(player.getPing() + "ms").color(NamedTextColor.AQUA)))
        .progress((float) Math.min((player.getHealth() + player.getAbsorptionAmount()) / 20, 1.0f));
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

    playerOne.sendActionBar(
        AuroraCombat.getInstance().getLang().formatComponent("tag-removed", playerTwo.getName()));
    playerTwo.sendActionBar(
        AuroraCombat.getInstance().getLang().formatComponent("tag-removed", playerOne.getName()));

    if (playerOneBar[0] != null && playerTwoBar[0] != null) {
      playerOne.hideBossBar(playerOneBar[0]);
      playerTwo.hideBossBar(playerTwoBar[0]);
    }

    tags.get(this.getPlayerOne().getUniqueId()).remove(this);
    tags.get(this.getPlayerTwo().getUniqueId()).remove(this);
  }
}
