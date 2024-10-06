package club.aurorapvp.auroracombat.events;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.events.listeners.*;
import org.bukkit.Bukkit;

public class EventManager {

  public static void init() {
    Bukkit.getPluginManager().registerEvents(new CombatEventListener(), AuroraCombat.getInstance());
    Bukkit.getPluginManager().registerEvents(new DamageEventListener(), AuroraCombat.getInstance());
    Bukkit.getPluginManager().registerEvents(new PhaseListener(), AuroraCombat.getInstance());
    Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), AuroraCombat.getInstance());
    Bukkit.getPluginManager().registerEvents(new ProjectileListener(), AuroraCombat.getInstance());
    Bukkit.getPluginManager().registerEvents(new DummyCombatEventListener(), AuroraCombat.getInstance());
  }
}
