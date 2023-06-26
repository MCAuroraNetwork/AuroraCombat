package club.aurorapvp.auroracombat;

import club.aurorapvp.auroracombat.commands.PluginCommands;
import co.aikar.commands.PaperCommandManager;

public class Commands {
  public static PaperCommandManager MANAGER = new PaperCommandManager(AuroraCombat.INSTANCE);

  public static void init() {
    MANAGER.registerCommand(new PluginCommands());
  }
}
