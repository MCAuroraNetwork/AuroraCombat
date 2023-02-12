package club.aurorapvp.modules;

import club.aurorapvp.configs.Config;
import club.aurorapvp.configs.Lang;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.entity.Player;

public class CombatTag {
  private static final List<CombatTag> tags = new ArrayList<>();
  private final Player tagged;
  private final Player opponent;
  private Timer t = new Timer();
  private Long timeStarted;

  public CombatTag(Player tagged, Player opponent) {
    this.tagged = tagged;
    this.opponent = opponent;

    if (getTag(tagged, opponent) != null) {
      getTag(tagged, opponent).resetTimer();
    } else {
      tagged.sendMessage(Lang.formatComponent("tagged", opponent.getName(), (Config.get().getInt("combat-tag.duration") / 1000)));
      opponent.sendMessage(Lang.formatComponent("tagged", tagged.getName(), (Config.get().getInt("combat-tag.duration") / 1000)));

      CombatTag tag = this;
      tags.add(this);
      timeStarted = System.currentTimeMillis();

      t.schedule(new TimerTask() {
                             @Override
                             public void run() {
                               tags.remove(tag);
                               tagged.sendMessage(Lang.formatComponent("tag-removed", opponent.getName()));
                               opponent.sendMessage(Lang.formatComponent("tag-removed", tagged.getName()));
                               this.cancel();
                             }
                           }, Config.get().getInt("combat-tag.duration")
      );
    }
  }

  public Player getTagged() {
    return tagged;
  }

  public Player getOpponent() {
    return opponent;
  }

  public void resetTimer() {
    t.cancel();
    t = new Timer();
    timeStarted = System.currentTimeMillis();

    CombatTag tag = this;
    t.schedule(new TimerTask() {
                 @Override
                 public void run() {
                   tags.remove(tag);
                   tagged.sendMessage(Lang.formatComponent("tag-removed", opponent.getName()));
                   opponent.sendMessage(Lang.formatComponent("tag-removed", tagged.getName()));
                   this.cancel();
                 }
               }, Config.get().getInt("combat-tag.duration")
    );
  }

  public int timeRemaining() {
    return (int) (timeStarted + 15000 - System.currentTimeMillis());
  }

  public void removeTag() {
    t.cancel();

    tags.remove(this);
    tagged.sendMessage(Lang.formatComponent("tag-removed", opponent.getName()));
    opponent.sendMessage(Lang.formatComponent("tag-removed", tagged.getName()));
  }

  public static void removeTags(Player p) {
    for (CombatTag tag : getTags(p)) {
      tag.removeTag();
    }
  }

  public static CombatTag[] getTags(Player p) {
    List<CombatTag> tagList = new ArrayList<>();

    for (CombatTag tag : tags) {
      if ((tag.getTagged() == p || tag.getOpponent() == p)) {
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
      if ((tag.getTagged() == p || tag.getOpponent() == p)) {
        times.add(tag.timeRemaining());
        tagTimes.put(tag.timeRemaining(), tag);
      }
    }

    Collections.sort(times);

    return tagTimes.get(times.get(0));
  }

  public static CombatTag getTag(Player p1, Player p2) {
    for (CombatTag tag : tags) {
      if ((tag.getTagged() == p1 || tag.getTagged() == p2) &&
          (tag.getOpponent() == p1 || tag.getOpponent() == p2)) {
        return tag;
      }
    }
    return null;
  }

  public static boolean isTagged(Player p) {
    for (CombatTag tag : tags) {
      if (tag.getTagged() == p || tag.getOpponent() == p) {
        return true;
      }
    }
    return false;
  }
}
