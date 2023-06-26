package club.aurorapvp.auroracombat;

import club.aurorapvp.auroracombat.configs.Config;
import club.aurorapvp.auroracombat.configs.Lang;
import club.aurorapvp.auroracombat.events.Events;
import club.aurorapvp.auroracombat.flags.RatingFlags;
import club.aurorapvp.auroracombat.modules.Rating;
import club.aurorapvp.auroracombat.placeholders.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuroraCombat extends JavaPlugin {
  public static JavaPlugin INSTANCE;
  private static boolean worldGuardInstalled;
  private long startTime;

  @Override
  public void onLoad() {
    startTime = System.currentTimeMillis();

    INSTANCE = this;

    // Setup configs
    Config.init();
    Lang.init();

    if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
      if (Config.get().getBoolean("optional-plugins.worldguard-compatibility")) {
        RatingFlags.init();
        worldGuardInstalled = true;
      }
    }
  }

  @Override
  public void onEnable() {
    // Setup classes
    Events.init();
    Rating.init();
    Commands.init();

    // Check if soft depends are installed
    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      new Placeholders().register();
    }

    getLogger().info(
        "Aurora Combat Loaded in " + Math.subtractExact(System.currentTimeMillis(), startTime) +
            "ms");
  }

  @Override
  public void onDisable() {
    long startTime = System.currentTimeMillis();

    Rating.saveAll();

    getLogger().info(
        "Aurora Combat Unloaded in " + Math.subtractExact(System.currentTimeMillis(), startTime) +
            "ms");
  }

  public static boolean isWorldGuardInstalled() {
    return worldGuardInstalled;
  }
}
