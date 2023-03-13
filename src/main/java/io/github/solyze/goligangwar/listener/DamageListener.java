package io.github.solyze.goligangwar.listener;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.manager.GangManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageListener implements Listener {

    private final GangManager gangManager;

    public DamageListener() {
        Bukkit.getPluginManager().registerEvents(this, GoliGangWar.getInstance());
        gangManager = GoliGangWar.getInstance().getGangManager();
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity en = e.getEntity();
        Entity damEN = e.getDamager();
        if (!(en instanceof Player) || !(damEN instanceof Player)) return;
        Player p = (Player) en;
        Player d = (Player) damEN;
        String pGang = gangManager.getGang(p.getUniqueId());
        if (pGang == null) return;
        String dGang = gangManager.getGang(d.getUniqueId());
        if (dGang == null) return;
        if (pGang.equals(dGang)) e.setCancelled(true);
    }
}
