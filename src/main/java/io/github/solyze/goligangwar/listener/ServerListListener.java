package io.github.solyze.goligangwar.listener;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.utility.Color;
import io.github.solyze.goligangwar.utility.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListListener implements Listener {

    private final FileManager configFM;

    public ServerListListener() {
        GoliGangWar instance;
        Bukkit.getPluginManager().registerEvents(this, instance = GoliGangWar.getInstance());
        configFM = instance.getConfigFM();
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent e) {
        FileConfiguration configuration = configFM.getConfiguration();
        e.setMotd(Color.process(configuration.getString("server-list.motd").replace("%n", "\n")));
        e.setMaxPlayers(configuration.getInt("server-list.max-players"));
    }
}