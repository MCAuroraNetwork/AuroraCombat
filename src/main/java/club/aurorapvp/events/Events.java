package club.aurorapvp.events;

import club.aurorapvp.AuroraCombat;
import club.aurorapvp.events.listeners.PlayerDamageEvents;
import club.aurorapvp.events.listeners.CombatTagEvents;
import club.aurorapvp.events.listeners.JoinEvent;
import club.aurorapvp.modules.DeathMessage;
import org.bukkit.Bukkit;

public class Events {
  public static void init() {
    Bukkit.getPluginManager().registerEvents(new CombatTagEvents(), AuroraCombat.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new PlayerDamageEvents(), AuroraCombat.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new JoinEvent(), AuroraCombat.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new DeathMessage(), AuroraCombat.INSTANCE);
  }
}