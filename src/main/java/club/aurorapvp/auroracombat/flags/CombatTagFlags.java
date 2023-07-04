package club.aurorapvp.auroracombat.flags;

import club.aurorapvp.auroracombat.AuroraCombat;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

public class CombatTagFlags {
  public static StateFlag TAGS_ENABLED;

  public static void init() {
    FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

    try {
      StateFlag flag = new StateFlag("tags-enabled", true);
      registry.register(flag);
      TAGS_ENABLED = flag;
    } catch (FlagConflictException e) {
      AuroraCombat.INSTANCE.getLogger().severe("Unable to register Tags Enabled flag");
    }
  }
}
