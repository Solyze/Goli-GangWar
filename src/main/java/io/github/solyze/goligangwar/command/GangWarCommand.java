package io.github.solyze.goligangwar.command;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.manager.KitManager;
import io.github.solyze.goligangwar.manager.MatchManager;
import io.github.solyze.goligangwar.manager.SpawnManager;
import io.github.solyze.goligangwar.utility.Color;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GangWarCommand implements CommandExecutor {

    private final String prefix;
    private final KitManager kitManager;
    private final SpawnManager spawnManager;
    private final MatchManager matchManager;

    public GangWarCommand() {
        GoliGangWar instance;
        (instance = GoliGangWar.getInstance()).getCommand("gangwar").setExecutor(this);
        prefix = GoliGangWar.getPrefix();
        kitManager = instance.getKitManager();
        spawnManager = instance.getSpawnManager();
        matchManager = instance.getMatchManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("goligangwar.gangwar.admin")) {
            sender.sendMessage(Color.process(prefix + "&cYou do not have permission"));
            return true;
        }
        if (args.length == 0) {
            sendUsage(sender, label);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "kit":
                if (args.length == 1) {
                    sendKitUsage(sender, label);
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "load":
                        Player target;
                        if (args.length >= 3) {
                            target = Bukkit.getPlayer(args[2]);
                        } else {
                            if (!(sender instanceof Player)) {
                                sender.sendMessage(Color.process(prefix + "&cYou must be a player"));
                                return true;
                            }
                            target = (Player) sender;
                        }
                        kitManager.loadKit(sender, target);
                        return true;
                    case "save":
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(Color.process(prefix + "&cYou must be a player"));
                            return true;
                        }
                        kitManager.saveKit((Player) sender);
                        return true;
                    default:
                        sendKitUsage(sender, label);
                        return true;
                }
            case "match":
                if (args.length == 1) {
                    sendMatchUsage(sender, label);
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "start":
                        if (args.length <= 3) {
                            sender.sendMessage(Color.process(prefix + "&cUsage: /" + label + " match start <redGang> <blueGang> [roundsNeededForWin]"));
                            return true;
                        }
                        String redGang = args[2];
                        String blueGang = args[3];
                        int roundsNeededForWin = 2;
                        if (args.length > 4) {
                            try {
                                roundsNeededForWin = Integer.parseInt(args[4]);
                            } catch (NumberFormatException ignored) {
                                sender.sendMessage(Color.process(prefix + "&4" + args[4] + " &cis not a valid number"));
                                return true;
                            }
                        }
                        matchManager.startMatch(sender, redGang, blueGang, roundsNeededForWin);
                        return true;
                    case "endround":
                        if (args.length <= 3) {
                            sender.sendMessage(Color.process(prefix + "&cUsage: /" + label + " match endround <winnerGang>"));
                            return true;
                        }
                        String winnerGang = args[2];
                        matchManager.endRound(sender, winnerGang);
                        return true;
                    default:
                        sendMatchUsage(sender, label);
                        return true;
                }
            case "spawn":
                if (args.length == 1) {
                    sendSpawnUsage(sender, label);
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "setred":
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(Color.process(prefix + "&cYou must be a player"));
                            return true;
                        }
                        spawnManager.setRedSpawn((Player) sender);
                        return true;
                    case "setblue":
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(Color.process(prefix + "&cYou must be a player"));
                            return true;
                        }
                        spawnManager.setBlueSpawn((Player) sender);
                        return true;
                    case "setmain":
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(Color.process(prefix + "&cYou must be a player"));
                            return true;
                        }
                        spawnManager.setMainSpawn((Player) sender);
                        return true;
                    default:
                        sendSpawnUsage(sender, label);
                        return true;
                }
            default:
                sendUsage(sender, label);
                return true;
        }
    }

    private void sendUsage(CommandSender sender, String label) {
        sender.sendMessage(Color.process(prefix + "&cUsage: /" + label + " <kit | match | spawn> [...]"));
    }

    private void sendKitUsage(CommandSender sender, String label) {
        sender.sendMessage(Color.process(prefix + "&cUsage: /" + label + " kit <load | save> [...]"));
    }

    private void sendMatchUsage(CommandSender sender, String label) {
        sender.sendMessage(Color.process(prefix + "&cUsage: /" + label + " match <start | tie | end> [...]"));
    }

    private void sendSpawnUsage(CommandSender sender, String label) {
        sender.sendMessage(Color.process(prefix + "&cUsage: /" + label + " spawn <setred | setblue | setmain>"));
    }
}