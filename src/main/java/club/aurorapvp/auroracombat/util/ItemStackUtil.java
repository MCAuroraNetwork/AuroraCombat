package club.aurorapvp.auroracombat.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackUtil {

  public static ItemStack toItemStack(EnderCrystal enderCrystal) {
    Component displayName = enderCrystal.customName();

    ItemStack item = new ItemStack(Material.END_CRYSTAL);
    ItemMeta meta = item.getItemMeta();

    meta.displayName(displayName);

    item.setItemMeta(meta);

    return item;
  }

  public static ItemStack toItemStack(Projectile projectile) {
    ItemStack item;
    Component displayName = projectile.customName();
    assert displayName != null;
    displayName = displayName.colorIfAbsent(NamedTextColor.AQUA);

    if (projectile instanceof Arrow) {
      item = new ItemStack(Material.ARROW);
    } else if (projectile instanceof SpectralArrow) {
      item = new ItemStack(Material.SPECTRAL_ARROW);
    } else if (projectile instanceof ThrownPotion thrownPotion) {
      return thrownPotion.getItem();
    } else {
      item =
          switch (projectile.getType()) {
            case SNOWBALL -> new ItemStack(Material.SNOWBALL);
            case EGG -> new ItemStack(Material.EGG);
            case ENDER_PEARL -> new ItemStack(Material.ENDER_PEARL);
            case TRIDENT -> new ItemStack(Material.TRIDENT);
            default -> throw new IllegalArgumentException(
                "Unsupported projectile type: " + projectile.getType());
          };
    }

    ItemMeta meta = item.getItemMeta();

    meta.displayName(displayName);

    item.setItemMeta(meta);

    return item;
  }
}
