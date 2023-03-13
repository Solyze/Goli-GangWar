package io.github.solyze.goligangwar.manager;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.utility.Color;
import io.github.solyze.goligangwar.utility.FileManager;
import io.github.solyze.goligangwar.utility.InventoryUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class KitManager {

    private final FileManager kitFM;
    private final String prefix;

    public KitManager() {
        kitFM = GoliGangWar.getInstance().getKitFM();
        prefix = GoliGangWar.getPrefix();
    }

    public void loadKit(CommandSender sender, Player player) {
        try {
            FileConfiguration config = kitFM.getConfiguration();
            String inventoryBase64 = config.getString("inventoryBase64");
            ItemStack[] inventoryContents = InventoryUtils.itemStackArrayFromBase64(inventoryBase64);
            player.getInventory().setContents(inventoryContents);
            String armorBase64 = config.getString("armorBase64");
            ItemStack[] armorContents = InventoryUtils.itemStackArrayFromBase64(armorBase64);
            player.getInventory().setArmorContents(armorContents);
            if (sender != null) {
                String playerName = player.getName();
                if (sender.getName().equals(playerName)) {
                    sender.sendMessage(Color.process(prefix + "&7You have been given the GangWar kit"));
                    Command.broadcastCommandMessage(sender, Color.process("&7&oYou have been given the GangWar kit"), false);
                } else {
                    sender.sendMessage(Color.process(prefix + "&b" + playerName + " &7has been given the GangWar kit"));
                    Command.broadcastCommandMessage(sender, Color.process("&b&o" + playerName + " &7&ohas been given the GangWar kit"), false);
                }
            }
        } catch (Exception ignored) {
            String playerName = player.getName();
            GoliGangWar.getInstance().getLogger().severe("An error occurred whilst trying to load the GangWar kit for " + playerName);
            if (sender != null) {
                if (sender.getName().equals(playerName)) {
                    sender.sendMessage(Color.process(prefix + "&cAn error occurred whilst trying to load the GangWar kit"));
                    Command.broadcastCommandMessage(sender, Color.process("&c&oAn error occurred whilst trying to load the GangWar kit&7&o"), false);
                } else {
                    sender.sendMessage(Color.process(prefix + "&cAn error occurred whilst trying to load the GangWar kit for &4" + playerName));
                    Command.broadcastCommandMessage(sender, Color.process("&c&oAn error occurred whilst trying to load the GangWar kit for &4&o" + playerName + "&7&o"), false);
                }
            }
        }
    }

    public void saveKit(Player player) {
        try {
            FileConfiguration config = kitFM.getConfiguration();
            PlayerInventory playerInventory = player.getInventory();
            ItemStack[] inventoryContents = playerInventory.getContents();
            String inventoryBase64 = InventoryUtils.itemStackArrayToBase64(inventoryContents);
            config.set("inventoryBase64", inventoryBase64);
            ItemStack[] armorContents = playerInventory.getArmorContents();
            String armorBase64 = InventoryUtils.itemStackArrayToBase64(armorContents);
            config.set("armorBase64", armorBase64);
            kitFM.save();
            player.sendMessage(Color.process(prefix + "&7You have saved the GangWar kit"));
            Command.broadcastCommandMessage(player, Color.process("&7&oYou have saved the GangWar kit"), false);
        } catch (Exception ignored) {
            String playerName = player.getName();
            GoliGangWar.getInstance().getLogger().severe("An error occurred whilst trying to save the GangWar kit from " + playerName);
        }
    }
}
