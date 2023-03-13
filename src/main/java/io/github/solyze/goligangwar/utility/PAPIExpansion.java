package io.github.solyze.goligangwar.utility;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.manager.GangManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PAPIExpansion extends PlaceholderExpansion {

    private final GangManager gangManager;

    public PAPIExpansion() {
        GoliGangWar instance = GoliGangWar.getInstance();
        gangManager = instance.getGangManager();
    }

    @Override
    public String getIdentifier() {
        return "gangwar";
    }

    @Override
    public String getAuthor() {
        return "Solyze";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(final Player player, @NotNull final String params) {

        if (params.equalsIgnoreCase("gang")) {
            String gang = gangManager.getGang(player.getUniqueId());
            if (gang == null) return "";
            return gang;
        }

        if (params.equalsIgnoreCase("gang-formatted")) {
            String gang = gangManager.getGang(player.getUniqueId());
            if (gang == null) return "";
            return Color.process("&7 (" + gang + ")");
        }

        if (params.equalsIgnoreCase("match-red-gang")) {
            Match match = GoliGangWar.getInstance().getMatchManager().getActiveMatch();
            if (match == null) return "N/A";
            return match.getRedGang().getKey();
        }

        if (params.equalsIgnoreCase("match-red-points")) {
            Match match = GoliGangWar.getInstance().getMatchManager().getActiveMatch();
            if (match == null) return "-";
            return String.valueOf(match.getWins(match.getRedGang().getKey()));
        }

        if (params.equalsIgnoreCase("match-blue-gang")) {
            Match match = GoliGangWar.getInstance().getMatchManager().getActiveMatch();
            if (match == null) return "N/A";
            return match.getBlueGang().getKey();
        }

        if (params.equalsIgnoreCase("match-blue-points")) {
            Match match = GoliGangWar.getInstance().getMatchManager().getActiveMatch();
            if (match == null) return "-";
            return String.valueOf(match.getWins(match.getBlueGang().getKey()));
        }

        if (params.startsWith("prefix-orelse:")) {
            String[] split = params.split(":");
            String orElse = Color.process(split[1].replace("__", " "));
            Match match = GoliGangWar.getInstance().getMatchManager().getActiveMatch();
            if (match == null) return orElse;
            String prefix = "";
            if (match.isOnRedTeam(player)) prefix = Color.process("&c&lRED &c");
            if (match.isOnBlueTeam(player)) prefix = Color.process("&9&lBLUE &9");
            return prefix;
        }

        if (params.startsWith("nameprefix-orelse:")) {
            String[] split = params.split(":");
            String orElse = Color.process(split[1].replace("__", " "));
            Match match = GoliGangWar.getInstance().getMatchManager().getActiveMatch();
            if (match == null) return orElse;
            String prefix = "";
            if (match.isOnRedTeam(player)) prefix = Color.process("&c");
            if (match.isOnBlueTeam(player)) prefix = Color.process("&9");
            return prefix;
        }

        if (params.equalsIgnoreCase("pred-red-name")) {
            Prediction prediction = GoliGangWar.getInstance().getPredictionManager().getActivePrediction();
            if (prediction == null) return "N/A";
            return prediction.getRedGang();
        }

        if (params.equalsIgnoreCase("pred-red-percentage")) {
            Prediction prediction = GoliGangWar.getInstance().getPredictionManager().getActivePrediction();
            if (prediction == null) return "-";
            return String.valueOf(prediction.getRedPredictions());
        }

        if (params.equalsIgnoreCase("pred-blue-name")) {
            Prediction prediction = GoliGangWar.getInstance().getPredictionManager().getActivePrediction();
            if (prediction == null) return "N/A";
            return prediction.getBlueGang();
        }

        if (params.equalsIgnoreCase("pred-blue-percentage")) {
            Prediction prediction = GoliGangWar.getInstance().getPredictionManager().getActivePrediction();
            if (prediction == null) return "-";
            return String.valueOf(prediction.getBluePredictions());
        }

        if (params.equalsIgnoreCase("preds-correct")) {
            return String.valueOf(GoliGangWar.getInstance().getPredictionManager().getPredictionPoints(player));
        }

        if (params.equalsIgnoreCase("preds-total")) {
            return String.valueOf(GoliGangWar.getInstance().getPredictionManager().getTotalPredictions());
        }

        return null;
    }
}
