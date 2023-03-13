package io.github.solyze.goligangwar.listener;

import com.google.common.collect.Sets;
import io.github.solyze.goligangwar.GoliGangWar;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Set;

public class SpawnListener implements Listener {

    private static final Set<EntityType> WHITELISTED_ENTITY_TYPES = Sets.immutableEnumSet(
            EntityType.ARMOR_STAND,
            EntityType.ARROW,
            EntityType.DROPPED_ITEM,
            EntityType.ITEM_FRAME,
            EntityType.PLAYER
    );

    public SpawnListener() {
        Bukkit.getPluginManager().registerEvents(this, GoliGangWar.getInstance());
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        if (WHITELISTED_ENTITY_TYPES.contains(e.getEntityType())) return;
        e.setCancelled(true);
    }
}