package club.aurorapvp.auroracombat.events.listeners;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.modules.BlockFallDamage;
import club.aurorapvp.auroracombat.modules.CombatTag;
import club.aurorapvp.auroracombat.modules.KillDeathTracker;
import club.aurorapvp.auroracombat.modules.Rating;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;

public class PlayerEventListener implements Listener {

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    if (AuroraCombat.getInstance().getConfig().getBoolean("combat-tag.enable")) {
      CombatTag.setTaggable(event.getPlayer(), true);
    }
  }

  @EventHandler
  public void onWorldChange(PlayerChangedWorldEvent event) {
    BlockFallDamage.setInvulnerable(event.getPlayer());
  }

  @EventHandler
  public void onSave(WorldSaveEvent event) {
    KillDeathTracker.saveAll();
    Rating.saveAll();
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    Rating.unregister(event.getPlayer());
    BlockFallDamage.unregister(event.getPlayer());
  }
}
