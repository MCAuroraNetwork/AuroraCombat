package club.aurorapvp.auroracombat.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackUtil {

  public static ItemStack makeItemUnbreakable(ItemStack itemStack) {
    if (itemStack == null) {
      return null;
    }

    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setUnbreakable(true);
    itemStack.setItemMeta(itemMeta);

    return itemStack;
  }


  public static ItemStack toItemStack(Projectile projectile) {
    ItemStack item;

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

    Component displayName = projectile.customName();

    if (displayName != null) {
      ItemMeta meta = item.getItemMeta();

      meta.displayName(displayName);

      item.setItemMeta(meta);
    }

    return item;
  }
}
