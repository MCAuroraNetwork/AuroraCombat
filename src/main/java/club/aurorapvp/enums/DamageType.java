package club.aurorapvp.enums;

import club.aurorapvp.events.listeners.Player;

public enum DamageType {
  MELEE(Player.attacked, Player.attacker, Player.weapon),
  MAGIC(Player.attacked, Player.attacker, Player.weapon),
  RANGED(Player.attacked, Player.attacker, Player.weapon),
  EXPLOSION(Player.attacked, Player.attacker, Player.weapon);
  private final org.bukkit.entity.Player attacked;
  private final org.bukkit.entity.Player attacker;
  private final Object weapon;

  DamageType(org.bukkit.entity.Player attacked, org.bukkit.entity.Player attacker, Object weapon) {
    this.attacked = attacked;
    this.attacker = attacker;
    this.weapon = weapon;
  }

  public Object getWeapon() {
    return weapon;
  }

  public org.bukkit.entity.Player getAttacked() {
    return attacked;
  }

  public org.bukkit.entity.Player getAttacker() {
    return attacker;
  }
}
