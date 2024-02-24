package club.aurorapvp.auroracombat.events.listeners;

import club.aurorapvp.auroracombat.events.custom.EntityDamagedByEntityEvent;
import club.aurorapvp.auroracombat.modules.PracticeDummy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class DummyCombatEventListener implements Listener {

  @EventHandler
  public void onDummyDamage(EntityDamagedByEntityEvent event) {
    if (!(event.getDamaged() instanceof Zombie zombie)) {
      return;
    }

    if (!(event.getAttacker() instanceof Player)) {
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

  @EventHandler
  public void onDummyAttack(EntityDamagedByEntityEvent event) {
    if (!(event.getAttacker() instanceof Zombie zombie)) {
      return;
    }

    if (!(event.getDamaged() instanceof Player)) {
      return;
    }

    PracticeDummy dummy = PracticeDummy.getDummy(zombie);

    if (dummy == null) {
      return;
    }

    event.setCancelled(true);
  }
}
