package club.aurorapvp.auroracombat.commands;


import club.aurorapvp.auroracombat.modules.PracticeDummy;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import org.bukkit.entity.Player;

@CommandAlias("summondummy")
@CommandPermission("auroracombat.command.summondummy")
public class DummyCommand extends BaseCommand {
  @Default
  @Description("Summons a practice dummy")
  @SuppressWarnings("unused")
  public void onSummon(Player player) {
    PracticeDummy.summonDummy(player);
  }
}
