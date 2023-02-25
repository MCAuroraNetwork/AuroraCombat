package club.aurorapvp.modules;

import club.aurorapvp.AuroraCombat;
import club.aurorapvp.configs.Config;
import club.aurorapvp.configs.Lang;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class Rating {
  private static final Set<Rating> ratings = new HashSet<>();
  private final NamespacedKey key;
  private final Player p;
  private int rating;
  private final String type;
  private final PersistentDataContainer container;

  public Rating(Player p, String type) {
    this.p = p;
    this.container = p.getPersistentDataContainer();
    this.type = type;
    this.key = new NamespacedKey(AuroraCombat.INSTANCE, "rating_" + type);
    this.rating = container.get(key, PersistentDataType.INTEGER);

    ratings.add(this);
  }

  public int getPoints() {
    return rating;
  }

  public void changePoints(int points) {
    rating = rating + points;

    p.sendMessage(Lang.formatComponent("points-changed", points));

    container.set(key, PersistentDataType.INTEGER, rating);
  }

  public Player getPlayer() {
    return p;
  }
  public String getType() {
    return type;
  }

  public static void changeRating(Player deadPlayer, Player killer, String type) {
    Rating playerRating = Rating.getRating(deadPlayer, type);
    Rating killerRating = Rating.getRating(killer, type);

    assert playerRating != null;
    assert killerRating != null;
    double EloChange = Rating.getELOChange(playerRating.getPoints(), killerRating.getPoints());

    playerRating.changePoints((int) Math.round(
        Config.get().getInt("elo.max-change") * EloChange));
    killerRating.changePoints((int) Math.round(
        Config.get().getInt("elo.max-change") * EloChange));
  }

  public static Rating getRating(Player p, String type) {
    for (Rating rating : ratings) {
      if (rating.getPlayer() == p && Objects.equals(rating.getType(), type)) {
        return rating;
      }
    }
    return null;
  }

  public static void setupPlayer(Player p) {
    PersistentDataContainer container = p.getPersistentDataContainer();
    NamespacedKey key = new NamespacedKey(AuroraCombat.INSTANCE, "rating_default");

    if (!container.has(key)) {
      container.set(key, PersistentDataType.INTEGER, Config.get().getInt("elo.default-points"));
    }

    new Rating(p, "default");
  }

  public static double getELOChange(int playerElo, int opponentElo) {
    return -(1 - (1.0 / (1 + ((playerElo - opponentElo) ^ 10) / 400.0)));
  }
}