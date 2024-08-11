package club.aurorapvp.auroracombat.commands;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.modules.KillDeathTracker;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("stats")
@CommandPermission("auroracombat.command.stats")
public class StatsCommand extends BaseCommand {

  @Default
  @CommandCompletion("@players")
  @Syntax("<player>")
  @Description("Gets another player's major stats")
  @SuppressWarnings("unused")
  public void onGetStats(Player sender, @Optional String playerName) {
    Player player = playerName == null ? sender : Bukkit.getPlayer(playerName);

    if (player == null) {
      assert sender != null;
      sender.sendMessage(AuroraCombat.getInstance().getLang().getComponent("unknown-player"));
      return;
    }

    KillDeathTracker tracker = KillDeathTracker.getTracker(player);

    player.sendMessage(
        AuroraCombat.getInstance()
            .getLang()
            .formatComponent(
                "stats-command",
                player.getName(),
                tracker.getKills(),
                tracker.getDeaths(),
                tracker.getKDR()));
  }
}
