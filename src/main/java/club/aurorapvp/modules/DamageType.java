package club.aurorapvp.modules;

import club.aurorapvp.events.listeners.PlayerDamage;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

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

  public Class<?> getWeaponType() {
    if (weapon instanceof ItemStack) {
      return ItemStack.class;
    } else if (weapon instanceof EnderCrystal) {
      return EnderCrystal.class;
    } else if (weapon instanceof Arrow) {
      return Arrow.class;
    } else if (weapon instanceof RespawnAnchor) {
      return RespawnAnchor.class;
    } else if (weapon instanceof Bed) {
      return Bed.class;
    } else if (weapon instanceof PotionEffect) {
      return PotionEffect.class;
    } else {
      return null;
    }
  }

  public Player getAttacked() {
    return attacked;
  }

  public Player getAttacker() {
    return attacker;
  }
}
