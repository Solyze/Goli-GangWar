package io.github.solyze.goligangwar.event;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.utility.Match;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class GWMatchRoundEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Match match;
    private final String winnerGang;
    private final String loserGang;
    private final int round;

    public GWMatchRoundEndEvent(String winnerGang, String loserGang, int round) {
        this.match = GoliGangWar.getInstance().getMatchManager().getActiveMatch();
        this.winnerGang = winnerGang;
        this.loserGang = loserGang;
        this.round = round;
        Bukkit.getPluginManager().callEvent(this);
    }

    @Override
    public HandlerList getHandlers() { return handlers; }

    public static HandlerList getHandlerList() { return handlers; }
}
