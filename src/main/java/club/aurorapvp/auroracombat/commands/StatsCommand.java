package club.aurorapvp.auroracombat.commands;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.modules.KillDeathTracker;
import club.aurorapvp.auroracombat.modules.Rating;
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

    StringBuilder ratings = new StringBuilder();
    for (Rating rating : Rating.getRatings()) {
      String ratingName = rating.getFriendlyName();
      int points = rating.getScore(player).getPoints();
      ratings
          .append("<aqua><bold>")
          .append(ratingName)
          .append(" Points: <reset><aqua>")
          .append(points)
          .append("<reset>\n");
    }

    player.sendMessage(
        AuroraCombat.getInstance()
            .getLang()
            .formatComponent(
                "stats-command",
                player.getName(),
                ratings.toString(),
                tracker.getKills(),
                tracker.getDeaths(),
                tracker.getKDR()));
  }
}
