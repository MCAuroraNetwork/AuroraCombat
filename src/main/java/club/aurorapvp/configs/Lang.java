package club.aurorapvp.configs;

import club.aurorapvp.AuroraCombat;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;

public class Lang {
  private static final HashMap<String, String> PLACEHOLDERS = new HashMap<>();
  private static final HashMap<String, String> DEFAULTS = new HashMap<>();
  private static final File FILE = new File(AuroraCombat.DATA_FOLDER, "lang.yml");
  private static YamlConfiguration lang;

  public static void init() {
    reload();
    generateDefaults();
  }

  public static void generateDefaults() {
    DEFAULTS.put("prefix", "~<gradient:#FFAA00:#FF55FF><bold>AuroraCombat ><reset>~");
    DEFAULTS.put("points-changed",
        "prefix <gradient:#FFAA00:#FF55FF>Your ELO changed by %s points!");
    DEFAULTS.put("tagged",
        "prefix <gradient:#FFAA00:#FF55FF>You have been tagged by %1$s for %2$s seconds!");
    DEFAULTS.put("tagged-action-bar",
        "<gradient:#FFAA00:#FF55FF>You are tagged by %1$s for %2$s more seconds");
    DEFAULTS.put("tag-removed", "prefix <gradient:#FFAA00:#FF55FF>You are no longer tagged by %s!");
    DEFAULTS.put("tag-removed-action-bar",
        "<gradient:#FFAA00:#FF55FF>You are no longer tagged by %s!");
    DEFAULTS.put("death-message.killed-by-player-generic",
        "<gradient:#FFAA00:#FF55FF>%1$s was killed by %2$s");
    DEFAULTS.put("death-message.killed-by-player-explosion",
        "<gradient:#FFAA00:#FF55FF>%1$s was blown up by %2$s using %3$s");
    DEFAULTS.put("death-message.killed-by-player-slain",
        "<gradient:#FFAA00:#FF55FF>%1$s was slain by %2$s using %3$s");
    DEFAULTS.put("death-message.killed-by-player-shot",
        "<gradient:#FFAA00:#FF55FF>%1$s was shot by %2$s using %3$s");
    DEFAULTS.put("commands-disabled",
        "prefix <gradient:#FFAA00:#FF55FF>Commands are disabled in combat!");

    for (String path : DEFAULTS.keySet()) {
      if (!get().contains(path) || get().getString(path) == null) {
        get().set(path, DEFAULTS.get(path));

        try {
          get().save(FILE);
        } catch (IOException e) {
          AuroraCombat.INSTANCE.getLogger().severe("Failed to save lang file");
        }
      }
    }

    for (Object path : get().getKeys(false).toArray()) {
      if (Objects.requireNonNull(get().getString((String) path)).startsWith("~") &&
          Objects.requireNonNull(get().getString((String) path)).endsWith("~")) {
        PLACEHOLDERS.put((String) path, Objects.requireNonNull(get().getString((String) path))
            .replace("~", ""));
      }
    }
  }

  public static String getString(String message) {
    String pathString = get().getString(message);
    for (String placeholder : PLACEHOLDERS.keySet()) {
      assert pathString != null;
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder,
            PLACEHOLDERS.get(placeholder));
      }
    }
    return pathString;
  }

  public static Component formatComponent(String message, Object... args) {
    String pathString = get().getString(message);
    assert pathString != null;
    for (String placeholder : PLACEHOLDERS.keySet()) {
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder,
            PLACEHOLDERS.get(placeholder));
      }
    }

    pathString = String.format(pathString, args);

    return AuroraCombat.COMPONENT_DESERIALIZER.deserialize(pathString);
  }

  public static Component getComponent(String message) {
    String pathString = get().getString(message);
    assert pathString != null;

    for (String placeholder : PLACEHOLDERS.keySet()) {
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder,
            PLACEHOLDERS.get(placeholder));
      }
    }
    return AuroraCombat.COMPONENT_DESERIALIZER.deserialize(pathString);
  }

  public static YamlConfiguration get() {
    return lang;
  }

  public static void reload() {
    if (!FILE.exists()) {
      try {
        FILE.getParentFile().mkdirs();
        FILE.createNewFile();
      } catch (IOException e) {
        AuroraCombat.INSTANCE.getLogger().severe("Failed to generate lang file");
      }
    }
    lang = YamlConfiguration.loadConfiguration(FILE);
    AuroraCombat.INSTANCE.getLogger().info("Lang reloaded!");
  }
}
