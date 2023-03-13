package io.github.solyze.goligangwar.manager;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.utility.Color;
import io.github.solyze.goligangwar.utility.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SpawnManager {

    private final FileManager spawnsFM;

    public SpawnManager() {
        spawnsFM = GoliGangWar.getInstance().getSpawnsFM();
    }

    public Location getRedSpawn() {
        FileConfiguration config = spawnsFM.getConfiguration();
        if (!config.contains("red")) return null;
        World world = Bukkit.getWorld(config.getString("red.world"));
        double x = config.getDouble("red.x");
        double y = config.getDouble("red.y");
        double z = config.getDouble("red.z");
        float yaw = (float) config.getDouble("red.yaw");
        float pitch = (float) config.getDouble("red.pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    public void setRedSpawn(Player player) {
        FileConfiguration config = spawnsFM.getConfiguration();
        Location location = player.getLocation();
        config.set("red.world", location.getWorld().getName());
        config.set("red.x", location.getX());
        config.set("red.y", location.getY());
        config.set("red.z", location.getZ());
        config.set("red.yaw", location.getYaw());
        config.set("red.pitch", location.getPitch());
        spawnsFM.save();
        player.sendMessage(Color.process(GoliGangWar.getPrefix() + "&7Spawn location for the &cRed Team &7updated"));
        Command.broadcastCommandMessage(player, Color.process("Spawn location for the &c&oRed Team &7&oupdated"), false);
    }

    public Location getBlueSpawn() {
        FileConfiguration config = spawnsFM.getConfiguration();
        if (!config.contains("blue")) return null;
        World world = Bukkit.getWorld(config.getString("blue.world"));
        double x = config.getDouble("blue.x");
        double y = config.getDouble("blue.y");
        double z = config.getDouble("blue.z");
        float yaw = (float) config.getDouble("blue.yaw");
        float pitch = (float) config.getDouble("blue.pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    public void setBlueSpawn(Player player) {
        FileConfiguration config = spawnsFM.getConfiguration();
        Location location = player.getLocation();
        config.set("blue.world", location.getWorld().getName());
        config.set("blue.x", location.getX());
        config.set("blue.y", location.getY());
        config.set("blue.z", location.getZ());
        config.set("blue.yaw", location.getYaw());
        config.set("blue.pitch", location.getPitch());
        spawnsFM.save();
        player.sendMessage(Color.process(GoliGangWar.getPrefix() + "&7Spawn location for the &9Blue Team &7updated"));
        Command.broadcastCommandMessage(player, Color.process("Spawn location for the &9&oBlue Team &7&oupdated"), false);
    }

    public Location getMainSpawn() {
        FileConfiguration config = spawnsFM.getConfiguration();
        if (!config.contains("main")) return null;
        World world = Bukkit.getWorld(config.getString("main.world"));
        double x = config.getDouble("main.x");
        double y = config.getDouble("main.y");
        double z = config.getDouble("main.z");
        float yaw = (float) config.getDouble("main.yaw");
        float pitch = (float) config.getDouble("main.pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    public void setMainSpawn(Player player) {
        FileConfiguration config = spawnsFM.getConfiguration();
        Location location = player.getLocation();
        config.set("main.world", location.getWorld().getName());
        config.set("main.x", location.getX());
        config.set("main.y", location.getY());
        config.set("main.z", location.getZ());
        config.set("main.yaw", location.getYaw());
        config.set("main.pitch", location.getPitch());
        spawnsFM.save();
        player.sendMessage(Color.process(GoliGangWar.getPrefix() + "&7Main spawn location updated"));
        Command.broadcastCommandMessage(player, Color.process("Main spawn location updated"), false);
    }
}
