package io.github.solyze.goligangwar.utility;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.*;

@Setter
@Getter
public class Match {

    private final Pair<String, List<UUID>> redGang;
    private final Pair<String, List<UUID>> blueGang;
    private final HashMap<Integer, String> roundWinnersMap;
    private int round;
    private List<UUID> dead;
    private int roundsNeededForWin;

    public Match(String redGang, List<UUID> redGangMembers, String blueGang, List<UUID> blueGangMembers, int roundsNeededForWin) {
        this.redGang = new Pair<>(redGang, redGangMembers);
        this.blueGang = new Pair<>(blueGang, blueGangMembers);
        this.roundWinnersMap = new HashMap<>();
        this.round = 1;
        this.dead = new ArrayList<>();
        this.roundsNeededForWin = roundsNeededForWin;
    }

    public List<UUID> getAllParticipants() {
        List<UUID> participants = new ArrayList<>();
        participants.addAll(redGang.getValue());
        participants.addAll(blueGang.getValue());
        return participants;
    }

    public void addWin(String gang) {
        roundWinnersMap.put(round, gang);
    }

    public int getWins(String gang) {
        int wins = 0;
        for (int i = 0; i < round + 1; i++) {
            if (roundWinnersMap.containsKey(i)) {
                String gang1 = roundWinnersMap.get(i);
                if (gang1.equals(gang)) wins++;
            }
        }
        return wins;
    }

    public String getScore() {
        int red = 0;
        int blue = 0;
        for (int i = 0; i < round + 1; i++) {
            if (roundWinnersMap.containsKey(i)) {
                String gang = roundWinnersMap.get(i);
                if (gang.equals(redGang.getKey())) red++;
                if (gang.equals(blueGang.getKey())) blue++;
            }
        }
        return Color.process("&c▬ &6&l" + red + " &7- &6&l" + blue + " &9▬");
    }

    public void clearDead() {
        dead.clear();
    }

    public void addDead(Player player) {
        dead.add(player.getUniqueId());
    }

    public boolean isDead(Player player) {
        return dead.contains(player.getUniqueId());
    }

    public boolean isOnRedTeam(Player player) {
        return redGang.getValue().contains(player.getUniqueId());
    }

    public boolean isOnBlueTeam(Player player) {
        return blueGang.getValue().contains(player.getUniqueId());
    }

    public int getAmountOfDeadPlayers(List<UUID> uuids) {
        int amount = 0;
        for (UUID uuid : uuids) if (getDead().contains(uuid)) amount++;
        return amount;
    }

    public int getAmountOfRedPlayers() {
        return redGang.getValue().size();
    }

    public int getAmountOfBluePlayers() {
        return blueGang.getValue().size();
    }
}
