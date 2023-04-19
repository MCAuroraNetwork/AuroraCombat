package club.aurorapvp.datahandlers;

import club.aurorapvp.AuroraCombat;
import club.aurorapvp.modules.Rating;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class RatingData {
  private final PersistentDataContainer container;
  private final NamespacedKey key;
  private final Rating rating;

  public RatingData(Rating rating) {
    this.rating = rating;
    this.container = rating.getPlayer().getPersistentDataContainer();
    this.key = new NamespacedKey(AuroraCombat.INSTANCE, "rating_" + rating.getType());
  }

  public int getRating() {
    return container.get(key, PersistentDataType.INTEGER);
  }

  public void save() {
    container.set(key, PersistentDataType.INTEGER, rating.getPoints());
  }

  public boolean exists() {
    return container.get(key, PersistentDataType.INTEGER) != null;
  }
}
