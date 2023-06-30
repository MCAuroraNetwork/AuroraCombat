package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.config.Config;
import club.aurorapvp.auroracombat.config.Lang;
import club.aurorapvp.auroracombat.data.RatingDataHandler;
import club.aurorapvp.auroracombat.flags.RatingFlags;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class Rating {
  private static final Map<Rating, Boolean> RATINGS = new HashMap<>();
  private static final Map<String, Boolean> RATING_TYPES = new HashMap<>();
  private final Player p;
  private final String type;
  private final RatingDataHandler data;
  private int rating;

  public Rating(Player p, String type, boolean updating) {
    this.p = p;
    this.type = type;
    this.data = new RatingDataHandler(this);

    if (this.exists()) {
      this.rating = data.getRating();
    } else {
      rating = Config.get().getInt("elo.default-points");
    }

    RATINGS.put(this, updating);
  }

  public static void init() {
    setupRating("default", true);
  }

  public static void setupRating(String type, boolean updating) {
    RATING_TYPES.put(type, updating);
  }

  public static String[] getTypes() {
    return RATING_TYPES.keySet().toArray(new String[0]);
  }

  public static void saveAll() {
    for (Rating rating : RATINGS.keySet()) {
      rating.save();
    }
  }

  @SuppressWarnings("unused")
  public void setUpdating(Rating rating, boolean updating) {
    RATINGS.put(rating, updating);
  }

  public boolean isUpdating(Location loc) {
    if (!RATINGS.get(this)) {
      return false;
    }

    if (AuroraCombat.isWorldGuardInstalled()) {
      RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
      RegionQuery query = container.createQuery();
      ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(loc));

      if (set != null) {
        for (ProtectedRegion region : set.getRegions()) {
          return !Objects.equals(region.getFlag(RatingFlags.GLOBAL_RATINGS),
              StateFlag.State.DENY) ||
              Objects.equals(region.getFlag(RatingFlags.REGION_RATING), type);
        }
      }
    }

    return true;
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
        Config.get().getInt("elo.max-change") * -(0 + EloChange)));
  }

  @SuppressWarnings("unused")
  public static void changeRating(Player p, int otherRating, String type) {
    Rating playerRating = Rating.getRating(p, type);

    assert playerRating != null;
    double EloChange = Rating.getELOChange(playerRating.getPoints(), otherRating);

    playerRating.changePoints((int) Math.round(
        Config.get().getInt("elo.max-change") * EloChange));
  }

  public static Rating getRating(Player p, String type) {
    for (Rating rating : RATINGS.keySet()) {
      if (rating.getPlayer() == p && Objects.equals(rating.getType(), type)) {
        return rating;
      }
    }
    return null;
  }

  @SuppressWarnings("unused")
  public static Rating getRating(int index, String type) {
    List<Rating> filteredRatings = RATINGS.keySet().stream()
        .filter(r -> r.getType().equals(type))
        .sorted(Comparator.comparingInt(Rating::getPoints).reversed())
        .toList();

    if (index > 0 && index <= filteredRatings.size()) {
      return filteredRatings.get(index - 1);
    }

    return null;
  }

  public static Rating[] getRatings(Player p) {
    List<Rating> ratingsList = new ArrayList<>();
    for (Rating r : RATINGS.keySet()) {
      if (r.getPlayer().equals(p)) {
        ratingsList.add(r);
      }
    }

    return ratingsList.toArray(new Rating[0]);
  }

  @SuppressWarnings("unused")
  public static Rating[] getRatings() {
    return RATINGS.keySet().toArray(new Rating[0]);
  }

  public static void register(Player p) {
    PersistentDataContainer container = p.getPersistentDataContainer();

    for (String type : RATING_TYPES.keySet()) {
      NamespacedKey key = new NamespacedKey(AuroraCombat.INSTANCE, "rating_" + type);

      if (!container.has(key)) {
        container.set(key, PersistentDataType.INTEGER, Config.get().getInt("elo.default-points"));
      }

      new Rating(p, type, RATING_TYPES.get(type));
    }
  }

  public static void unregister(Player p) {
    for (Rating rating : Rating.getRatings(p)) {
      rating.save();
    }

    RATINGS.keySet().removeIf(rating -> rating.getPlayer().equals(p));
  }

  public static double getELOChange(int playerElo, int opponentElo) {
    return -(1 - 1.0 / (1 + Math.pow(10, (playerElo - opponentElo) / 400.0)));
  }

  public int getPoints() {
    return rating;
  }

  public void changePoints(int points) {
    rating = rating + points;

    if (points < 0) {
      p.sendMessage(Lang.formatComponent("points-decreased", points));
    } else {
      p.sendMessage(Lang.formatComponent("points-increased", points));
    }
  }

  public Player getPlayer() {
    return p;
  }

  public String getType() {
    return type;
  }

  public void save() {
    this.data.save();
  }

  public boolean exists() {
    return this.data.exists();
  }
}