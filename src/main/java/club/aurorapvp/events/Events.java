package club.aurorapvp.events;

import club.aurorapvp.AuroraCombat;
import club.aurorapvp.events.listeners.Tagger;
import club.aurorapvp.events.listeners.PlayerDamage;
import club.aurorapvp.events.listeners.PlayerJoin;
import org.bukkit.Bukkit;

public class Events {
  public Events() {
    Bukkit.getServer().getPluginManager().registerEvents(new Tagger(), AuroraCombat.INSTANCE);
    Bukkit.getServer().getPluginManager().registerEvents(new PlayerDamage(), AuroraCombat.INSTANCE);
    Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoin(), AuroraCombat.INSTANCE);
  }
}
