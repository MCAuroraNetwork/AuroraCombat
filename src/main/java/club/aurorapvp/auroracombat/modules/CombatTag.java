package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.events.custom.CombatTagEvent;
import club.aurorapvp.auroracombat.events.custom.CombatTagRemovedEvent;
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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class CombatTag {

  private static final Map<UUID, LinkedList<CombatTag>> TAGS = new HashMap<>();
  private static final Map<UUID, Boolean> TAGGABLE_PLAYERS = new HashMap<>();
  private final Player playerOne;
  private final Player playerTwo;
  private final BossBar[] playerOneBossBar = new BossBar[1];
  private final BossBar[] playerTwoBossBar = new BossBar[1];
  private Component playerOneActionBar;
  private Component playerTwoActionBar;
  private int timeRemaining =
      AuroraCombat.getInstance().getConfig().getInt("combat-tag.duration") * 1000;
  private Timer decrementTimer;
  private Timer actionBarTimer;
  private BukkitTask bossbarTask;

  static {
    new BukkitRunnable() {

      @Override
      public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
          if (!CombatTag.isTagged(player)) {
            continue;
          }

          player.sendActionBar(CombatTag.getOldestTag(player).getActionBar(player));
        }
      }
    }.runTaskTimer(AuroraCombat.getInstance(), 1L, 1L);
  }

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

  public static CombatTag getOldestTag(Player player) {
    return TAGS.get(player.getUniqueId()).getLast();
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

  public Component getActionBar(Player player) {
    if (player.equals(playerOne)) {
      return playerOneActionBar;
    } else {
      return playerTwoActionBar;
    }
  }

  public int getTimeRemaining() {
    return timeRemaining;
  }

  public void startTimer() {
    final CombatTag tag = this;

    final int totalTime = timeRemaining;

    decrementTimer = new Timer();
    actionBarTimer  = new Timer();

    decrementTimer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        if (timeRemaining > 0) {
          timeRemaining--;
        } else {
          tag.removeTag();
        }
      }
    }, 0, 1L);

    long interval = totalTime / 30;

    actionBarTimer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
          int greenBars = (int) Math.round((double) timeRemaining / totalTime * 30);
          int redBars   = 30 - greenBars;

          Component green = Component.text("|".repeat(greenBars))
                  .color(NamedTextColor.GREEN);
          Component red   = Component.text("|".repeat(redBars))
                  .color(NamedTextColor.RED);

          Component p1Name = playerOne.name()
                  .decorate(TextDecoration.BOLD)
                  .color(NamedTextColor.RED);
          Component p2Name = playerTwo.name()
                  .decorate(TextDecoration.BOLD)
                  .color(NamedTextColor.RED);

          playerOneActionBar = p2Name.appendSpace().append(green).append(red);
          playerTwoActionBar = p1Name.appendSpace().append(green).append(red);
      }
    }, 0, interval);

    playerOneBossBar[0] = BossBar.bossBar(Component.text(), 0, Color.RED, Overlay.PROGRESS);
    playerTwoBossBar[0] = BossBar.bossBar(Component.text(), 0, Color.RED, Overlay.PROGRESS);

    playerOne.showBossBar(playerOneBossBar[0]);
    playerTwo.showBossBar(playerTwoBossBar[0]);

    bossbarTask =
        new BukkitRunnable() {
          @Override
          public void run() {
            updatebar(playerOneBossBar[0], playerTwo);

            updatebar(playerTwoBossBar[0], playerOne);
          }
        }.runTaskTimer(AuroraCombat.getInstance(), 0L, 1L);
  }

  private void updatebar(BossBar bar, Player opponent) {
    bar.name(
            opponent
                .displayName()
                .decorate(TextDecoration.BOLD)
                .color(NamedTextColor.YELLOW)
                .appendSpace()
                .append(
                    Component.text((int) (opponent.getHealth() + opponent.getAbsorptionAmount()) + "❤")
                        .color(NamedTextColor.RED))
                .appendSpace()
                .append(Component.text(opponent.getPing() + "ms").color(NamedTextColor.AQUA)))
        .progress((float) Math.min((opponent.getHealth() + opponent.getAbsorptionAmount()) / 20, 1.0f));
  }

  public void resetTimer() {
    timeRemaining = AuroraCombat.getInstance().getConfig().getInt("combat-tag.duration") * 1000;
  }

  public void removeTag() {
    new BukkitRunnable() {
      @Override
      public void run() {
        new CombatTagRemovedEvent(playerOne, playerTwo).callEvent();
      }
    }.runTask(AuroraCombat.getInstance());

    if (!bossbarTask.isCancelled() && bossbarTask != null) {
      bossbarTask.cancel();
    }

    decrementTimer.cancel();
    actionBarTimer.cancel();

    if (playerOneBossBar[0] != null && playerTwoBossBar[0] != null) {
      playerOne.hideBossBar(playerOneBossBar[0]);
      playerTwo.hideBossBar(playerTwoBossBar[0]);
    }


    playerOne.sendActionBar(
            AuroraCombat.getInstance().getLang().formatComponent("tag-removed", playerTwo.getName()));
    playerTwo.sendActionBar(
            AuroraCombat.getInstance().getLang().formatComponent("tag-removed", playerOne.getName()));

    if (playerOneBossBar[0] != null && playerTwoBossBar[0] != null) {
      playerOne.hideBossBar(playerOneBossBar[0]);
      playerTwo.hideBossBar(playerTwoBossBar[0]);
    }

    TAGS.get(this.getPlayerOne().getUniqueId()).remove(this);
    TAGS.get(this.getPlayerTwo().getUniqueId()).remove(this);
  }
}
