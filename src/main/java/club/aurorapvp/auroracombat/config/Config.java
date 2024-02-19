package club.aurorapvp.auroracombat.config;

import club.aurorapvp.auroracombat.AuroraCombat;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

  private final File FILE = new File(AuroraCombat.getInstance().getDataFolder(), "config.yml");
  private YamlConfiguration config;

  public Config() {
    this.reload();
    this.generateDefaults();
  }

  public void generateDefaults() {
    final HashMap<String, Object> DEFAULTS = new HashMap<>();

    DEFAULTS.put("elo.default-points", 400);
    DEFAULTS.put("elo.max-change", 32);
    DEFAULTS.put("combat-tag.duration", 15);
    DEFAULTS.put("combat-tag.commands.allow-commands", false);
    DEFAULTS.put("combat-tag.commands.whitelisted", new ArrayList<>(List.of("kill")));
    DEFAULTS.put("combat-tag.enable", true);
    DEFAULTS.put("misc.fall-damage.enable-first", false);
    DEFAULTS.put("misc.min-killstreak-to-announce", 10);
    DEFAULTS.put("misc.ender-pearl-cooldown.enabled", false);
    DEFAULTS.put("misc.ender-pearl-cooldown.max-distance", 100);
    DEFAULTS.put("misc.ender-pearl-cooldown.time", 10);
    DEFAULTS.put("misc.ender-pearl-cooldown.only-active-when-tagged", false);
    DEFAULTS.put("rating.enable-default", true);
    DEFAULTS.put("optional-plugins.worldguard-compatibility", true);

    for (String path : DEFAULTS.keySet()) {
      if (!getYaml().isSet(path) || getYaml().getString(path) == null) {
        getYaml().set(path, DEFAULTS.get(path));
      }
    }

    try {
      getYaml().save(FILE);
    } catch (IOException e) {
      AuroraCombat.getInstance().getLogger().log(Level.SEVERE, "Failed to save config file", e);
    }
  }

  public YamlConfiguration getYaml() {
    return config;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void reload() {
    if (!FILE.exists()) {
      try {
        FILE.getParentFile().mkdirs();
        FILE.createNewFile();

        config = YamlConfiguration.loadConfiguration(FILE);

        this.generateDefaults();
      } catch (IOException e) {
        AuroraCombat.getInstance().getLogger().log(Level.SEVERE, "Failed to generate config file", e);
      }
    }

    config = YamlConfiguration.loadConfiguration(FILE);
    AuroraCombat.getInstance().getLogger().info("Config reloaded!");
  }
}
