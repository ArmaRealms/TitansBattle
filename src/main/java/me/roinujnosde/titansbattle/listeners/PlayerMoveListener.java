package me.roinujnosde.titansbattle.listeners;

import me.roinujnosde.titansbattle.BaseGame;
import me.roinujnosde.titansbattle.TitansBattle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerMoveListener extends TBListener {

    public PlayerMoveListener(@NotNull TitansBattle plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommandTeleport(PlayerMoveEvent event) {
        if (event.hasChangedBlock()) {
            BaseGame game = plugin.getBaseGameFrom(event.getPlayer());
            if (game != null && game.isPreparation()) {
                event.setCancelled(true);
            }
        }
    }

}
