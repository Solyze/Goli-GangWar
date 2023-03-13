package io.github.solyze.goligangwar.listener;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.manager.PredictionManager;
import io.github.solyze.goligangwar.menu.PredictionMenu;
import io.github.solyze.goligangwar.utility.Prediction;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class MenuListener implements Listener {

    private final PredictionManager predictionManager;

    public MenuListener() {
        GoliGangWar instance;
        Bukkit.getPluginManager().registerEvents(this, instance = GoliGangWar.getInstance());
        predictionManager = instance.getPredictionManager();
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();
        if (itemStack == null) return;
        Inventory inventory = p.getOpenInventory().getTopInventory();
        InventoryHolder holder = inventory.getHolder();
        if (holder == null) return;

        if (holder instanceof PredictionMenu) {
            e.setCancelled(true);
            int slot = e.getSlot();
            switch (slot) {
                case 21: // Red
                    Prediction prediction = predictionManager.getActivePrediction();
                    predictionManager.predict(p, prediction.getRedGang());
                    p.openInventory(new PredictionMenu(p).getInventory());
                    return;
                case 22: // None
                    p.closeInventory();
                    return;
                case 23: // Blue
                    Prediction prediction1 = predictionManager.getActivePrediction();
                    predictionManager.predict(p, prediction1.getBlueGang());
                    p.openInventory(new PredictionMenu(p).getInventory());
                    return;
            }
        }
    }
}
