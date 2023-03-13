package io.github.solyze.goligangwar.event;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.utility.Match;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class GWMatchRoundStartEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Match match;
    private final String redGang;
    private final String blueGang;
    private final int round;
    private final int roundsNeededForWin;

    public GWMatchRoundStartEvent(String redGang, String blueGang, int round, int roundsNeededForWin) {
        this.match = GoliGangWar.getInstance().getMatchManager().getActiveMatch();
        this.redGang = redGang;
        this.blueGang = blueGang;
        this.round = round;
        this.roundsNeededForWin = roundsNeededForWin;
        Bukkit.getPluginManager().callEvent(this);
    }

    @Override
    public HandlerList getHandlers() { return handlers; }

    public static HandlerList getHandlerList() { return handlers; }
}
