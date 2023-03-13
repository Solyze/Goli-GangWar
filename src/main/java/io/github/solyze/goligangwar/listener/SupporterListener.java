package io.github.solyze.goligangwar.listener;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.utility.Color;
import io.github.solyze.goligangwar.utility.FileManager;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SupporterListener implements Listener {

    private final GoliGangWar instance;
    private final FileManager supporterBlocksFM;
    private final List<UUID> cooldown;
    private final String prefix;

    public SupporterListener() {
        Bukkit.getPluginManager().registerEvents(this, (instance = GoliGangWar.getInstance()));
        supporterBlocksFM = instance.getSupporterBlocksFM();
        cooldown = new ArrayList<>();
        prefix = GoliGangWar.getPrefix();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        ItemStack current = e.getCurrentItem();
        if (current != null && current.getType() != Material.AIR) if (current.getType() == Material.BANNER) e.setCancelled(true);
        ItemStack cursor = e.getCursor();
        if (cursor != null && cursor.getType() != Material.AIR) if (cursor.getType() == Material.BANNER) e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null || block.getType() == Material.AIR) return;
        SupporterBlock supporterBlock = SupporterBlock.get(block, supporterBlocksFM);
        if (supporterBlock == SupporterBlock.NONE) return;
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        if (cooldown.contains(uuid)) {
            p.sendMessage(Color.process(prefix + "&cPlease wait &e3 seconds &cbetween swapping"));
            p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
            return;
        }
        ItemStack itemStack = supporterBlock.getSupporterBanner();
        p.getInventory().setHelmet(itemStack);
        p.sendMessage(Color.process(prefix + "&7You are now supporting the " + supporterBlock.getTeamDisplayName()));
        p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1.0F, 1.0F);
        cooldown.add(uuid);
        Bukkit.getScheduler().scheduleAsyncDelayedTask(instance, () -> cooldown.remove(uuid), 60L);
    }

    @Getter
    private enum SupporterBlock {
        RED("&cRed Team"), BLUE("&9Blue Team"), NONE(null);

        private final String teamDisplayName;

        SupporterBlock(String teamDisplayName) {
            this.teamDisplayName = teamDisplayName;
        }

        public ItemStack getSupporterBanner() {
            if (this == NONE) return null;
            int data;
            String displayName;
            if (this == RED) {
                data = 1;
                displayName = Color.process("&c&lRed Supporter Banner");
            } else {
                data = 4;
                displayName = Color.process("&9&lBlue Supporter Banner");
            }
            ItemStack itemStack = new ItemStack(Material.BANNER, 1, (short) data);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(displayName);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }

        public static SupporterBlock get(Block block, FileManager supporterBlocksFM) {
            FileConfiguration config = supporterBlocksFM.getConfiguration();
            Location location = block.getLocation();
            World world = location.getWorld();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            World redWorld = Bukkit.getWorld(config.getString("red.world"));
            int redX = config.getInt("red.x");
            int redY = config.getInt("red.y");
            int redZ = config.getInt("red.z");
            if (Objects.equals(world.getName(), redWorld.getName()) && redX == x && redY == y && redZ == z) return RED;
            World blueWorld = Bukkit.getWorld(config.getString("blue.world"));
            int blueY = config.getInt("blue.y");
            int blueX = config.getInt("blue.x");
            int blueZ = config.getInt("blue.z");
            if (Objects.equals(world.getName(), blueWorld.getName()) && blueX == x && blueY == y && blueZ == z) return BLUE;
            return NONE;
        }
    }
}
