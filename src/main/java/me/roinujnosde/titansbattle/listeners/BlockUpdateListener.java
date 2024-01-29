package me.roinujnosde.titansbattle.listeners;

import me.roinujnosde.titansbattle.TitansBattle;
import me.roinujnosde.titansbattle.games.Game;
import me.roinujnosde.titansbattle.types.Warrior;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BlockUpdateListener extends TBListener {

    public BlockUpdateListener(@NotNull TitansBattle plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        cancel(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event) {
        cancel(event.getPlayer(), event);
    }

    private void cancel(Player player, Cancellable event) {
        Optional<Game> optionalGame = plugin.getGameManager().getCurrentGame();
        optionalGame.ifPresent(game -> {
            if (game.getConfig().isCancelBlockInteract()) {
                Warrior warrior = plugin.getDatabaseManager().getWarrior(player);
                if (game.isInBattle(warrior)) {
                    event.setCancelled(true);
                }
            }
        });
    }

}
