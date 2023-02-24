package club.aurorapvp.modules;

import club.aurorapvp.AuroraCombat;
import club.aurorapvp.configs.Config;
import club.aurorapvp.configs.Lang;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class Rating {
  private static final NamespacedKey KEY = new NamespacedKey(AuroraCombat.INSTANCE, "rating");
  private static final List<Rating> ratings = new ArrayList<>();
  private final Player p;
  private int rating;
  private final PersistentDataContainer container;

  public Rating(Player p) {
    this.p = p;
    this.container = p.getPersistentDataContainer();
    this.rating = container.get(KEY, PersistentDataType.INTEGER);

    ratings.add(this);
  }

  public int getPoints() {
    return rating;
  }

  public void changePoints(int points) {
    rating = rating + points;

    p.sendMessage(Lang.formatComponent("points-changed", points));

    container.set(KEY, PersistentDataType.INTEGER, rating);
  }

  public Player getPlayer() {
    return p;
  }

  public static void changeRating(Player deadPlayer, Player killer) {
    Rating playerRating = Rating.getRating(deadPlayer);
    Rating killerRating = Rating.getRating(killer);

    assert playerRating != null;
    assert killerRating != null;
    double EloChange = Rating.getELOChange(playerRating.getPoints(), killerRating.getPoints());

    playerRating.changePoints((int) Math.round(
        Config.get().getInt("elo.max-change") * EloChange));
    killerRating.changePoints((int) Math.round(
        Config.get().getInt("elo.max-change") * EloChange));
  }

  public static Rating getRating(Player p) {
    for (Rating rating : ratings) {
      if (rating.getPlayer() == p) {
        return rating;
      }
    }
    return null;
  }

  public static void setupPlayer(Player p) {
    PersistentDataContainer container = p.getPersistentDataContainer();

    if (!container.has(KEY)) {
      container.set(KEY, PersistentDataType.INTEGER, Config.get().getInt("elo.default-points"));
    }

    new Rating(p);
  }

  public static double getELOChange(int playerElo, int opponentElo) {
    return -( 1 - (1.0 / (1 + ((playerElo - opponentElo) ^ 10) / 400.0)));
  }
}