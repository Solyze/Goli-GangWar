package io.github.solyze.goligangwar.menu;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.manager.PredictionManager;
import io.github.solyze.goligangwar.utility.Color;
import io.github.solyze.goligangwar.utility.Match;
import io.github.solyze.goligangwar.utility.Prediction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class PredictionMenu implements InventoryHolder {

    private final Inventory inventory;
    private final PredictionManager predictionManager;

    public PredictionMenu(Player player) {
        predictionManager = GoliGangWar.getInstance().getPredictionManager();
        inventory = Bukkit.createInventory(this, 45, Color.process("&lPREDICTION MASTER"));
        ItemStack glassStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9);
        ItemMeta itemMeta = glassStack.getItemMeta();
        itemMeta.setDisplayName(Color.process("&7Decoration"));
        glassStack.setItemMeta(itemMeta);
        for (int i = 0; i < 10; i++) inventory.setItem(i, glassStack);
        inventory.setItem(17, glassStack);
        inventory.setItem(18, glassStack);
        inventory.setItem(26, glassStack);
        inventory.setItem(27, glassStack);
        inventory.setItem(35, glassStack);
        for (int i = 36; i < 45; i++) inventory.setItem(i, glassStack);
        update(player);
    }

    public void update(Player player) {
        inventory.setItem(21, null);
        inventory.setItem(22, null);
        inventory.setItem(23, null);

        Prediction prediction = predictionManager.getActivePrediction();
        if (prediction != null) {
            boolean hasRed = prediction.getRedList().contains(player.getUniqueId());
            String redGang = prediction.getRedGang();
            ItemStack red = new ItemStack(Material.WOOL, 1, (short) 14);
            ItemMeta redMeta = red.getItemMeta();
            if (hasRed) {
                redMeta.addEnchant(Enchantment.DURABILITY, 1, false);
                redMeta.addItemFlags(ItemFlag.values());
            }
            redMeta.setDisplayName(Color.process("&c&lPREDICT THAT " + redGang.toUpperCase() + " WINS"));
            redMeta.setLore(Arrays.asList(
                    "",
                    Color.process("&8▎ &7If &f" + redGang + " &7wins, you will"),
                    Color.process("&8▎ &7get &a+1 &7prediction point."),
                    "",
                    Color.process(hasRed ? "&f➥ &cYou predicted this" : "&f➥ &cClick to predict this")
            ));
            red.setItemMeta(redMeta);
            inventory.setItem(21, red);

            boolean hasBlue = prediction.getBlueList().contains(player.getUniqueId());
            String blueGang = prediction.getBlueGang();
            ItemStack blue = new ItemStack(Material.WOOL, 1, (short) 11);
            ItemMeta blueMeta = blue.getItemMeta();
            if (hasBlue) {
                redMeta.addEnchant(Enchantment.DURABILITY, 1, false);
                redMeta.addItemFlags(ItemFlag.values());
            }
            blueMeta.setDisplayName(Color.process("&9&lPREDICT THAT " + blueGang.toUpperCase() + " WINS"));
            blueMeta.setLore(Arrays.asList(
                    "",
                    Color.process("&8▎ &7If &f" + blueGang + " &7wins, you will"),
                    Color.process("&8▎ &7get &a+1 &7prediction point."),
                    "",
                    Color.process(hasBlue ? "&f➥ &9You predicted this" : "&f➥ &9Click to predict this")
            ));
            blue.setItemMeta(blueMeta);
            inventory.setItem(23, blue);
        } else {
            ItemStack none = new ItemStack(Material.BARRIER, 1);
            ItemMeta meta = none.getItemMeta();
            meta.setDisplayName(Color.process("&c&lNO PREDICTION ACTIVE"));
            meta.setLore(Arrays.asList(
                    "",
                    Color.process("&8▎ &7At least not right now..."),
                    "",
                    Color.process("&f➥ &cCome back later")
            ));
            none.setItemMeta(meta);
            inventory.setItem(22, none);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
