package club.aurorapvp.auroracombat.events;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.events.listeners.CombatEventListener;
import club.aurorapvp.auroracombat.events.listeners.DamageEventListener;
import club.aurorapvp.auroracombat.events.listeners.DummyCombatEventListener;
import club.aurorapvp.auroracombat.events.listeners.PlayerEventListener;
import club.aurorapvp.auroracombat.events.listeners.ProjectileListener;
import org.bukkit.Bukkit;

public class EventManager {

  public static void init() {
    Bukkit.getPluginManager().registerEvents(new CombatEventListener(), AuroraCombat.getInstance());
    Bukkit.getPluginManager().registerEvents(new DamageEventListener(), AuroraCombat.getInstance());
    Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), AuroraCombat.getInstance());
    Bukkit.getPluginManager().registerEvents(new ProjectileListener(), AuroraCombat.getInstance());
    Bukkit.getPluginManager().registerEvents(new DummyCombatEventListener(), AuroraCombat.getInstance());
  }
}
