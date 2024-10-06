package club.aurorapvp.auroracombat.events.listeners;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.modules.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class PlayerEventListener implements Listener {

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    if (AuroraCombat.getInstance().getConfig().getBoolean("combat-tag.enable")) {
      CombatTag.setTaggable(event.getPlayer(), true);
    }

    BlockFallDamage.setInvulnerable(event.getPlayer());
    Rating.register(event.getPlayer());
    KillDeathTracker.register(event.getPlayer());
    CombatTag.register(event.getPlayer());
  }

  @EventHandler
  public void onRespawn(PlayerRespawnEvent event) {
    BlockFallDamage.setInvulnerable(event.getPlayer());
  }

  @EventHandler
  public void onWorldChange(PlayerChangedWorldEvent event) {
    BlockFallDamage.setInvulnerable(event.getPlayer());
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    Rating.unregister(event.getPlayer());
    KillDeathTracker.unregister(event.getPlayer());
    BlockFallDamage.unregister(event.getPlayer());
    CombatTag.unregister(event.getPlayer());
  }
}
