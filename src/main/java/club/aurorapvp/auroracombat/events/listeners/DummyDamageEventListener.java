package club.aurorapvp.auroracombat.events.listeners;

import club.aurorapvp.auroracombat.events.custom.EntityDamagedByPlayerEvent;
import club.aurorapvp.auroracombat.modules.PracticeDummy;
import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class DummyDamageEventListener implements Listener {

  @EventHandler
  public void onDummyDamage(EntityDamagedByPlayerEvent event) {
    if (!(event.getDamaged() instanceof Zombie zombie)) {
      return;
    }

    PracticeDummy dummy = PracticeDummy.getDummy(zombie);

    if (dummy == null) {
      return;
    }

    if (zombie.getEquipment().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {
      return;
    }

    if (dummy.getInventory().contains(Material.TOTEM_OF_UNDYING)) {
      dummy.getInventory().remove(Material.TOTEM_OF_UNDYING);

      dummy.getZombie().getEquipment().setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING));
    }
  }
}
