package club.aurorapvp.events;

import club.aurorapvp.AuroraCombat;
import club.aurorapvp.events.listeners.CombatTag;
import club.aurorapvp.events.listeners.Damage;
import club.aurorapvp.events.listeners.PlayerJoin;
import org.bukkit.Bukkit;

public class Events {
  public Events() {
    Bukkit.getServer().getPluginManager().registerEvents(new CombatTag(), AuroraCombat.INSTANCE);
    Bukkit.getServer().getPluginManager().registerEvents(new Damage(), AuroraCombat.INSTANCE);
    Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoin(), AuroraCombat.INSTANCE);
  }
}
