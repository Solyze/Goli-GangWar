package io.github.solyze.goligangwar.listener;

import com.google.common.base.Charsets;
import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.event.GWMatchRoundEndEvent;
import io.github.solyze.goligangwar.event.GWMatchRoundStartEvent;
import io.github.solyze.goligangwar.manager.*;
import io.github.solyze.goligangwar.utility.Color;
import io.github.solyze.goligangwar.utility.Countdown;
import io.github.solyze.goligangwar.utility.DefaultFontInfo;
import io.github.solyze.goligangwar.utility.Match;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoundListener implements Listener {

    private final SpawnManager spawnManager;
    private final KitManager kitManager;
    private final MatchManager matchManager;
    private final GangManager gangManager;
    private final BarrierManager barrierManager;
    private final String prefix;

    public RoundListener() {
        GoliGangWar instance;
        Bukkit.getPluginManager().registerEvents(this, instance = GoliGangWar.getInstance());
        spawnManager = instance.getSpawnManager();
        kitManager = instance.getKitManager();
        matchManager = instance.getMatchManager();
        gangManager = instance.getGangManager();
        barrierManager = instance.getBarrierManager();
        prefix = GoliGangWar.getPrefix();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onRoundStart(GWMatchRoundStartEvent e) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getType() == EntityType.DROPPED_ITEM) {
                    entity.remove();
                }
            }
        }
        barrierManager.showRedBarriers();
        barrierManager.showBlueBarriers();
        String redGang = e.getRedGang();
        String blueGang = e.getBlueGang();
        List<UUID> redUUIDs = gangManager.getMembers(redGang);
        List<UUID> blueUUIDs = gangManager.getMembers(blueGang);
        int roundsNeededForWin = e.getRoundsNeededForWin();
        Location redSpawn = spawnManager.getRedSpawn();
        for (UUID uuid : redUUIDs) {
            Player redPlayer = Bukkit.getPlayer(uuid);
            redPlayer.setHealth(20);
            redPlayer.setFireTicks(0);
            for (PotionEffect pe : redPlayer.getActivePotionEffects()) redPlayer.removePotionEffect(pe.getType());
            kitManager.loadKit(null, redPlayer);
            redPlayer.teleport(redSpawn);
        }
        Location blueSpawn = spawnManager.getBlueSpawn();
        for (UUID uuid : blueUUIDs) {
            Player bluePlayer = Bukkit.getPlayer(uuid);
            bluePlayer.setHealth(20);
            bluePlayer.setFireTicks(0);
            for (PotionEffect pe : bluePlayer.getActivePotionEffects()) bluePlayer.removePotionEffect(pe.getType());
            kitManager.loadKit(null, bluePlayer);
            bluePlayer.teleport(blueSpawn);
        }
        //matchManager.setActiveMatch(new Match(redGang, redUUIDs, blueGang, blueUUIDs, maxRounds));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(DefaultFontInfo.getCenteredMessage(Color.process("&f&m-----&r &6&lROUND &e&l"
                + e.getRound() + " &a&lSTARTING &f&m-----&r")));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(DefaultFontInfo.getCenteredMessage(Color
                .process("&7[&c" + redGang + "&7] &c" + getNameList(redUUIDs, "&c")
                        + " &7vs. &9" + getNameList(blueUUIDs, "&9") + " &7[&9" + blueGang + "&7]")));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(DefaultFontInfo.getCenteredMessage(Color.process(e.getMatch().getScore())));
        Bukkit.broadcastMessage(DefaultFontInfo.getCenteredMessage(Color.process("&8(" + roundsNeededForWin + " round"
                + (roundsNeededForWin == 1 ? "" : "s") + " needed to win)")));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(DefaultFontInfo.getCenteredMessage(Color.process("&7The barriers will be lowered in")));
        Bukkit.broadcastMessage(DefaultFontInfo.getCenteredMessage(Color.process("&f60 &7seconds. Gangs, get ready!")));
        Bukkit.broadcastMessage("");
        for (Player online : Bukkit.getOnlinePlayers()) online.playSound(online.getLocation(), Sound.NOTE_PLING, 0.5F, 1.0F);
        new Countdown() {
            @Override public int[] getBroadcastNumbers() { return new int[] { 1, 2, 3, 4, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60 }; }
            @Override public int getSeconds() { return 60; }
            @Override public void onBroadcast(int seconds) {
                Bukkit.broadcastMessage(Color.process(prefix + "&7The round will start in &f" + seconds + " &7second" + (seconds == 1 ? "" : "s")));
                for (Player online : Bukkit.getOnlinePlayers()) online.playSound(online.getLocation(), Sound.CLICK, 0.5F, 1.0F);
            }
            @Override public void onComplete() {
                barrierManager.hideRedBarriers();
                barrierManager.hideBlueBarriers();
                Bukkit.broadcastMessage(Color.process(prefix + "&aThe round has started, good luck!"));
                for (Player online : Bukkit.getOnlinePlayers()) online.playSound(online.getLocation(), Sound.ENDERDRAGON_GROWL, 0.5F, 1.0F);
            }
        }.start();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onRoundEnd(GWMatchRoundEndEvent e) {
        String winnerGang = e.getWinnerGang();
        Match activeMatch = e.getMatch();
        int round = activeMatch.getRound();
        int roundsNeededForWin = activeMatch.getRoundsNeededForWin();
        String redGang = activeMatch.getRedGang().getKey();
        String blueGang = activeMatch.getBlueGang().getKey();
        activeMatch.addWin(winnerGang);
        if (activeMatch.getWins(winnerGang) >= roundsNeededForWin) {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(DefaultFontInfo.getCenteredMessage(Color
                    .process("&f&m-----&r &6&lROUND &e&l" + round + " &c&lENDED &f&m-----&r")));
            Bukkit.broadcastMessage("");
            List<UUID> winnerUUIDs = new ArrayList<>();
            String winnerName = "";
            String colorCode = "&7";
            if (redGang.equals(winnerGang)) {
                winnerName = redGang;
                winnerUUIDs = activeMatch.getRedGang().getValue();
                colorCode = "&c";
            } else if (blueGang.equals(winnerGang)) {
                winnerName = blueGang;
                winnerUUIDs = activeMatch.getBlueGang().getValue();
                colorCode = "&9";
            }
            Bukkit.broadcastMessage(DefaultFontInfo.getCenteredMessage(Color.process(activeMatch.getScore())));
            Bukkit.broadcastMessage(DefaultFontInfo.getCenteredMessage(Color
                    .process("&7[" + colorCode + winnerName + "&7] " + getNameList(winnerUUIDs, colorCode) + " &awon the match")));
            Bukkit.broadcastMessage("");
            barrierManager.showRedBarriers();
            barrierManager.showBlueBarriers();
            new Countdown() {
                @Override public int[] getBroadcastNumbers() { return new int[] { 1, 2, 3, 4, 5 }; }
                @Override public int getSeconds() { return 5; }
                @Override public void onBroadcast(int seconds) {}
                @Override public void onComplete() {
                    for (UUID uuid : activeMatch.getAllParticipants()) {
                        Player participant = Bukkit.getPlayer(uuid);
                        participant.setHealth(20);
                        participant.setFireTicks(0);
                        participant.getInventory().clear();
                        participant.getInventory().setHelmet(null);
                        participant.getInventory().setChestplate(null);
                        participant.getInventory().setLeggings(null);
                        participant.getInventory().setBoots(null);
                        participant.teleport(spawnManager.getMainSpawn());
                    }
                    activeMatch.clearDead();
                    matchManager.setActiveMatch(null);
                }
            }.start();
        } else {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(DefaultFontInfo.getCenteredMessage(Color
                    .process("&f&m-----&r &6&lROUND &e&l" + round + " &c&lENDED &f&m-----&r")));
            Bukkit.broadcastMessage("");
            String winnerName = "";
            ChatColor winnerColor = ChatColor.WHITE;
            if (redGang.equals(winnerGang)) {
                winnerName = redGang;
                winnerColor = ChatColor.RED;
            } else if (blueGang.equals(winnerGang)) {
                winnerName = blueGang;
                winnerColor = ChatColor.BLUE;
            }
            Bukkit.broadcastMessage(DefaultFontInfo.getCenteredMessage(Color
                    .process(winnerColor + winnerName + " &awon the round")));
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(DefaultFontInfo.getCenteredMessage(Color.process("&7Starting the next round...")));
            Bukkit.broadcastMessage("");
            round++;
            activeMatch.setRound(round);
            int finalRound = round;
            new Countdown() {
                @Override public int[] getBroadcastNumbers() { return new int[] { 1, 2, 3, 4, 5 }; }
                @Override public int getSeconds() { return 5; }
                @Override public void onBroadcast(int seconds) {}
                @Override public void onComplete() {
                    List<UUID> redUUIDs = gangManager.getMembers(redGang);
                    List<UUID> blueUUIDs = gangManager.getMembers(blueGang);
                    Location redSpawn = spawnManager.getRedSpawn();
                    for (UUID uuid : redUUIDs) {
                        Player redPlayer = Bukkit.getPlayer(uuid);
                        redPlayer.setHealth(20);
                        redPlayer.setFireTicks(0);
                        for (PotionEffect pe : redPlayer.getActivePotionEffects()) redPlayer.removePotionEffect(pe.getType());
                        kitManager.loadKit(null, redPlayer);
                        redPlayer.teleport(redSpawn);
                    }
                    Location blueSpawn = spawnManager.getBlueSpawn();
                    for (UUID uuid : blueUUIDs) {
                        Player bluePlayer = Bukkit.getPlayer(uuid);
                        bluePlayer.setHealth(20);
                        bluePlayer.setFireTicks(0);
                        for (PotionEffect pe : bluePlayer.getActivePotionEffects()) bluePlayer.removePotionEffect(pe.getType());
                        kitManager.loadKit(null, bluePlayer);
                        bluePlayer.teleport(blueSpawn);
                    }
                    activeMatch.clearDead();
                    new GWMatchRoundStartEvent(redGang, blueGang, finalRound, activeMatch.getRoundsNeededForWin());
                }
            }.start();
        }
    }

    private String getNameList(List<UUID> uuids, String colorCode) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (UUID uuid : uuids) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            String name = offlinePlayer.getName();
            if (name == null || name.equals("null")) {
                try {
                    String url = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString();
                    JSONObject jsonObject = readJsonFromUrl(url);
                    name = jsonObject.getString("name");
                } catch (Exception ignored) {
                    GoliGangWar.getInstance().getLogger().severe("Could not read profile JSON from Mojang's " +
                            "session server. Names will be displayed as null.");
                }
            }
            if (first) {
                sb.append("&f").append(name);
                //sb.append(colorCode).append(name);
                first = false;
                continue;
            }
            sb.append("&7, &f").append(name);
            //sb.append("&7, ").append(colorCode).append(name);
        }
        return replaceLast(sb.toString(), "&7, &f", " &7& &f"); //replaceLast(sb.toString(), "&7, " + colorCode, "&");
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

    private String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
}