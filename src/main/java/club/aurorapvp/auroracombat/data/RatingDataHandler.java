package club.aurorapvp.auroracombat.data;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.modules.Rating;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class RatingDataHandler {
  private final PersistentDataContainer container;
  private final NamespacedKey key;
  private final Rating rating;

  public RatingDataHandler(Rating rating) {
    this.rating = rating;
    this.container = rating.getPlayer().getPersistentDataContainer();
    this.key = new NamespacedKey(AuroraCombat.INSTANCE, "rating_" + rating.getType());
  }

  public int getRating() {
    return container.getOrDefault(key, PersistentDataType.INTEGER, -1);
  }

  public void save() {
    container.set(key, PersistentDataType.INTEGER, rating.getPoints());
  }

  public boolean exists() {
    return container.get(key, PersistentDataType.INTEGER) != null;
  }
}
