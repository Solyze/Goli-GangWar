package io.github.solyze.goligangwar.manager;

import com.google.common.base.Charsets;
import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.utility.Color;
import io.github.solyze.goligangwar.utility.FileManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class GangManager {

    private final FileManager gangsFM;
    private final String prefix;

    public GangManager() {
        gangsFM = GoliGangWar.getInstance().getGangsFM();
        prefix = GoliGangWar.getPrefix();
    }

    public void createGang(CommandSender sender, String name) {
        FileConfiguration gangsConfig = gangsFM.getConfiguration();
        if (gangsConfig.contains("gangs." + name)) {
            sender.sendMessage(Color.process(prefix + "&cA gang with the name &4" + name + " &calready exists"));
            return;
        }
        gangsConfig.set("gangs." + name, new ArrayList<>());
        try {
            gangsConfig.save(gangsFM.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sender.sendMessage(Color.process(prefix + "&7Created a gang with the name &f" + name));
        Command.broadcastCommandMessage(sender, Color.process("&7&oCreated a gang with the name &f&o" + name + "&7&o"), false);
    }

    public void deleteGang(CommandSender sender, String name) {
        FileConfiguration gangsConfig = gangsFM.getConfiguration();
        if (!gangsConfig.contains("gangs." + name)) {
            sender.sendMessage(Color.process(prefix + "&cA gang with the name &4" + name + " &cdoes not exist"));
            return;
        }
        gangsConfig.set("gangs." + name, null);
        try {
            gangsConfig.save(gangsFM.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sender.sendMessage(Color.process(prefix + "&7The &f" + name + " &7gang has been deleted"));
        Command.broadcastCommandMessage(sender, Color.process("&7&oThe &f&o" + name + " &7&ogang has been deleted"), false);
    }

    public void listGangs(CommandSender sender) {
        FileConfiguration gangsConfig = gangsFM.getConfiguration();
        ConfigurationSection section = gangsConfig.getConfigurationSection("gangs");
        if (section == null) {
            sender.sendMessage(Color.process(prefix + "&7Gangs (0): &cNone"));
            return;
        }
        Set<String> keys = section.getKeys(false);
        boolean first = true;
        StringBuilder sb = new StringBuilder(prefix + "&7Gangs (" + keys.size() + "): &f");
        for (String key : keys) {
            if (first) {
                sb.append(key);
                first = false;
                continue;
            }
            sb.append("&7, &f").append(key);
        }
        sender.sendMessage(Color.process(sb.toString()));
    }

    public List<UUID> getMembers(String name) {
        FileConfiguration gangsConfig = gangsFM.getConfiguration();
        if (gangsConfig.contains("gangs." + name)) {
            List<UUID> members = new ArrayList<>();
            for (String rawUUID : gangsConfig.getStringList("gangs." + name)) {
                members.add(UUID.fromString(rawUUID));
            }
            return members;
        } else {
            return new ArrayList<>();
        }
    }

    public void displayGangMembers(CommandSender sender, String name) {
        FileConfiguration gangsConfig = gangsFM.getConfiguration();
        if (!gangsConfig.contains("gangs." + name)) {
            sender.sendMessage(Color.process(prefix + "&cA gang with the name &4" + name + " &cdoes not exist"));
            return;
        }
        boolean first = true;
        List<UUID> members = getMembers(name);
        StringBuilder sb = new StringBuilder(prefix + "&7Members of &e" + name + " &7(" + members.size() + "): &f");
        for (UUID uuid : members) {
            String playerName = Bukkit.getOfflinePlayer(uuid).getName();
            if (playerName == null || playerName.equals("null")) {
                try {
                    String url = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString();
                    JSONObject jsonObject = readJsonFromUrl(url);
                    playerName = jsonObject.getString("name");
                } catch (Exception ignored) {
                    GoliGangWar.getInstance().getLogger().severe("Could not read profile JSON from Mojang's " +
                            "session server. Names will be displayed as null.");
                }
            }
            if (first) {
                sb.append(playerName);
                first = false;
                continue;
            }
            sb.append("&7, &f").append(playerName);
        }
        sender.sendMessage(Color.process(sb.toString()));
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

    public void addMember(CommandSender sender, String name, OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        String playerName = offlinePlayer.getName();
        FileConfiguration gangsConfig = gangsFM.getConfiguration();
        if (!gangsConfig.contains("gangs." + name)) {
            sender.sendMessage(Color.process(prefix + "&cA gang with the name &4" + name + " &cdoes not exist"));
            return;
        }
        List<UUID> uuids = getMembers(name);
        if (uuids.contains(uuid)) {
            sender.sendMessage(Color.process(prefix + "&4" + name + " &cis already in &4" + name));
            return;
        }
        List<String> rawUUIDs = new ArrayList<>();
        uuids.add(uuid);
        for (UUID uuid1 : uuids) {
            rawUUIDs.add(uuid1.toString());
        }
        gangsConfig.set("gangs." + name, rawUUIDs);
        try {
            gangsConfig.save(gangsFM.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sender.sendMessage(Color.process(prefix + "&b" + playerName + " &7has been added to the &f" + name + " &7gang"));
        Command.broadcastCommandMessage(sender, Color.process("&b&o" + name + " &7&ohas been added to the &f&o" + name + " &7&ogang"), false);
    }

    public void removeMember(CommandSender sender, String name, OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        String playerName = offlinePlayer.getName();
        FileConfiguration gangsConfig = gangsFM.getConfiguration();
        if (!gangsConfig.contains("gangs." + name)) {
            sender.sendMessage(Color.process(prefix + "&cA gang with the name &4" + name + " &cdoes not exist"));
            return;
        }
        List<UUID> uuids = getMembers(name);
        if (!uuids.contains(uuid)) {
            sender.sendMessage(Color.process(prefix + "&4" + name + " &cis not in &4" + name));
            return;
        }
        uuids.remove(uuid);
        List<String> rawUUIDs = new ArrayList<>();
        for (UUID uuid1 : uuids) {
            rawUUIDs.add(uuid1.toString());
        }
        gangsConfig.set("gangs." + name, rawUUIDs);
        try {
            gangsConfig.save(gangsFM.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sender.sendMessage(Color.process(prefix + "&b" + playerName + " &7has been removed from the &f" + name + " &7gang"));
        Command.broadcastCommandMessage(sender, Color.process("&b&o" + name + " &7&ohas been removed from the &f&o" + name + " &7&ogang"), false);
    }

    public String getGang(UUID uuid) {
        String uuidString = uuid.toString();
        FileConfiguration gangsConfig = gangsFM.getConfiguration();
        if (!gangsConfig.contains("gangs")) return null;
        ConfigurationSection section = gangsConfig.getConfigurationSection("gangs");
        if (section == null) return null;
        Set<String> keys = section.getKeys(false);
        for (String key : keys) {
            List<String> rawUUIDs = gangsConfig.getStringList("gangs." + key);
            if (rawUUIDs.contains(uuidString)) return key;
        }
        return null;
    }
}
