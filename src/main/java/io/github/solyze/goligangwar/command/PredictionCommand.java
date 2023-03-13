package io.github.solyze.goligangwar.command;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.manager.PredictionManager;
import io.github.solyze.goligangwar.menu.PredictionMenu;
import io.github.solyze.goligangwar.utility.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PredictionCommand implements CommandExecutor {

    private final String prefix;
    private final PredictionManager predictionManager;

    public PredictionCommand() {
        GoliGangWar instance;
        (instance = GoliGangWar.getInstance()).getCommand("prediction").setExecutor(this);
        prefix = GoliGangWar.getPrefix();
        predictionManager = instance.getPredictionManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender, label);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "start":
                if (!sender.hasPermission("goligangwar.prediction.admin")) {
                    sender.sendMessage(Color.process(prefix + "&cYou do not have permission"));
                    return true;
                }
                if (args.length <= 2) {
                    sender.sendMessage(Color.process(prefix + "&cUsage: /" + label + " start <redGang> <blueGang>"));
                    return true;
                }
                String redGang = args[1];
                String blueGang = args[2];
                predictionManager.startPrediction(sender, redGang, blueGang);
                return true;
            case "close":
                if (!sender.hasPermission("goligangwar.prediction.admin")) {
                    sender.sendMessage(Color.process(prefix + "&cYou do not have permission"));
                    return true;
                }
                predictionManager.closePrediction(sender);
                return true;
            case "end":
                if (!sender.hasPermission("goligangwar.prediction.admin")) {
                    sender.sendMessage(Color.process(prefix + "&cYou do not have permission"));
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(Color.process(prefix + "&cUsage: /" + label + " end <winner>"));
                    return true;
                }
                String winner = args[1];
                predictionManager.endPrediction(sender, winner);
                return true;
            case "openmenu":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Color.process(prefix + "&cYou must be a player"));
                    return true;
                }
                Player p = (Player) sender;
                p.openInventory(new PredictionMenu(p).getInventory());
                return true;
            default:
                sendUsage(sender, label);
                return true;
        }
    }

    private void sendUsage(CommandSender sender, String label) {
        if (!sender.hasPermission("goligangwar.prediction.admin")) {
            sender.sendMessage(Color.process(prefix + "&cYou do not have permission"));
            return;
        }
        sender.sendMessage(Color.process(prefix + "&cUsage: /" + label + " <start | close | end> [...]"));
    }
}
