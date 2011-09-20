package net.gamesketch.advancedpvp.listeners;

import net.gamesketch.advancedpvp.AdvancedPVP;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class PlayerAttackListener extends EntityListener {
    private AdvancedPVP plugin;

    public PlayerAttackListener(AdvancedPVP advancedPVP) {
        plugin = advancedPVP;
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
            return;
        }
        Player damagee = (Player) event.getEntity();
        Player damager = null; // register non-player damage with damager set to null
        if (event instanceof EntityDamageByEntityEvent) {
            Entity damagerEntity = ((EntityDamageByEntityEvent) event).getDamager();
            if (damagerEntity instanceof Projectile) {
                damagerEntity = ((Projectile) damagerEntity).getShooter();
            }
            if (damagerEntity instanceof Player ) {
                damager = (Player) damagerEntity;
                event.setCancelled(plugin.isAttackBlocked(damagee, damager));
            }
        }
        if (!event.isCancelled()) {
            plugin.registerDamage(damagee, damager, event.getDamage());
        }
    }

    @Override
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            plugin.registerDeath((Player) event.getEntity());
        }
    }
}
