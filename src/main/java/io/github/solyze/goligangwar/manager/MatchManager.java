package io.github.solyze.goligangwar.manager;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.event.GWMatchRoundEndEvent;
import io.github.solyze.goligangwar.event.GWMatchRoundStartEvent;
import io.github.solyze.goligangwar.utility.Color;
import io.github.solyze.goligangwar.utility.Match;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class MatchManager {

    private final GangManager gangManager;
    private final SpawnManager spawnManager;
    private final KitManager kitManager;
    private final String prefix;
    private Match activeMatch;
    private final BarrierManager barrierManager;

    public MatchManager() {
        GoliGangWar instance;
        gangManager = (instance = GoliGangWar.getInstance()).getGangManager();
        spawnManager = instance.getSpawnManager();
        kitManager = instance.getKitManager();
        prefix = GoliGangWar.getPrefix();
        activeMatch = null;
        barrierManager = instance.getBarrierManager();
    }

    public void startMatch(CommandSender sender, String redGang, String blueGang, int roundsNeededForWin) {
        if (activeMatch != null) {
            sender.sendMessage(Color.process(prefix + "&cThere is already an active match, you can end it by using &4/gangwar match end"));
            return;
        }
        if (roundsNeededForWin <= 0) {
            sender.sendMessage(Color.process(prefix + "&cThere must be at least &41 &cround"));
            return;
        }
        List<UUID> redUUIDs = gangManager.getMembers(redGang);
        if (redUUIDs.isEmpty()) {
            sender.sendMessage(Color.process(prefix + "&cGang &4" + redGang + " &ceither doesn't exist or has no members"));
            return;
        }
        List<UUID> blueUUIDs = gangManager.getMembers(blueGang);
        if (blueUUIDs.isEmpty()) {
            sender.sendMessage(Color.process(prefix + "&cGang &4" + blueGang + " &ceither doesn't exist or has no members"));
            return;
        }
        activeMatch = new Match(redGang, redUUIDs, blueGang, blueUUIDs, roundsNeededForWin);
        new GWMatchRoundStartEvent(redGang, blueGang, 1, roundsNeededForWin);
    }

    public void endRound(CommandSender sender, String winnerGang) {
        if (activeMatch == null) {
            sender.sendMessage(Color.process(prefix + "&cThere is no active match right now"));
            return;
        }
        new GWMatchRoundEndEvent(winnerGang, winnerGang.equals(activeMatch.getRedGang().getKey())
                ? activeMatch.getRedGang().getKey() : activeMatch.getBlueGang().getKey(), activeMatch.getRound());
    }
}
