package club.aurorapvp.auroracombat.commands;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.config.Lang;
import club.aurorapvp.auroracombat.modules.KillDeathTracker;
import club.aurorapvp.auroracombat.modules.Rating;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Syntax;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("stats")
@CommandPermission("auroracombat.command.stats")
public class StatsCommand extends BaseCommand {

  @Default
  @Description("Gets your own major stats")
  @SuppressWarnings("unused")
  public void onGetStats(Player player) {
    KillDeathTracker tracker = KillDeathTracker.getTracker(player);

    StringBuilder ratings = new StringBuilder();
    for (Rating rating : Rating.getRatings()) {
      String ratingName = rating.getFriendlyName();
      int points = rating.getScore(player).getPoints();
      ratings.append("<aqua>").append(ratingName).append(" Points: ").append(points).append("\n");
    }

    player.sendMessage(
        AuroraCombat.INSTANCE.getLang().formatComponent("stats-command", player.getName(), ratings.toString(),
            tracker.getKills(),
            tracker.getDeaths(), tracker.getKDR()));
  }

  @Default
  @CommandCompletion("@players")
  @Syntax("<player>")
  @Description("Gets another player's major stats")
  @SuppressWarnings("unused")
  public void onGetStats(Player sender, String playerName) {
    Player player = Bukkit.getPlayer(playerName);

    if (player == null) {
      sender.sendMessage(AuroraCombat.INSTANCE.getLang().getComponent("unknown-player"));
      return;
    }

    KillDeathTracker tracker = KillDeathTracker.getTracker(player);

    StringBuilder ratings = new StringBuilder();
    for (Rating rating : Rating.getRatings()) {
      String ratingName = rating.getFriendlyName();
      int points = rating.getScore(player).getPoints();
      ratings.append("<aqua>").append(ratingName).append(" Points: ").append(points).append("\n");
    }

    player.sendMessage(
        AuroraCombat.INSTANCE.getLang().formatComponent("stats-command", player.getName(), ratings.toString(),
            tracker.getKills(),
            tracker.getDeaths(), tracker.getKDR()));
  }
}
