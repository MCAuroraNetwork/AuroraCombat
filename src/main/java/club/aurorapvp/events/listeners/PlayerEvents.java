package club.aurorapvp.events.listeners;

import club.aurorapvp.configs.Config;
import club.aurorapvp.modules.BlockFallDamage;
import club.aurorapvp.modules.CombatTag;
import club.aurorapvp.modules.Rating;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;

public class PlayerEvents implements Listener {
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Rating.register(event.getPlayer());
    BlockFallDamage.setInVulnerable(event.getPlayer());

    if (Config.get().getBoolean("combat-tag.enable")) {
      CombatTag.setTaggable(event.getPlayer(), true);
    }
  }

  @EventHandler
  public void onSave(WorldSaveEvent event) {
    Rating.saveAll();
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    Rating.unregister(event.getPlayer());
  }
}
