package io.github.solyze.goligangwar.command;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.manager.GangManager;
import io.github.solyze.goligangwar.utility.Color;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GangCommand implements CommandExecutor {

    private final String prefix;
    private final GangManager gangManager;

    public GangCommand() {
        GoliGangWar instance;
        (instance = GoliGangWar.getInstance()).getCommand("gang").setExecutor(this);
        prefix = GoliGangWar.getPrefix();
        gangManager = instance.getGangManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender, label);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "create":
                if (!sender.hasPermission("goligangwar.gang.admin")) {
                    sender.sendMessage(Color.process(prefix + "&cYou do not have permission"));
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(Color.process(prefix + "&cUsage: /" + label + " create <name>"));
                    return true;
                }
                String name = args[1];
                gangManager.createGang(sender, name);
                return true;
            case "delete":
                if (!sender.hasPermission("goligangwar.gang.admin")) {
                    sender.sendMessage(Color.process(prefix + "&cYou do not have permission"));
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(Color.process(prefix + "&cUsage: /" + label + " delete <name>"));
                    return true;
                }
                String name1 = args[1];
                gangManager.deleteGang(sender, name1);
                return true;
            case "addmember":
                if (!sender.hasPermission("goligangwar.gang.admin")) {
                    sender.sendMessage(Color.process(prefix + "&cYou do not have permission"));
                    return true;
                }
                if (args.length <= 2) {
                    sender.sendMessage(Color.process(prefix + "&cUsage: /" + label + " addmember <name> <player>"));
                    return true;
                }
                String name3 = args[1];
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(Color.process(prefix + "&cPlayer &4" + args[2] + " &cwas not found"));
                    return true;
                }
                gangManager.addMember(sender, name3, target);
                return true;
            case "removemember":
                if (!sender.hasPermission("goligangwar.gang.admin")) {
                    sender.sendMessage(Color.process(prefix + "&cYou do not have permission"));
                    return true;
                }
                if (args.length <= 2) {
                    sender.sendMessage(Color.process(prefix + "&cUsage: /" + label + " removemember <name> <player>"));
                    return true;
                }
                String name4 = args[1];
                OfflinePlayer target1 = Bukkit.getOfflinePlayer(args[2]);
                if (target1 == null) {
                    sender.sendMessage(Color.process(prefix + "&cPlayer &4" + args[2] + " &cwas not found"));
                    return true;
                }
                gangManager.removeMember(sender, name4, target1);
                return true;
            case "members":
                if (args.length == 1) {
                    sender.sendMessage(Color.process(prefix + "&cUsage: /" + label + " members <name>"));
                    return true;
                }
                String name2 = args[1];
                gangManager.displayGangMembers(sender, name2);
                return true;
            case "list":
                gangManager.listGangs(sender);
                return true;
            default:
                sendUsage(sender, label);
                return true;
        }
    }

    private void sendUsage(CommandSender sender, String label) {
        if (sender.hasPermission("goligangwar.gang.admin")) {
            sender.sendMessage(Color.process(prefix + "&cUsage: /" + label + " <create | delete | addmember | removemember | members | list> [...]"));
            return;
        }
        sender.sendMessage(Color.process(prefix + "&cUsage: /" + label + " <members | list> [...]"));
    }
}
