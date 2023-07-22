package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.config.Config;
import club.aurorapvp.auroracombat.flags.RatingFlags;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Rating {
  private static final Set<Rating> RATINGS = new HashSet<>();
  private final String name;
  private final RatingType type;
  private final Set<Score> SCORES = new HashSet<>();
  private boolean enabled;

  public Rating(String name, RatingType type, boolean enabled) {
    this.name = name;
    this.type = type;
    this.enabled = enabled;

    RATINGS.add(this);
  }

  public static void init() {
    new Rating("default", RatingType.GLOBAL, true);
  }

  public String getName() {
    return name;
  }

  public RatingType getType() {
    return type;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public Set<Score> getScores() {
    return SCORES;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public static void saveAll() {
    for (Rating rating : RATINGS) {
      for (Score score : rating.getScores()) {
        score.save();
      }
    }
  }

  public boolean isEnabled(Location loc) {
    if (!this.isEnabled()) {
      return false;
    }

    if (!AuroraCombat.isWorldGuardInstalled()) {
      return true;
    }

    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    RegionQuery query = container.createQuery();
    ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(loc));

    for (ProtectedRegion region : set.getRegions()) {
      return (Objects.equals(region.getFlag(RatingFlags.GLOBAL_RATINGS), StateFlag.State.ALLOW)
              && this.getType() == RatingType.GLOBAL)
          || (Objects.equals(region.getFlag(RatingFlags.REGION_RATING), name)
              && this.getType() == RatingType.REGION);
    }

    return true;
  }

  public Score getScore(Player p) {
    for (Score score : SCORES) {
      if (score.getPlayer() == p) {
        return score;
      }
    }

    return null;
  }

  @SuppressWarnings("unused")
  public Score getScoreAt(int index) {
    List<Score> filteredScores =
        SCORES.stream().sorted(Comparator.comparingInt(Score::getPoints).reversed()).toList();

    if (index > 0 && index <= filteredScores.size()) {
      return filteredScores.get(index - 1);
    }

    return null;
  }

  public void loadScore(Player p) {
    SCORES.add(new Score(p, this));
  }

  public void unloadScore(Player p) {
    this.getScore(p).save();

    SCORES.removeIf(s -> s.getPlayer().equals(p));
  }

  public void updateElo(Player deadPlayer, Player killer) {
    Score deadScore = this.getScore(deadPlayer);
    Score killerScore = this.getScore(killer);

    assert deadScore != null;
    assert killerScore != null;
    double EloChange = Rating.getELOChange(deadScore.getPoints(), killerScore.getPoints());

    deadScore.changePoints((int) Math.round(Config.get().getInt("elo.max-change") * EloChange));
    killerScore.changePoints(
        (int) Math.round(Config.get().getInt("elo.max-change") * -(0 + EloChange)));
  }

  public static Rating getRating(String name) {
    for (Rating rating : RATINGS) {
      if (Objects.equals(rating.getName(), name)) {
        return rating;
      }
    }

    return null;
  }

  public static Set<Rating> getRatings() {
    return RATINGS;
  }

  public static void register(Player p) {
    for (Rating rating : RATINGS) {
      rating.loadScore(p);
    }
  }

  public static void unregister(Player p) {
    for (Rating rating : RATINGS) {
      rating.unloadScore(p);
    }
  }

  public static double getELOChange(int playerElo, int opponentElo) {
    return -(1 - 1.0 / (1 + Math.pow(10, (playerElo - opponentElo) / 400.0)));
  }

  public enum RatingType {
    GLOBAL,
    REGION,
    CUSTOM
  }
}
