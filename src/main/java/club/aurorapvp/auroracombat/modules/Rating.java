package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.data.RatingDataHandler;
import club.aurorapvp.auroracombat.flags.RatingFlags;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Rating {

  public static final Map<String, Rating> RATINGS = new HashMap<>();
  private final String name;
  private final RatingType type;
  private final RatingDataHandler data;
  public final Map<UUID, Score> scores = new HashMap<>();
  private final HashMap<UUID, Boolean> enabledPlayers = new HashMap<>();
  private boolean enabled;

  public Rating(String name, RatingType type, boolean enabled) {
    this.name = name;
    this.type = type;
    this.enabled = enabled;

    this.data = new RatingDataHandler(this);

    RATINGS.put(name, this);
  }

  public static void init() {
    if (AuroraCombat.getInstance().getConfig().getBoolean("rating.enable-default")) {
      new Rating("default", RatingType.GLOBAL, true);
    }

    File file = new File(AuroraCombat.getInstance().getDataFolder(), "ratings.yml");
    if (!file.exists()) {
      return;
    }

    YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

    String[] yamlKeys = {"ratings.global", "ratings.region", "ratings.custom"};
    RatingType[] ratingTypes = {RatingType.GLOBAL, RatingType.REGION, RatingType.CUSTOM};

    List<String> ratings;

    for (int i = 0; i < yamlKeys.length; i++) {
      if (yaml.contains(yamlKeys[i])) {
        ratings = yaml.getStringList(yamlKeys[i]);
      } else {
        ratings = new ArrayList<>();
      }

      for (String str : ratings) {
        new Rating(str, ratingTypes[i], true);
      }
    }
  }

  public String getName() {
    return name;
  }

  public String getFriendlyName() {
    String[] words = name.split("_");
    for (int i = 0; i < words.length; i++) {
      words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
    }

    return String.join(" ", words);
  }

  public RatingType getType() {
    return type;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public Set<Score> getScores() {
    return new HashSet<>(scores.values());
  }

  @SuppressWarnings("unused")
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @SuppressWarnings("unused")
  public void setPlayerEnabled(Player player, boolean enabled) {
    enabledPlayers.put(player.getUniqueId(), enabled);
  }

  public boolean isEnabled(Player player) {
    if (!this.isEnabled()) {
      return false;
    }

    if (enabledPlayers.containsKey(player.getUniqueId())) {
      return enabledPlayers.get(player.getUniqueId());
    }

    if (!AuroraCombat.getInstance().isWorldGuardInstalled()) {
      return this.getType() != RatingType.REGION;
    }

    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    RegionQuery query = container.createQuery();
    ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));

    if (set.size() == 0) {
      return this.getType() != RatingType.REGION;
    }

    for (ProtectedRegion region : set.getRegions()) {
      return (Objects.equals(region.getFlag(RatingFlags.GLOBAL_RATINGS), StateFlag.State.ALLOW)
              && this.getType() == RatingType.GLOBAL)
          || (Objects.equals(region.getFlag(RatingFlags.REGION_RATING), name)
              && this.getType() == RatingType.REGION);
    }

    return false;
  }

  public Score getScore(Player player) {
    return scores.get(player.getUniqueId());
  }

  @SuppressWarnings("unused")
  public Score getScoreAt(int index) {
    List<Score> filteredScores =
        scores.values().stream()
            .sorted(Comparator.comparingInt(Score::getPoints).reversed())
            .toList();

    if (index > 0 && index <= filteredScores.size()) {
      return filteredScores.get(index - 1);
    }

    return null;
  }

  public void create() {
    data.create();
  }

  public void delete() {
    data.delete();
  }

  public void updateElo(Player deadPlayer, Player killer) {
    Score deadScore = this.getScore(deadPlayer);
    Score killerScore = this.getScore(killer);

    assert deadScore != null;
    assert killerScore != null;
    double EloChange = Rating.getELOChange(deadScore.getPoints(), killerScore.getPoints());

    deadScore.changePoints(
        (int) (AuroraCombat.getInstance().getConfig().getInt("elo.max-change") * EloChange));
    killerScore.changePoints(
        (int) (AuroraCombat.getInstance().getConfig().getInt("elo.max-change") * -(0 + EloChange)));

    deadPlayer.sendMessage(
        AuroraCombat.getInstance()
            .getLang()
            .formatComponent(
                "you-were-killed-by",
                killer.getName(),
                killerScore.getPoints(),
                (int)
                    (AuroraCombat.getInstance().getConfig().getInt("elo.max-change") * EloChange)));
    killer.sendMessage(
        AuroraCombat.getInstance()
            .getLang()
            .formatComponent(
                "you-killed",
                deadPlayer.getName(),
                deadScore.getPoints(),
                (int)
                    (AuroraCombat.getInstance().getConfig().getInt("elo.max-change")
                        * -(0 + EloChange))));
  }

  @SuppressWarnings("unused")
  public void updateElo(Player player, int referencePoints, boolean winner) {
    Score score = this.getScore(player);

    double EloChange = Rating.getELOChange(score.getPoints(), referencePoints);

    if (winner) {
      score.changePoints(
          (int)
              Math.round(
                  AuroraCombat.getInstance().getConfig().getInt("elo.max-change")
                      * -(0 + EloChange)));
    } else {
      score.changePoints(
          (int)
              Math.round(
                  AuroraCombat.getInstance().getConfig().getInt("elo.max-change") * EloChange));
    }
  }

  public static Rating getRating(String name) {
    return RATINGS.get(name);
  }

  public static Set<Rating> getRatings() {
    return new HashSet<>(RATINGS.values());
  }

  public static void register(Player player) {
    for (Rating rating : RATINGS.values()) {
      rating.scores.put(player.getUniqueId(), new Score(player, rating));
    }
  }

  public static void unregister(Player player) {
    new BukkitRunnable() {
      @Override
      public void run() {
        for (Rating rating : RATINGS.values()) {
          rating.scores.remove(player.getUniqueId());
        }
      }
    }.runTaskLater(AuroraCombat.getInstance(), 1);
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
