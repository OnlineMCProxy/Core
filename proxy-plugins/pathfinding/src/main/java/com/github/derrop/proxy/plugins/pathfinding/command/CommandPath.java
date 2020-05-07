package com.github.derrop.proxy.plugins.pathfinding.command;

import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.plugins.pathfinding.Path;
import com.github.derrop.proxy.plugins.pathfinding.finder.PathFindInteraction;
import com.github.derrop.proxy.plugins.pathfinding.provider.PathProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandPath extends NonTabCompleteableCommandCallback {

    private static final double BPS = 5D; // blocks per second by the player

    private final ExecutorService executorService = Executors.newFixedThreadPool(6);
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);

    private ServiceRegistry registry;

    public CommandPath(ServiceRegistry registry) {
        super("command.path", null);
        this.registry = registry;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender sender, @NotNull String[] args, @NotNull String fullLine) throws CommandExecutionException {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is only available for players");
            return CommandResult.BREAK;
        }

        if (args.length != 3) {
            sender.sendMessage("path <x> <y> <z>");
            return CommandResult.BREAK;
        }

        BlockPos pos = this.getTarget(args);
        if (pos == null) {
            sender.sendMessage("§cInvalid target coordinates provided");
            return CommandResult.BREAK;
        }

        Player player = (Player) sender;

        if (player.getConnectedClient() == null) {
            player.sendMessage("Not connected with any client");
            return CommandResult.BREAK;
        }

        sender.sendMessage("Waiting for free worker...");

        this.executorService.execute(() -> {
            sender.sendMessage("Searching for the path, this may take a while...");

            PathFindInteraction interaction = new PathFindInteraction();

            this.executorService.execute(() -> {
                int index = 0;
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                    if (interaction.isCompleted()) {
                        break;
                    }

                    if (++index >= 24) {
                        interaction.cancel();
                        player.sendMessage("§cThe action has been cancelled because it took longer than 2 minutes");
                        break;
                    }
                }
            });

            boolean canFly = player.getConnectedClient().getAbilities().isAllowedFlying();

            BlockPos start = player.getLocation().toBlockPos();
            Path path = this.registry.getProviderUnchecked(PathProvider.class)
                    .findShortestPath(interaction, canFly, player.getConnectedClient().getBlockAccess(), start.down(), pos);

            if (path.isSuccess()) {
                double distance = Math.sqrt(start.distanceSq(pos));
                long time = (long) ((double) path.getAllPoints().length / BPS) + 10;

                sender.sendMessage("§aSuccessfully found the path from " + start.toShortString() + " to " + pos.toShortString() + " (" + String.format("%.2f", distance) + " blocks airway)");
                sender.sendMessage("§aBlocks to walk: " + path.getAllPoints().length + " §7(Calculated with a speed of " + BPS + " blocks per second)");

                path.fill(player.getConnectedClient().getBlockAccess(), Material.EMERALD_BLOCK, true);

                sender.sendMessage(String.format("§cThe emeralds will be replaced in §e%d §cseconds", time));
                this.scheduledExecutorService.schedule(() -> {
                    if (player.getConnectedClient() != null) {
                        player.sendMessage("§aReplacing all " + path.getAllPoints().length + " blocks...");
                        path.refill(player.getConnectedClient().getBlockAccess());
                    }
                }, time, TimeUnit.SECONDS);

            } else {
                sender.sendMessage("§cThere is no way to reach " + pos.toShortString() + " from " + start.toShortString());
            }
        });

        return CommandResult.END;
    }

    private BlockPos getTarget(String[] args) {
        try {
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);
            return new BlockPos(x, y, z);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

}
