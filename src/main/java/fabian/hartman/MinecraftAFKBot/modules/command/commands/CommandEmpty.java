package fabian.hartman.MinecraftAFKBot.modules.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import javafx.application.Platform;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.modules.command.BrigardierCommand;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;
import fabian.hartman.MinecraftAFKBot.utils.ItemUtils;
import fabian.hartman.MinecraftAFKBot.utils.LocationUtils;

import java.text.MessageFormat;

public class CommandEmpty extends BrigardierCommand {
    public CommandEmpty() {
        super("empty", "Drops all contents of my inventory (except the fishing rod)", "clear", "drop");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.then(argument("direction", StringArgumentType.word())
                        .then(argument("slot", IntegerArgumentType.integer())
                                .executes(getExecutor()))
                        .executes(getExecutor()))
                .executes(getExecutor());
    }

    private Command<CommandExecutor> getExecutor() {
        return context -> {
            CommandExecutor source = context.getSource();
            source.sendTranslatedMessages("command-empty");

            String directionStr = null;
            try {
                directionStr = context.getArgument("direction", String.class);
            } catch (IllegalArgumentException ignore) {}
            Integer slot = null;
            try {
                slot = context.getArgument("slot", Integer.class);
            } catch (IllegalArgumentException ignore) {}

            if (directionStr != null) {
                LocationUtils.Direction direction;
                try {
                    direction = LocationUtils.Direction.valueOf(directionStr);
                } catch (Exception ex) {
                    source.sendTranslatedMessages("command-empty-unknown-type", directionStr.toUpperCase());
                    return 0;
                }

                float yawBefore = MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().getYaw();
                float pitchBefore = MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().getPitch();
                Integer finalSlot = slot;
                MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().look(direction, finished -> {
                    if (finalSlot != null) {
                        drop(finalSlot.shortValue());
                    } else {
                        empty();
                    }
                    MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().look(yawBefore, pitchBefore, 8);
                });
                return 0;
            }
            empty();
            return 0;
        };
    }

    private void empty() {
        for (short slotId = 9; slotId <= 44; slotId++) {
            drop(slotId);
        }
    }

    private void drop(short slotId) {
        if (slotId == MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().getHeldSlot())
            return;
        if (ItemUtils.isFishingRod(MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().getInventory().getContent().get(slotId)))
            return;
        MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().dropStack(slotId, (short) (slotId - 8));
    }

    @Override
    public String getSyntax(String label) {
        return MessageFormat.format("/{0} [Direction] [Slot]", label);
    }
}