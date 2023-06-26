package club.aurorapvp.auroracombat.events;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.events.listeners.PlayerDamageEventListener;
import club.aurorapvp.auroracombat.events.listeners.CombatTagEventListener;
import club.aurorapvp.auroracombat.events.listeners.PlayerEventListener;
import org.bukkit.Bukkit;

public class EventManager {
  public static void init() {
    Bukkit.getPluginManager().registerEvents(new CombatTagEventListener(), AuroraCombat.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new PlayerDamageEventListener(), AuroraCombat.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), AuroraCombat.INSTANCE);
  }
}