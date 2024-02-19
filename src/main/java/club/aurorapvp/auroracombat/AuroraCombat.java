package club.aurorapvp.auroracombat;

import club.aurorapvp.auroracombat.commands.CommandManager;
import club.aurorapvp.auroracombat.config.Config;
import club.aurorapvp.auroracombat.config.Lang;
import club.aurorapvp.auroracombat.events.EventManager;
import club.aurorapvp.auroracombat.flags.CombatTagFlags;
import club.aurorapvp.auroracombat.flags.RatingFlags;
import club.aurorapvp.auroracombat.modules.KillDeathTracker;
import club.aurorapvp.auroracombat.modules.Placeholders;
import club.aurorapvp.auroracombat.modules.PlayerInfo;
import club.aurorapvp.auroracombat.modules.Rating;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class AuroraCombat extends JavaPlugin {

  private static AuroraCombat INSTANCE;
  private boolean worldGuardInstalled;
  private Config config;
  private Lang lang;

  public static AuroraCombat getInstance() {
    return INSTANCE;
  }

  public boolean isWorldGuardInstalled() {
    return worldGuardInstalled;
  }

  public @NotNull YamlConfiguration getConfig() {
    return config.getYaml();
  }

  public Lang getLang() {
    return lang;
  }

  @Override
  public void onLoad() {
    long startTime = System.currentTimeMillis();

    INSTANCE = this;

    // Setup configs
    config = new Config();
    lang = new Lang();

    if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
      if (this.getConfig().getBoolean("optional-plugins.worldguard-compatibility")) {
        RatingFlags.init();
        CombatTagFlags.init();
        worldGuardInstalled = true;
      }
    }

    getLogger().info("AuroraCombat loaded in " + (System.currentTimeMillis() - startTime) + "ms");
  }

  @Override
  public void onEnable() {
    long startTime = System.currentTimeMillis();

    // Setup classes
    EventManager.init();
    Rating.init();
    PlayerInfo.init();
    CommandManager.init();

    // Check if soft depends are installed
    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      new Placeholders().register();
    }

    getLogger().info("AuroraCombat enabled in " + (System.currentTimeMillis() - startTime) + "ms");
  }

  @Override
  public void onDisable() {
    long startTime = System.currentTimeMillis();

    Rating.saveAll();
    KillDeathTracker.saveAll();

    getLogger().info("AuroraCombat disabled in " + (System.currentTimeMillis() - startTime) + "ms");
  }

  public void reloadConfig() {
    config.reload();
  }
}
