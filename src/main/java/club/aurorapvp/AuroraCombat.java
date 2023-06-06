package club.aurorapvp;

import club.aurorapvp.configs.Config;
import club.aurorapvp.configs.Lang;
import club.aurorapvp.events.Events;
import club.aurorapvp.flags.RatingFlags;
import club.aurorapvp.modules.Rating;
import club.aurorapvp.placeholders.Placeholders;
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
