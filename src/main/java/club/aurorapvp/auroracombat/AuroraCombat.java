package club.aurorapvp.auroracombat;

import club.aurorapvp.auroracombat.commands.CommandManager;
import club.aurorapvp.auroracombat.config.Config;
import club.aurorapvp.auroracombat.config.Lang;
import club.aurorapvp.auroracombat.events.EventManager;
import club.aurorapvp.auroracombat.flags.CombatTagFlags;
import club.aurorapvp.auroracombat.flags.RatingFlags;
import club.aurorapvp.auroracombat.modules.Placeholders;
import club.aurorapvp.auroracombat.modules.Rating;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuroraCombat extends JavaPlugin {
  public static JavaPlugin INSTANCE;
  private static boolean worldGuardInstalled;

  @Override
  public void onLoad() {
    long startTime = System.currentTimeMillis();

    INSTANCE = this;

    // Setup configs
    Config.init();
    Lang.init();

    if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
      if (Config.get().getBoolean("optional-plugins.worldguard-compatibility")) {
        RatingFlags.init();
        CombatTagFlags.init();
        worldGuardInstalled = true;
      }
    }

    getLogger().info(
        "AuroraCombat loaded in " + Math.subtractExact(System.currentTimeMillis(), startTime) +
            "ms");
  }

  @Override
  public void onEnable() {
    long startTime = System.currentTimeMillis();

    // Setup classes
    EventManager.init();
    Rating.init();
    CommandManager.init();

    // Check if soft depends are installed
    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      new Placeholders().register();
    }

    getLogger().info(
        "AuroraCombat enabled in " + Math.subtractExact(System.currentTimeMillis(), startTime) +
            "ms");
  }

  public static boolean isWorldGuardInstalled() {
    return worldGuardInstalled;
  }

  @Override
  public void onDisable() {
    long startTime = System.currentTimeMillis();

    Rating.saveAll();

    getLogger().info(
        "AuroraCombat disabled in " + Math.subtractExact(System.currentTimeMillis(), startTime) +
            "ms");
  }
}
