package club.aurorapvp.enums;

import club.aurorapvp.events.listeners.PlayerDamage;
import org.bukkit.entity.Player;

public enum DamageType {
  MELEE(PlayerDamage.attacked, PlayerDamage.attacker, PlayerDamage.weapon),
  MAGIC(PlayerDamage.attacked, PlayerDamage.attacker, PlayerDamage.weapon),
  RANGED(PlayerDamage.attacked, PlayerDamage.attacker, PlayerDamage.weapon),
  EXPLOSION(PlayerDamage.attacked, PlayerDamage.attacker, PlayerDamage.weapon);
  private final Player attacked;
  private final Player attacker;
  private final Object weapon;

  DamageType(Player attacked, Player attacker, Object weapon) {
    this.attacked = attacked;
    this.attacker = attacker;
    this.weapon = weapon;
  }

  public Object getWeapon() {
    return weapon;
  }

  public Player getAttacked() {
    return attacked;
  }

  public Player getAttacker() {
    return attacker;
  }
}
