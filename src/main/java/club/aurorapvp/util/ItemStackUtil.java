package club.aurorapvp.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

public class ItemStackUtil {
  public static ItemStack toItemStack(EnderCrystal enderCrystal) {
    Component displayName = enderCrystal.name();

    ItemStack item = new ItemStack(Material.END_CRYSTAL);
    ItemMeta meta = item.getItemMeta();

    meta.displayName(displayName);

    item.setItemMeta(meta);

    return item;
  }

  public static ItemStack toItemStack(Projectile projectile) {
    ItemStack item;
    Component displayName = projectile.name();

    if (projectile instanceof Arrow) {
      Arrow arrow = (Arrow) projectile;
      if (!arrow.getCustomEffects().isEmpty()) {
        item = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta potionMeta = (PotionMeta) item.getItemMeta();

        for (PotionEffect effect : arrow.getCustomEffects()) {
          potionMeta.addCustomEffect(effect, true);
        }

        item.setItemMeta(potionMeta);
      } else if (projectile instanceof SpectralArrow) {
        item = new ItemStack(Material.SPECTRAL_ARROW);
      } else {
        item = new ItemStack(Material.ARROW);
      }
    } else {
      switch (projectile.getType()) {
        case SNOWBALL:
          item = new ItemStack(Material.SNOWBALL);
          break;
        case EGG:
          item = new ItemStack(Material.EGG);
          break;
        case ENDER_PEARL:
          item = new ItemStack(Material.ENDER_PEARL);
          break;
        case TRIDENT:
          item = new ItemStack(Material.TRIDENT);
          break;
        default:
          throw new IllegalArgumentException(
              "Unsupported projectile type: " + projectile.getType());
      }
    }

    ItemMeta meta = item.getItemMeta();

    meta.displayName(displayName);

    item.setItemMeta(meta);

    return item;
  }
}
