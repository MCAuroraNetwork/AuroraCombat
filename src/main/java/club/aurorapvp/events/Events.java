package club.aurorapvp.events;

import club.aurorapvp.AuroraCombat;
import club.aurorapvp.events.listeners.FallDamage;
import club.aurorapvp.events.listeners.PlayerDamage;
import club.aurorapvp.events.listeners.CombatTags;
import club.aurorapvp.events.listeners.PlayerJoin;
import club.aurorapvp.modules.DeathMessage;
import org.bukkit.Bukkit;

public class Events {
  public Events() {
    Bukkit.getPluginManager().registerEvents(new CombatTags(), AuroraCombat.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new FallDamage(), AuroraCombat.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new PlayerDamage(), AuroraCombat.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new PlayerJoin(), AuroraCombat.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new DeathMessage(), AuroraCombat.INSTANCE);
  }
}