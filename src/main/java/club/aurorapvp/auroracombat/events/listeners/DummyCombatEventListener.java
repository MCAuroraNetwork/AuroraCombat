package club.aurorapvp.auroracombat.events.listeners;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.events.custom.CombatTagEvent;
import club.aurorapvp.auroracombat.events.custom.EntityDamagedByEntityEvent;
import club.aurorapvp.auroracombat.modules.PracticeDummy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class DummyCombatEventListener implements Listener {

  @EventHandler
  public void onDummyDamage(EntityResurrectEvent event) {
    if (!(event.getEntity() instanceof Zombie zombie)) {
      return;
    }

    PracticeDummy dummy = PracticeDummy.getDummy(zombie);

    if (dummy == null) {
      return;
    }

    new BukkitRunnable() {
      @Override
      public void run() {
        if (dummy.getInventory().contains(Material.TOTEM_OF_UNDYING)) {
          dummy.getInventory().removeItem(new ItemStack(Material.TOTEM_OF_UNDYING));

          dummy.getZombie().getEquipment().setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING));
        }
      }
    }.runTaskLater(AuroraCombat.getInstance(), 1L);
  }

  @EventHandler
  public void onCombatTag(CombatTagEvent event) {
    PracticeDummy.getDummy(event.getTagged()).getZombie().remove();
    PracticeDummy.getDummy(event.getTagger()).getZombie().remove();
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
