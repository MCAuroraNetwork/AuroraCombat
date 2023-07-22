package club.aurorapvp.auroracombat.config;

import club.aurorapvp.auroracombat.AuroraCombat;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
  private static final File FILE = new File(AuroraCombat.INSTANCE.getDataFolder(), "config.yml");
  private static YamlConfiguration config;

  public static void init() {
    reload();
    generateDefaults();
  }

  public static void generateDefaults() {
    final HashMap<String, Object> DEFAULTS = new HashMap<>();

    DEFAULTS.put("elo.default-points", 400);
    DEFAULTS.put("elo.max-change", 32);
    DEFAULTS.put("combat-tag.duration", 15000);
    DEFAULTS.put("combat-tag.allow-commands", false);
    DEFAULTS.put("combat-tag.enable", true);
    DEFAULTS.put("misc.fall-damage.enable-first", false);
    DEFAULTS.put("rating.enable-default", true);
    DEFAULTS.put("optional-plugins.worldguard-compatibility", true);

    for (String path : DEFAULTS.keySet()) {
      if (!get().isSet(path) || get().getString(path) == null) {
        get().set(path, DEFAULTS.get(path));
      }
    }

    try {
      get().save(FILE);
    } catch (IOException e) {
      AuroraCombat.INSTANCE.getLogger().severe("Failed to save config file");
    }
  }

  public static YamlConfiguration get() {
    return config;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public static void reload() {
    if (!FILE.exists()) {
      try {
        FILE.getParentFile().mkdirs();
        FILE.createNewFile();
      } catch (IOException e) {
        AuroraCombat.INSTANCE.getLogger().severe("Failed to generate config file");
      }
    }
    config = YamlConfiguration.loadConfiguration(FILE);
    AuroraCombat.INSTANCE.getLogger().info("Config reloaded!");
  }
}
