package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.config.Config;
import club.aurorapvp.auroracombat.config.Lang;
import club.aurorapvp.auroracombat.flags.CombatTagFlags;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
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
  private Timer t;
  private BukkitTask task;
  private Long timeStarted;

  public CombatTag(Player tagged, Player opponent) {
    this.playerOne = tagged;
    this.playerTwo = opponent;

    if (!canBeTagged(tagged) || !canBeTagged(opponent)) {
      return;
    }

    if (getTag(tagged, opponent) != null) {
      Objects.requireNonNull(getTag(tagged, opponent)).resetTimer();
    } else {
      tagged.sendMessage(Lang.formatComponent("tagged", opponent.getName(),
          (Config.get().getInt("combat-tag.duration") / 1000)));
      opponent.sendMessage(Lang.formatComponent("tagged", tagged.getName(),
          (Config.get().getInt("combat-tag.duration") / 1000)));

      playerOne.setGlowing(true);
      playerTwo.setGlowing(true);

      tags.add(this);

      resetTimer();
    }
  }

  public static void removeTags(Player p) {
    for (CombatTag tag : getTags(p)) {
      tag.removeTag();
    }
  }

  public static CombatTag[] getTags(Player p) {
    List<CombatTag> tagList = new ArrayList<>();

    for (CombatTag tag : tags) {
      if ((tag.getPlayerOne() == p || tag.getPlayerTwo() == p)) {
        tagList.add(tag);
      }
    }

    CombatTag[] combatTags = new CombatTag[tagList.size()];

    for (int i = 0; i < tagList.size(); i++) {
      combatTags[i] = tagList.get(i);
    }

    return combatTags;
  }

  public static CombatTag getRecentTag(Player p) {
    HashMap<Integer, CombatTag> tagTimes = new HashMap<>();
    List<Integer> times = new ArrayList<>();

    for (CombatTag tag : tags) {
      if ((tag.getPlayerOne() == p || tag.getPlayerTwo() == p)) {
        times.add(tag.getTimeRemaining());
        tagTimes.put(tag.getTimeRemaining(), tag);
      }
    }

    Collections.sort(times);

    if (times.size() > 0) {
      return tagTimes.get(times.get(0));
    }
    return null;
  }

  public static CombatTag getTag(Player p1, Player p2) {
    for (CombatTag tag : tags) {
      if ((tag.getPlayerOne() == p1 || tag.getPlayerOne() == p2) &&
          (tag.getPlayerTwo() == p1 || tag.getPlayerTwo() == p2)) {
        return tag;
      }
    }
    return null;
  }

  public static boolean isTagged(Player p) {
    for (CombatTag tag : tags) {
      if (tag.getPlayerOne() == p || tag.getPlayerTwo() == p) {
        return true;
      }
    }
    return false;
  }

  public static void setTaggable(Player p, boolean taggable) {
    taggablePlayers.put(p, taggable);
  }

  public static boolean canBeTagged(Player p) {
    if (AuroraCombat.isWorldGuardInstalled()) {
      RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
      RegionQuery query = container.createQuery();
      ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(p.getLocation()));

      if (set != null) {
        for (ProtectedRegion region : set.getRegions()) {
          return Objects.equals(region.getFlag(CombatTagFlags.TAGS_ENABLED), StateFlag.State.ALLOW);
        }
      }
    }

    return taggablePlayers.get(p);
  }

  public Player getPlayerOne() {
    return playerOne;
  }

  public Player getPlayerTwo() {
    return playerTwo;
  }

  public Player getOpponent(Player p) {
    if (p == playerOne) {
      return playerTwo;
    } else {
      return playerOne;
    }
  }

  public void resetTimer() {
    if (t != null) {
      t.cancel();
    }
    if (task != null) {
      task.cancel();
    }

    timeStarted = System.currentTimeMillis();
    CombatTag tag = this;

    t = new Timer();

    // TODO isn't this duplicate code of CombatTag#removeTag?
    t.schedule(new TimerTask() {
                 @Override
                 public void run() {
                   tags.remove(tag);
                   playerOne.sendMessage(Lang.formatComponent("tag-removed", playerTwo.getName()));
                   playerTwo.sendMessage(Lang.formatComponent("tag-removed", playerOne.getName()));
                   playerOne.sendActionBar(
                       Lang.formatComponent("tag-removed-action-bar", playerTwo.getName()));
                   playerTwo.sendActionBar(
                       Lang.formatComponent("tag-removed-action-bar", playerOne.getName()));

                   playerOne.hideBossBar(playerOneBar[0]);
                   playerTwo.hideBossBar(playerTwoBar[0]);

                   if (!CombatTag.isTagged(playerOne)) {
                     playerOne.setGlowing(false);
                   }

                   if (!CombatTag.isTagged(playerTwo)) {
                     playerTwo.setGlowing(false);
                   }

                   this.cancel();
                 }
               }, Config.get().getInt("combat-tag.duration")
    );

    task = new BukkitRunnable() {
      int seconds = Config.get().getInt("combat-tag.duration") / 1000;

      @Override
      public void run() {
        if ((seconds -= 1) == 0) {
          this.cancel();
        } else {
          playerOne.sendActionBar(
              Lang.formatComponent("tagged-action-bar", playerTwo.getName(), seconds));
          playerTwo.sendActionBar(
              Lang.formatComponent("tagged-action-bar", playerOne.getName(), seconds));

          if (playerOneBar[0] != null) {
            playerOne.hideBossBar(playerOneBar[0]);
          }

          playerOneBar[0] = BossBar.bossBar(
              Lang.formatComponent("opponent-bossbar", playerTwo.getName(),
                  (int) playerTwo.getHealth(),
                  (int) playerTwo.getLocation().distance(playerOne.getLocation())),
              1.0f,
              BossBar.Color.RED,
              BossBar.Overlay.PROGRESS
          );

          playerOne.showBossBar(playerOneBar[0]);

          if (playerTwoBar[0] != null) {
            playerTwo.hideBossBar(playerTwoBar[0]);
          }

          playerTwoBar[0] = BossBar.bossBar(
              Lang.formatComponent("opponent-bossbar", playerOne.getName(),
                  (int) playerOne.getHealth(),
                  (int) playerOne.getLocation().distance(playerTwo.getLocation())),
              1.0f,
              BossBar.Color.RED,
              BossBar.Overlay.PROGRESS
          );

          playerTwo.showBossBar(playerTwoBar[0]);
        }
      }
    }.runTaskTimer(AuroraCombat.INSTANCE, 0, 20);
  }

  public int getTimeRemaining() {
    return (int) (timeStarted + 15000 - System.currentTimeMillis());
  }

  public void removeTag() {
    t.cancel();
    task.cancel();

    tags.remove(this);

    playerOne.sendMessage(Lang.formatComponent("tag-removed", playerTwo.getName()));
    playerTwo.sendMessage(Lang.formatComponent("tag-removed", playerOne.getName()));
    playerOne.sendActionBar(Lang.formatComponent("tag-removed-action-bar", playerTwo.getName()));
    playerTwo.sendActionBar(Lang.formatComponent("tag-removed-action-bar", playerOne.getName()));

    playerOne.hideBossBar(playerOneBar[0]);
    playerTwo.hideBossBar(playerTwoBar[0]);

    if (!CombatTag.isTagged(playerOne)) {
      playerOne.setGlowing(false);
    }

    if (!CombatTag.isTagged(playerTwo)) {
      playerTwo.setGlowing(false);
    }
  }
}
