package io.github.solyze.goligangwar.listener;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.utility.Color;
import io.github.solyze.goligangwar.utility.DefaultFontInfo;
import io.github.solyze.goligangwar.utility.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class JoinListener implements Listener {

    private final FileManager configFM;

    public JoinListener() {
        GoliGangWar instance;
        Bukkit.getPluginManager().registerEvents(this, instance = GoliGangWar.getInstance());
        configFM = instance.getConfigFM();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        e.setJoinMessage(null);
        Bukkit.dispatchCommand(p, "lp log notify off");
        Bukkit.getScheduler().scheduleSyncDelayedTask(GoliGangWar.getInstance(), () -> {
            FileConfiguration config = configFM.getConfiguration();
            List<String> joinMOTD = config.getStringList("join-motd");
            p.setGameMode(GameMode.ADVENTURE);
            p.getInventory().clear();
            p.getInventory().setHelmet(null);
            p.getInventory().setChestplate(null);
            p.getInventory().setLeggings(null);
            p.getInventory().setBoots(null);
            for (String line : joinMOTD) {
                line = Color.process(line.replace("{player}", p.getName()));
                if (line.contains("[CENTER]")) line = DefaultFontInfo.getCenteredMessage(line.replace("[CENTER]", ""));
                p.sendMessage(line);
            }
        }, 1L);
    }
}
