package club.aurorapvp;

import club.aurorapvp.configs.Config;
import club.aurorapvp.configs.Lang;
import club.aurorapvp.listeners.Commands;
import club.aurorapvp.listeners.Events;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuroraCombat extends JavaPlugin {
  public static Plugin INSTANCE;
  public static Logger LOGGER;
  public static File DATA_FOLDER;
  public static PlainTextComponentSerializer COMPONENT_SERIALIZER;
  public static MiniMessage COMPONENT_DESERIALIZER;

  @Override
  public void onEnable() {
    long startTime = System.currentTimeMillis();

    // Register important variables
    INSTANCE = this;
    LOGGER = this.getLogger();
    DATA_FOLDER = this.getDataFolder();
    COMPONENT_SERIALIZER = PlainTextComponentSerializer.plainText();
    COMPONENT_DESERIALIZER = MiniMessage.miniMessage();

    // Setup configs
    Lang.reload();
    Config.reload();
    Lang.generateDefaults();
    Config.generateDefaults();

    // Setup classes


    // Register Event Listeners
    getServer().getPluginManager().registerEvents(new Events(), this);

    // Register commands
    List<Command> commandList = PluginCommandYamlParser.parse(INSTANCE);
    for (Command command : commandList) {
      getCommand(command.getName()).setExecutor(new Commands());
      getCommand(command.getName()).setTabCompleter(new Commands());
    }

    getLogger().info(
        "Aurora Combat Loaded in " + Math.subtractExact(System.currentTimeMillis(), startTime) +
            "ms");
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }
}
