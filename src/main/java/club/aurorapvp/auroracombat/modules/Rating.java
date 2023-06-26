package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.configs.Config;
import club.aurorapvp.auroracombat.configs.Lang;
import club.aurorapvp.auroracombat.datahandlers.RatingData;
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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class Rating {
  private static final Set<Rating> ratings = new HashSet<>();
  private static final Set<String> types = new HashSet<>();
  private final Player p;
  private int rating;
  private final String type;
  private final RatingData data;

  public static void init() {
    setupRating("default");
  }

  public Rating(Player p, String type) {
    this.p = p;
    this.type = type;
    this.data = new RatingData(this);

    if (this.exists()) {
      this.rating = data.getRating();
    } else {
      rating = Config.get().getInt("elo.default-points");
    }

    ratings.add(this);
  }

  public int getPoints() {
    return rating;
  }

  public void changePoints(int points) {
    rating = rating + points;

    p.sendMessage(Lang.formatComponent("points-changed", points));
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

  public static void setupRating(String type) {
    types.add(type);
  }

  public static String[] getTypes() {
    return types.toArray(new String[0]);
  }

  public static void saveAll() {
    for (Rating rating : ratings) {
      rating.save();
    }
  }

  public static boolean isUpdating(Location loc, String type) {
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
    for (Rating rating : ratings) {
      if (rating.getPlayer() == p && Objects.equals(rating.getType(), type)) {
        return rating;
      }
    }
    return null;
  }

  @SuppressWarnings("unused")
  public static Rating getRating(int index, String type) {
    List<Rating> filteredRatings = ratings.stream()
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
    for (Rating r : ratings) {
      if (r.getPlayer().equals(p)) {
        ratingsList.add(r);
      }
    }

    return ratingsList.toArray(new Rating[0]);
  }

  @SuppressWarnings("unused")
  public static Rating[] getRatings() {
    return ratings.toArray(new Rating[0]);
  }

  public static void register(Player p) {
    PersistentDataContainer container = p.getPersistentDataContainer();

    for (String type : types) {
      NamespacedKey key = new NamespacedKey(AuroraCombat.INSTANCE, "rating_" + type);

      if (!container.has(key)) {
        container.set(key, PersistentDataType.INTEGER, Config.get().getInt("elo.default-points"));
      }

      new Rating(p, type);
    }
  }

  public static void unregister(Player p) {
    for (Rating rating : Rating.getRatings(p)) {
      rating.save();
    }

    ratings.removeIf(rating -> rating.getPlayer().equals(p));
  }

  public static double getELOChange(int playerElo, int opponentElo) {
    return -(1 - 1.0 / (1 + Math.pow(10, (playerElo - opponentElo) / 400.0)));
  }
}