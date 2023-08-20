package club.aurorapvp.auroracombat.data;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.modules.Rating;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import org.bukkit.configuration.file.YamlConfiguration;

public class RatingDataHandler {

  private static final File FILE = new File(AuroraCombat.INSTANCE.getDataFolder(), "ratings.yml");
  private static final YamlConfiguration yaml = YamlConfiguration.loadConfiguration(FILE);
  private final Rating rating;

  public RatingDataHandler(Rating rating) {
    this.rating = rating;
  }

  public void create() {
    try {
      Files.createDirectories(FILE.getParentFile().toPath());
      Files.createFile(FILE.toPath());
    } catch (IOException e) {
      AuroraCombat.INSTANCE.getLogger().log(Level.SEVERE, "Failed to generate ratings file", e);
      return;
    }

    String type = switch (this.rating.getType()) {
      case GLOBAL -> "global";
      case REGION -> "region";
      case CUSTOM -> "custom";
    };

    String key = "ratings." + type;
    List<String> ratings = Optional.of(yaml.getStringList(key)).orElse(new ArrayList<>());

    String newRating = rating.getName();
    if (!ratings.contains(newRating)) {
      ratings.add(newRating);
      yaml.set(key, ratings);

      try {
        yaml.save(FILE);
      } catch (IOException e) {
        AuroraCombat.INSTANCE.getLogger().log(Level.SEVERE, "Failed to save ratings file", e);
      }
    }
  }

  public void delete() {
    String type = switch (this.rating.getType()) {
      case GLOBAL -> "global";
      case REGION -> "region";
      case CUSTOM -> "custom";
    };

    String key = "ratings." + type;
    List<String> ratings = Optional.of(yaml.getStringList(key)).orElse(new ArrayList<>());

    String ratingToDelete = rating.getName();
    if (ratings.contains(ratingToDelete)) {
      ratings.remove(ratingToDelete);
      yaml.set(key, ratings);

      try {
        yaml.save(FILE);
      } catch (IOException e) {
        AuroraCombat.INSTANCE.getLogger().log(Level.SEVERE, "Failed to save ratings file", e);
      }
    }
  }

}
