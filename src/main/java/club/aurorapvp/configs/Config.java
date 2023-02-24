package club.aurorapvp.configs;

import club.aurorapvp.AuroraCombat;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
  private static final HashMap<String, Object> DEFAULTS = new HashMap<>();
  private static final File FILE = new File(AuroraCombat.DATA_FOLDER, "config.yml");
  private static YamlConfiguration config;

  public Config() {
    reload();
    generateDefaults();
  }

  public static void generateDefaults() {
    DEFAULTS.put("elo.default-points", 1000);
    DEFAULTS.put("elo.max-change", 32);
    DEFAULTS.put("combat-tag.duration", 15000);
    DEFAULTS.put("combat-tag.allow-commands", false);
    DEFAULTS.put("misc.fall-damage.enable-first", false);

    for (String path : DEFAULTS.keySet()) {
      if (!get().contains(path) || get().getString(path) == null) {
        get().set(path, DEFAULTS.get(path));

        try {
          get().save(FILE);
        } catch (IOException e) {
          AuroraCombat.INSTANCE.getLogger().severe("Failed to save config file");
        }
      }
    }
  }

  public static YamlConfiguration get() {
    return config;
  }

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