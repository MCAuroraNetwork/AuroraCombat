package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class PracticeDummy {
  private static final Map<UUID, Zombie> dummies = new HashMap<>();
  public static void summonDummy(Player player) {
    Zombie zombie = (Zombie) player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);

    zombie.setAI(false);

    zombie.setHealth(player.getHealth());
    zombie.addPotionEffects(player.getActivePotionEffects());

    zombie.getEquipment().setHelmet(player.getInventory().getHelmet());
    zombie.getEquipment().setChestplate(player.getInventory().getChestplate());
    zombie.getEquipment().setLeggings(player.getInventory().getLeggings());
    zombie.getEquipment().setBoots(player.getInventory().getBoots());
    zombie.getEquipment().setItemInOffHand(player.getInventory().getItemInOffHand());
    zombie.getEquipment().setItemInMainHand(player.getInventory().getItemInMainHand());

    if (dummies.containsKey(player.getUniqueId())) {
      dummies.get(player.getUniqueId()).remove();
    }

    dummies.put(player.getUniqueId(), zombie);

    player.sendMessage(AuroraCombat.getInstance().getLang().getComponent("dummy-summoned"));
  }
}
