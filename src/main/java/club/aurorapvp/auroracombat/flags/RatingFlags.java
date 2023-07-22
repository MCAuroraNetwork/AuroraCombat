package club.aurorapvp.auroracombat.flags;

import club.aurorapvp.auroracombat.AuroraCombat;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

public class RatingFlags {
  public static StateFlag GLOBAL_RATINGS;
  public static StringFlag REGION_RATING;

  public static void init() {
    FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

    try {
      StateFlag flag = new StateFlag("global-ratings", true);
      registry.register(flag);
      GLOBAL_RATINGS = flag;
    } catch (FlagConflictException e) {
      AuroraCombat.INSTANCE.getLogger().severe("Unable to register Global Rating flag");
    }

    try {
      StringFlag flag = new StringFlag("region-ratings");
      registry.register(flag);
      REGION_RATING = flag;
    } catch (FlagConflictException e) {
      AuroraCombat.INSTANCE.getLogger().severe("Unable to register Regional Rating flag");
    }
  }
}
