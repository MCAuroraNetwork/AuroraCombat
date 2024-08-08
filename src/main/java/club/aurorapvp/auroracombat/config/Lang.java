package club.aurorapvp.auroracombat.config;

import club.aurorapvp.auroracombat.AuroraCombat;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;

public class Lang {

  private final HashMap<String, String> PLACEHOLDERS = new HashMap<>();
  private final File FILE = new File(AuroraCombat.getInstance().getDataFolder(), "lang.yml");
  private YamlConfiguration lang;

  public Lang() {
    this.reload();
    this.generateDefaults();
  }

  public void generateDefaults() {
    final HashMap<String, String> DEFAULTS = new HashMap<>();

    DEFAULTS.put("prefix", "~<gradient:#FFAA00:#FF55FF><bold>AuroraCombat ><reset>~");
    DEFAULTS.put("reloaded", "prefix <green>Reloaded");
    DEFAULTS.put("you-killed", "prefix <green>You killed <bold>%1$s<reset><green>!%2$s");
    DEFAULTS.put(
        "you-were-killed-by", "prefix <red>You were killed by <bold>%1$s!<reset><red>.%2$s");
    DEFAULTS.put("tagged", "prefix <red>Tagged by <bold>%1$s<reset><red> for %2$s seconds");
    DEFAULTS.put("tag-removed", "prefix <green>You're no longer tagged by %s!");
    DEFAULTS.put("tag-removed-action-bar", "<green>You're no longer tagged by %s!");
    DEFAULTS.put(
        "killstreak-lost",
        "prefix <red><bold>%1$s<reset><red> died and lost <red><bold>%2$s<reset><red> Killstreak!");
    DEFAULTS.put(
        "on-killstreak",
        "prefix <green><bold>%1$s<reset><green> has a Killstreak of <green><bold>%2$s<reset><green>!");
    DEFAULTS.put("discord-killstreak-lost", "%1$s died and lost %2$s Killstreak!");
    DEFAULTS.put("discord-on-killstreak", "%1$s has a Killstreak of %2$s!");
    DEFAULTS.put("death-message.killed-by-player-generic", "<red>%1$s killed by %2$s");
    DEFAULTS.put(
        "death-message.killed-by-player-explosion", "<red>%1$s was blown up by %2$s with %3$s");
    DEFAULTS.put("death-message.killed-by-player-slain", "<red>%1$s was slain by %2$s with %3$s");
    DEFAULTS.put("death-message.killed-by-player-shot", "<red>%1$s was shot by %2$s with %3$s");
    DEFAULTS.put(
        "death-message.killed-by-player-magic", "<red>%1$s was killed by %2$s's magic %3$s");
    DEFAULTS.put("death-message.combat-logged", "<red>%1$s combat logged to hide from %2$s");
    DEFAULTS.put("unknown-player", "prefix <red>Player not found!");
    DEFAULTS.put("commands-disabled", "prefix <red>Commands disabled in combat");
    DEFAULTS.put("no-running", "prefix <red>No running from combat");
    DEFAULTS.put("dummy-summoned", "prefix <green>Summoned a practice dummy!");
    DEFAULTS.put("rating-created", "prefix <green>Rating created!");
    DEFAULTS.put(
        "stats-command",
        """
        prefix <yellow><bold>%1$s's Stats<reset>
        %2$s

        <yellow><bold>Kills: <reset><yellow>%3$s
        <yellow><bold>Deaths: <reset><yellow>%4$s
        <yellow><bold>KDR: <reset><yellow>%5$s
        """);

    for (String path : DEFAULTS.keySet()) {
      if (!getYaml().contains(path) || getYaml().getString(path) == null) {
        getYaml().set(path, DEFAULTS.get(path));
      }
    }

    try {
      getYaml().save(FILE);
    } catch (IOException e) {
      AuroraCombat.getInstance().getLogger().log(Level.SEVERE, "Failed to save lang file", e);
    }

    for (var path : getYaml().getKeys(false).toArray()) {
      if (Objects.requireNonNull(getYaml().getString((String) path)).startsWith("~")
          && Objects.requireNonNull(getYaml().getString((String) path)).endsWith("~")) {
        PLACEHOLDERS.put(
            (String) path,
            Objects.requireNonNull(getYaml().getString((String) path)).replace("~", ""));
      }
    }
  }

  public String getString(String message) {
    String pathString = getYaml().getString(message);
    for (String placeholder : PLACEHOLDERS.keySet()) {
      assert pathString != null;
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder, PLACEHOLDERS.get(placeholder));
      }
    }
    return pathString;
  }

  public Component formatComponent(String message, Object... args) {
    String pathString = getYaml().getString(message);
    assert pathString != null;
    for (String placeholder : PLACEHOLDERS.keySet()) {
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder, PLACEHOLDERS.get(placeholder));
      }
    }

    pathString = String.format(pathString, args);

    return MiniMessage.miniMessage().deserialize(pathString);
  }

  public Component getComponent(String message) {
    String pathString = getYaml().getString(message);
    assert pathString != null;

    for (String placeholder : PLACEHOLDERS.keySet()) {
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder, PLACEHOLDERS.get(placeholder));
      }
    }
    return MiniMessage.miniMessage().deserialize(pathString);
  }

  public YamlConfiguration getYaml() {
    return lang;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void reload() {
    if (!FILE.exists()) {
      try {
        FILE.getParentFile().mkdirs();
        FILE.createNewFile();

        lang = YamlConfiguration.loadConfiguration(FILE);

        this.generateDefaults();
      } catch (IOException e) {
        AuroraCombat.getInstance().getLogger().log(Level.SEVERE, "Failed to generate lang file", e);
      }
    }

    lang = YamlConfiguration.loadConfiguration(FILE);
    AuroraCombat.getInstance().getLogger().info("Lang reloaded!");
  }
}
