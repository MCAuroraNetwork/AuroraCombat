package club.aurorapvp.auroracombat.commands;

import club.aurorapvp.auroracombat.config.Lang;
import club.aurorapvp.auroracombat.modules.Rating;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.entity.Player;

@CommandAlias("ratings")
@CommandPermission("auroracombat.command.ratings")
public class RatingCommands extends BaseCommand {

  @Subcommand("create")
  @Syntax("[name] [global: true|false]")
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
}
