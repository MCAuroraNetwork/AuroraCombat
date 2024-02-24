package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.util.ItemStackUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.PlayerInventory;

public class PracticeDummy {

  private static final Map<UUID, PracticeDummy> DUMMIES = new HashMap<>();
  private final Player player;
  private final Zombie zombie;
  private PlayerInventory inventory;

  public PracticeDummy(Player player) {
    this.player = player;
    this.zombie = (Zombie) player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);

    if (DUMMIES.containsKey(player.getUniqueId())) {
      PracticeDummy dummy = DUMMIES.get(player.getUniqueId());

      dummy.getZombie().remove();
    }

    DUMMIES.put(player.getUniqueId(), this);
  }

  public void summonDummy() {
    zombie.setHealth(player.getHealth());
    zombie.addPotionEffects(player.getActivePotionEffects());

    inventory = player.getInventory();

    zombie.getEquipment().setHelmet(ItemStackUtil.makeItemUnbreakable(inventory.getHelmet()));
    zombie.getEquipment().setChestplate(
        ItemStackUtil.makeItemUnbreakable(inventory.getChestplate()));
    zombie.getEquipment().setLeggings(
        ItemStackUtil.makeItemUnbreakable(inventory.getLeggings()));
    zombie.getEquipment().setBoots(
        ItemStackUtil.makeItemUnbreakable(inventory.getBoots()));
    zombie.getEquipment().setItemInOffHand(inventory.getItemInOffHand());
    zombie.getEquipment().setItemInMainHand(inventory.getItemInMainHand());

    player.sendMessage(AuroraCombat.getInstance().getLang().getComponent("dummy-summoned"));
  }

  public Zombie getZombie() {
    return zombie;
  }

  public PlayerInventory getInventory() {
    return inventory;
  }

  public static PracticeDummy getDummy(Zombie zombie) {
    for (PracticeDummy dummy : DUMMIES.values()) {
      if (dummy.getZombie() == zombie) {
        return dummy;
      }
    }

    return null;
  }
}
