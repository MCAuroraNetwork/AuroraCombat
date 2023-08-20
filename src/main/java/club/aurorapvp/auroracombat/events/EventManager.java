package club.aurorapvp.auroracombat.events;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.events.listeners.CombatEventListener;
import club.aurorapvp.auroracombat.events.listeners.DamageEventListener;
import club.aurorapvp.auroracombat.events.listeners.PlayerEventListener;
import org.bukkit.Bukkit;

public class EventManager {

  public static void init() {
    Bukkit.getPluginManager().registerEvents(new CombatEventListener(), AuroraCombat.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new DamageEventListener(), AuroraCombat.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), AuroraCombat.INSTANCE);
  }
}
