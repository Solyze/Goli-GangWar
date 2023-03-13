package io.github.solyze.goligangwar.listener;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.event.GWDeathEvent;
import io.github.solyze.goligangwar.event.GWMatchRoundEndEvent;
import io.github.solyze.goligangwar.manager.MatchManager;
import io.github.solyze.goligangwar.utility.Color;
import io.github.solyze.goligangwar.utility.Match;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.UUID;

public class DeathQuitListener implements Listener {

    private final MatchManager matchManager;
    private final String prefix;

    public DeathQuitListener() {
        GoliGangWar instance;
        Bukkit.getPluginManager().registerEvents(this, instance = GoliGangWar.getInstance());
        matchManager = instance.getMatchManager();
        prefix = GoliGangWar.getPrefix();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.setDeathMessage(null);
        Player p = e.getEntity();
        e.setKeepInventory(true);
        Player k = p.getKiller();
        p.setVelocity(new Vector());
        p.spigot().respawn();
        new GWDeathEvent(p, k, false);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        e.setQuitMessage(null);
        p.getInventory().clear();
        p.getInventory().setHelmet(null);
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setBoots(null);
        p.setFireTicks(0);
        p.setHealth(20);
        Match match = matchManager.getActiveMatch();
        if (match != null) {
            if (match.getAllParticipants().contains(p.getUniqueId())) {
                if (!match.isDead(p)) {
                    p.getLocation().getWorld().strikeLightningEffect(p.getLocation());
                    new GWDeathEvent(p, p, true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onGWDeath(GWDeathEvent e) {
        Player p = e.getPlayer();
        p.getInventory().clear();
        p.getInventory().setHelmet(null);
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setBoots(null);
        for (PotionEffect pe : p.getActivePotionEffects()) {
            p.removePotionEffect(pe.getType());
        }
        if (e.isDisconnect()) {
            Bukkit.broadcastMessage(Color.process(prefix + getColor(p) + p.getName() + " &7disconnected"));
        } else {
            Player k = e.getKiller();
            if (k == null) {
                Bukkit.broadcastMessage(Color.process(prefix + getColor(p) + p.getName() + " &7died"));
            } else {
                Bukkit.broadcastMessage(Color.process(prefix + getColor(p) + p.getName() + " &7was killed by " + getColor(k) + k.getName()));
            }
        }
        Match match = matchManager.getActiveMatch();
        UUID uuid = p.getUniqueId();
        if (match != null && match.getAllParticipants().contains(uuid)) {
            match.addDead(p);
            if (match.isOnRedTeam(p) && (match.getAmountOfRedPlayers() == match.getAmountOfDeadPlayers(match.getRedGang().getValue()))) {
            //if (match.isOnRedTeam(p) && (match.getDead().size() == match.getAmountOfDeadPlayers(match.getRedGang().getValue()))) {
                // All red players are dead, blue wins the round
                new GWMatchRoundEndEvent(match.getBlueGang().getKey(), match.getRedGang().getKey(), match.getRound());
            }
            if (match.isOnBlueTeam(p) && (match.getAmountOfBluePlayers() == match.getAmountOfDeadPlayers(match.getBlueGang().getValue()))) {
            //if (match.isOnBlueTeam(p) && (match.getDead().size() == match.getAmountOfDeadPlayers(match.getBlueGang().getValue()))) {
                // All blue players are dead, red wins the round
                new GWMatchRoundEndEvent(match.getRedGang().getKey(), match.getBlueGang().getKey(), match.getRound());
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Match match = matchManager.getActiveMatch();
        if (match == null) return;
        Player player = e.getPlayer();
        if (match.getDead().contains(player.getUniqueId())) e.setCancelled(true);
    }

    private ChatColor getColor(Player p) {
        ChatColor color = ChatColor.WHITE;
        Match match = matchManager.getActiveMatch();
        if (match == null) return color;
        if (match.isOnBlueTeam(p)) color = ChatColor.BLUE;
        if (match.isOnRedTeam(p)) color = ChatColor.RED;
        return color;
    }
}