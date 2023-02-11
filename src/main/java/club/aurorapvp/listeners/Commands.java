package club.aurorapvp.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor, TabCompleter {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, @NotNull String[] args) {
    if (sender instanceof Player p) {
      switch (command.getName()) {

      }
    }
    return true;
  }

  private static final List<String> results = new ArrayList<>();

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                    @NotNull String alias, String[] args) {
    results.clear();

    if (args.length == 1) {
      switch (cmd.getLabel()) {

      }
    }

    Collections.sort(results);
    return results;
  }
}
