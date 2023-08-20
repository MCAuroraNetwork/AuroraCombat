package club.aurorapvp.auroracombat.commands;

import club.aurorapvp.auroracombat.config.Lang;
import club.aurorapvp.auroracombat.modules.Rating;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import org.bukkit.entity.Player;

@CommandAlias("ratings")
@CommandPermission("auroracombat.command.ratings")
public class RatingCommands extends BaseCommand {

  @Subcommand("create")
  @CommandPermission("auroracombat.command.ratings.create")
  @Syntax("<name> <true|false>")
  @Description("Creates a new rating")
  @SuppressWarnings("unused")
  public void onCreate(Player player, String name, String global) {
    if (Boolean.parseBoolean(global)) {
      new Rating(name, Rating.RatingType.GLOBAL, true).create();
    } else {
      new Rating(name, Rating.RatingType.REGION, true).create();
    }

    player.sendMessage(Lang.getComponent("rating-created"));
  }

  @Subcommand("delete")
  @CommandPermission("auroracombat.command.ratings.delete")
  @Syntax("<name>")
  @Description("Deletes a rating")
  @SuppressWarnings("unused")
  public void onDelete(Player player, String name) {
    Rating.getRating(name).delete();

    player.sendMessage(Lang.getComponent("rating-deleted"));
  }
}
