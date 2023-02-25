package club.aurorapvp.events;

import club.aurorapvp.AuroraCombat;
import club.aurorapvp.events.listeners.FallDamage;
import club.aurorapvp.events.listeners.Tagger;
import club.aurorapvp.events.listeners.PlayerDamage;
import club.aurorapvp.events.listeners.PlayerJoin;
import org.bukkit.Bukkit;

public class Events {
  public Events() {
    Bukkit.getPluginManager().registerEvents(new Tagger(), AuroraCombat.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new FallDamage(), AuroraCombat.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new PlayerDamage(), AuroraCombat.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new PlayerJoin(), AuroraCombat.INSTANCE);
  }
}