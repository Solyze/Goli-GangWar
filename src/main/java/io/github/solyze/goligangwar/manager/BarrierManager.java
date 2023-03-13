package io.github.solyze.goligangwar.manager;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.utility.FileManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BarrierManager {

    private final FileManager barriersFM;

    public BarrierManager() {
        barriersFM = GoliGangWar.getInstance().getBarriersFM();
    }

    public void showRedBarriers() {
        showBarriers("red");
    }

    public void showBlueBarriers() {
        showBarriers("blue");
    }

    public void hideRedBarriers() {
        hideBarriers("red");
    }

    public void hideBlueBarriers() {
        hideBarriers("blue");
    }

    private void showBarriers(String team) {
        DyeColor dyeColor = DyeColor.WHITE;
        if (team.equals("red")) dyeColor = DyeColor.RED;
        if (team.equals("blue")) dyeColor = DyeColor.BLUE;
        for (Block block : getBarrierBlocks(team)) {
            block.setType(Material.STAINED_GLASS);
            block.setData(dyeColor.getWoolData());
        }
    }

    private void hideBarriers(String team) {
        for (Block block : getBarrierBlocks(team)) {
            block.setType(Material.AIR);
        }
    }

    private List<Block> getBarrierBlocks(String team) {
        List<Block> blocks = new ArrayList<>();
        FileConfiguration config = barriersFM.getConfiguration();
        ConfigurationSection section = config.getConfigurationSection(team);
        if (section == null) return blocks;
        Set<String> keys = section.getKeys(false);
        if (keys.isEmpty()) return blocks;
        for (String key : keys) {
            World world = Bukkit.getWorld(config.getString(team + "." + key + ".world"));
            int x = config.getInt(team + "." + key + ".x");
            int y = config.getInt(team + "." + key + ".y");
            int z = config.getInt(team + "." + key + ".z");
            Location location = new Location(world, x, y, z);
            blocks.add(location.getBlock());
        }
        return blocks;
    }
}
