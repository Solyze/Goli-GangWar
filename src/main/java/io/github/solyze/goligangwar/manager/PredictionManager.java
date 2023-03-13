package io.github.solyze.goligangwar.manager;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.utility.Color;
import io.github.solyze.goligangwar.utility.FileManager;
import io.github.solyze.goligangwar.utility.Prediction;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PredictionManager {

    private final String prefix;
    private final FileManager predictionsFM;
    @Getter
    private Prediction activePrediction = null;

    public PredictionManager() {
        GoliGangWar instance;
        predictionsFM = (instance = GoliGangWar.getInstance()).getPredictionsFM();
        prefix = GoliGangWar.getPrefix();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (activePrediction == null) return;
                if (!activePrediction.isOpen()) return;
                Bukkit.broadcastMessage(Color.process(prefix + "&7A prediction is &aactive&7, go to the &3Prediction Master"));
            }
        }.runTaskTimer(instance, 0L, TimeUnit.MINUTES.toSeconds(3) * 20L);
    }

    public void predict(Player player, String option) {
        UUID uuid = player.getUniqueId();
        List<UUID> redList = activePrediction.getRedList();
        List<UUID> blueList = activePrediction.getBlueList();
        String redGang = activePrediction.getRedGang();
        String blueGang = activePrediction.getBlueGang();
        if (!activePrediction.isOpen()) {
            player.sendMessage(Color.process(prefix + "&cThe prediction is closed"));
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
            return;
        }
        if (redList.contains(uuid)) {
            player.sendMessage(Color.process(prefix + "&cYou have already predicted that &4" + redGang + " &cwins"));
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
            return;
        }
        if (blueList.contains(uuid)) {
            player.sendMessage(Color.process(prefix + "&cYou have already predicted that &4" + blueGang + " &cwins"));
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
            return;
        }
        if (redGang.equalsIgnoreCase(option)) {
            if (!redList.contains(uuid)) redList.add(uuid);
            activePrediction.setRedList(redList);
            player.sendMessage(Color.process(prefix + "&7You have predicted that &f" + redGang + " &7wins"));
            player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
        }
        if (blueGang.equalsIgnoreCase(option)) {
            if (!blueList.contains(uuid)) blueList.add(uuid);
            activePrediction.setBlueList(blueList);
            player.sendMessage(Color.process(prefix + "&7You have predicted that &f" + blueGang + " &7wins"));
            player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
        }
    }

    public void startPrediction(CommandSender sender, String option1, String option2) {
        if (activePrediction != null) {
            sender.sendMessage(Color.process(prefix + "&cThere's already an active prediction, you can end it with /prediction end"));
            return;
        }
        activePrediction = new Prediction(option1, option2);
        Bukkit.broadcastMessage(Color.process(prefix + "&a&lNEW PREDICTION! &7Go to the &3Prediction Master"));
    }

    public void closePrediction(CommandSender sender) {
        if (activePrediction == null) {
            sender.sendMessage(Color.process(prefix + "&cThere's no active prediction, you can start one with /prediction start"));
            return;
        }
        activePrediction.setOpen(false);
        Bukkit.broadcastMessage(Color.process(prefix + "&7The prediction has been &cclosed"));
    }

    public void endPrediction(CommandSender sender, String winner) {
        if (!winner.equalsIgnoreCase("red") && !winner.equalsIgnoreCase("blue")) {
            sender.sendMessage(Color.process(prefix + "&cThe winner must be either &4red &cor &4blue"));
            return;
        }
        payout(winner.equalsIgnoreCase("red"), winner.equalsIgnoreCase("blue"));
        activePrediction = null;
        Bukkit.broadcastMessage(Color.process(prefix + "&7The prediction has &cended"));
    }

    private void payout(boolean redWon, boolean blueWon) {
        FileConfiguration config = predictionsFM.getConfiguration();
        if (redWon) {
            for (UUID uuid : activePrediction.getRedList()) {
                String uuidString = uuid.toString();
                int correct = config.contains("players." + uuidString) ? config.getInt("players." + uuidString) : 0;
                correct++;
                config.set("players." + uuidString, correct);
                predictionsFM.save();
            }
        }
        if (blueWon) {
            for (UUID uuid : activePrediction.getBlueList()) {
                String uuidString = uuid.toString();
                int correct = config.contains("players." + uuidString) ? config.getInt("players." + uuidString) : 0;
                correct++;
                config.set("players." + uuidString, correct);
                predictionsFM.save();
            }
        }
        int total = getTotalPredictions();
        config.set("total", total + 1);
        predictionsFM.save();
    }

    public int getPredictionPoints(Player player) {
        FileConfiguration config = predictionsFM.getConfiguration();
        String uuid = player.getUniqueId().toString();
        return config.contains("players." + uuid) ? config.getInt("players." + uuid) : 0;
    }

    public int getTotalPredictions() {
        FileConfiguration config = predictionsFM.getConfiguration();
        return config.contains("total") ? config.getInt("total") : 0;
    }
}
