package club.aurorapvp.auroracombat.config;

import club.aurorapvp.auroracombat.AuroraCombat;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;

public class Lang {
  private static final HashMap<String, String> PLACEHOLDERS = new HashMap<>();
  private static final File FILE = new File(AuroraCombat.INSTANCE.getDataFolder(), "lang.yml");
  private static YamlConfiguration lang;

  public static void init() {
    reload();
    generateDefaults();
  }

  public static void generateDefaults() {
    final HashMap<String, String> DEFAULTS = new HashMap<>();

    DEFAULTS.put("prefix", "~<gradient:#FFAA00:#FF55FF><bold>AuroraCombat ><reset>~");
    DEFAULTS.put("points-increased", "prefix <green>%1$s ELO + %2$s points");
    DEFAULTS.put("points-decreased", "prefix <red>%1$s ELO - %2$s points");
    DEFAULTS.put("tagged", "prefix <aqua>Tagged by %1$s for %2$s sec");
    DEFAULTS.put("tagged-action-bar", "<aqua>Tagged by %1$s for %2$s sec");
    DEFAULTS.put("tag-removed", "prefix <aqua>Untagged by %s");
    DEFAULTS.put("tag-removed-action-bar", "<aqua>Untagged by %s");
    DEFAULTS.put("death-message.killed-by-player-generic", "<red>%1$s killed by %2$s");
    DEFAULTS.put("death-message.killed-by-player-explosion", "<red>%1$s blown up by %2$s with %3$s");
    DEFAULTS.put("death-message.killed-by-player-slain", "<red>%1$s slain by %2$s with %3$s");
    DEFAULTS.put("death-message.killed-by-player-shot", "<red>%1$s shot by %2$s with %3$s");
    DEFAULTS.put("death-message.killed-by-player-magic", "<red>%1$s killed by %2$s's magic %3$s");
    DEFAULTS.put("commands-disabled", "prefix <red>Commands disabled in combat");
    DEFAULTS.put("rating-created", "prefix <green>Rating created");
    DEFAULTS.put("opponent-bossbar", "<aqua><bold>%1$s <reset><red>| %2$s❤ |<reset><aqua> %3$sm |<reset><yellow> %4$sms");
    DEFAULTS.put("player-health-and-ping", "<reset><red>❤ |<reset><blue> %s" + "ms");

    for (String path : DEFAULTS.keySet()) {
      if (!get().contains(path) || get().getString(path) == null) {
        get().set(path, DEFAULTS.get(path));
      }
    }

    try {
      get().save(FILE);
    } catch (IOException e) {
      AuroraCombat.INSTANCE.getLogger().severe("Failed to save lang file");
    }

    for (Object path : get().getKeys(false).toArray()) {
      if (Objects.requireNonNull(get().getString((String) path)).startsWith("~")
          && Objects.requireNonNull(get().getString((String) path)).endsWith("~")) {
        PLACEHOLDERS.put(
            (String) path, Objects.requireNonNull(get().getString((String) path)).replace("~", ""));
      }
    }
  }

  @SuppressWarnings("unused")
  public static String getString(String message) {
    String pathString = get().getString(message);
    for (String placeholder : PLACEHOLDERS.keySet()) {
      assert pathString != null;
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder, PLACEHOLDERS.get(placeholder));
      }
    }
    return pathString;
  }

  public static Component formatComponent(String message, Object... args) {
    String pathString = get().getString(message);
    assert pathString != null;
    for (String placeholder : PLACEHOLDERS.keySet()) {
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder, PLACEHOLDERS.get(placeholder));
      }
    }

    pathString = String.format(pathString, args);

    return MiniMessage.miniMessage().deserialize(pathString);
  }

  public static Component getComponent(String message) {
    String pathString = get().getString(message);
    assert pathString != null;

    for (String placeholder : PLACEHOLDERS.keySet()) {
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder, PLACEHOLDERS.get(placeholder));
      }
    }
    return MiniMessage.miniMessage().deserialize(pathString);
  }

  public static YamlConfiguration get() {
    return lang;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
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
