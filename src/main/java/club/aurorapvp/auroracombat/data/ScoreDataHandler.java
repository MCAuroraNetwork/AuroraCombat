package club.aurorapvp.auroracombat.data;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.modules.Score;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ScoreDataHandler {
  private final PersistentDataContainer container;
  private final NamespacedKey key;
  private final Score score;

  public ScoreDataHandler(Score score) {
    this.score = score;
    this.container = score.getPlayer().getPersistentDataContainer();
    this.key = new NamespacedKey(AuroraCombat.INSTANCE, "rating_" + score.getRating().getName());
  }

  public int getPoints() {
    return container.getOrDefault(key, PersistentDataType.INTEGER, -1);
  }

  public void save() {
    container.set(key, PersistentDataType.INTEGER, score.getPoints());
  }

  public boolean exists() {
    return container.get(key, PersistentDataType.INTEGER) != null;
  }
}
