package club.aurorapvp.auroracombat.data;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.modules.KillDeathTracker;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class KillDeathDataHandler {

  private final PersistentDataContainer container;
  private final NamespacedKey killKey = new NamespacedKey(AuroraCombat.INSTANCE, "kills");
  private final NamespacedKey deathKey = new NamespacedKey(AuroraCombat.INSTANCE, "deaths");
  private final NamespacedKey streakKey = new NamespacedKey(AuroraCombat.INSTANCE, "killstreak");
  private final KillDeathTracker tracker;

  public KillDeathDataHandler(KillDeathTracker tracker) {
    this.tracker = tracker;
    this.container = tracker.getPlayer().getPersistentDataContainer();
  }

  public int getKills() {
    return container.getOrDefault(killKey, PersistentDataType.INTEGER, -1);
  }

  public int getDeaths() {
    return container.getOrDefault(deathKey, PersistentDataType.INTEGER, -1);
  }

  public void save() {
    container.set(streakKey, PersistentDataType.INTEGER, tracker.getKillStreak());
    container.set(killKey, PersistentDataType.INTEGER, tracker.getKills());
    container.set(deathKey, PersistentDataType.INTEGER, tracker.getDeaths());
  }

  public boolean exists() {
    return container.get(deathKey, PersistentDataType.INTEGER) != null
        && container.get(killKey, PersistentDataType.INTEGER) != null;
  }
}
