package io.github.solyze.goligangwar.event;

import io.github.solyze.goligangwar.GoliGangWar;
import io.github.solyze.goligangwar.utility.Match;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@Getter
public class GWDeathEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Match match;
    private final Player killer;
    private final boolean disconnect;

    public GWDeathEvent(Player player, Player killer, boolean disconnect) {
        super(player);
        this.match = GoliGangWar.getInstance().getMatchManager().getActiveMatch();
        this.killer = killer;
        this.disconnect = disconnect;
        Bukkit.getPluginManager().callEvent(this);
    }

    @Override
    public HandlerList getHandlers() { return handlers; }

    public static HandlerList getHandlerList() { return handlers; }
}
