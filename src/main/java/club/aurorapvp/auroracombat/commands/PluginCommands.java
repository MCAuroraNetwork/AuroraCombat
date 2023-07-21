package club.aurorapvp.auroracombat.commands;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.config.Config;
import club.aurorapvp.auroracombat.config.Lang;
import club.aurorapvp.auroracombat.modules.Rating;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("auroracombat")
@CommandPermission("auroracombat.command.auroracombat")
public class PluginCommands extends BaseCommand {
  @Subcommand("reload")
  @CommandPermission("auroracombat.admin")
  @Description("Reloads and saves all plugin data")
  @SuppressWarnings("unused")
  public void onReload() {
    long startTime = System.currentTimeMillis();

    Config.reload();
    Lang.reload();
    Rating.saveAll();

    AuroraCombat.INSTANCE.getLogger().info(
        "AuroraCombat reloaded in " + (System.currentTimeMillis() - startTime) + "ms");
  }
}
