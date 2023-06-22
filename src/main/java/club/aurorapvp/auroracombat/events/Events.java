package club.aurorapvp.auroracombat.events;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.events.listeners.PlayerDamageEvents;
import club.aurorapvp.auroracombat.events.listeners.CombatTagEvents;
import club.aurorapvp.auroracombat.events.listeners.PlayerEvents;
import org.bukkit.Bukkit;

public class Events {
  public static void init() {
    Bukkit.getPluginManager().registerEvents(new CombatTagEvents(), AuroraCombat.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new PlayerDamageEvents(), AuroraCombat.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new PlayerEvents(), AuroraCombat.INSTANCE);
  }
}