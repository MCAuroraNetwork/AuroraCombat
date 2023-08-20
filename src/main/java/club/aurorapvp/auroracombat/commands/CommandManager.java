package club.aurorapvp.auroracombat.commands;

import club.aurorapvp.auroracombat.AuroraCombat;
import co.aikar.commands.PaperCommandManager;

public class CommandManager {
  public static PaperCommandManager MANAGER = new PaperCommandManager(AuroraCombat.INSTANCE);

  public static void init() {
    MANAGER.registerCommand(new RatingCommands());
    MANAGER.registerCommand(new PluginCommands());
    MANAGER.registerCommand(new StatsCommand());
  }
}
