package fabian.hartman.MinecraftAFKBot.modules.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.bot.Inventory;
import fabian.hartman.MinecraftAFKBot.bot.Slot;
import fabian.hartman.MinecraftAFKBot.modules.command.BrigardierCommand;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;
import fabian.hartman.MinecraftAFKBot.utils.ItemUtils;

import java.text.MessageFormat;

public class CommandDropRod extends BrigardierCommand {
    public CommandDropRod() {
        super("droprod", "Drops either your current selected rod, all but your selected rod or all of your rods", "roddrop", "droprods", "rodsdrop", "emptyrod", "rodempty");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.then(argument("filter", StringArgumentType.greedyString())
                        .executes(getExecutor()))
                .executes(getExecutor());
    }

    private Command<CommandExecutor> getExecutor() {
        return context -> {
            CommandExecutor source = context.getSource();
            Filter filter = Filter.ALL_BUT_SELECTED;

            String filterStr = null;
            try {
                filterStr = context.getArgument("filter", String.class);
            } catch (IllegalArgumentException ignore) {}

            if (filterStr != null) {
                try {
                    filter = Filter.valueOf(filterStr.replace(" ", "_").toUpperCase());
                } catch (Exception e) {
                    source.sendTranslatedMessages("command-droprod-unknown-type", filterStr.replace(" ", "_").toUpperCase());
                    return 0;
                }
            }

            Inventory inventory = MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().getInventory();
            int dropCount = 0;

            for (int slotId : inventory.getContent().keySet()) {
                Slot slot = inventory.getContent().get(slotId);
                if (!ItemUtils.isFishingRod(slot)) continue;

                if (filter == Filter.ALL || (filter == Filter.ALL_BUT_SELECTED && slotId != MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().getHeldSlot())) {
                    MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().dropStack((short) slotId, (short) (slotId - 8));
                    dropCount++;
                } else if (filter == Filter.SELECTED && slotId == MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().getHeldSlot()) {
                    MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().dropStack((short) slotId, (short) (slotId - 8));
                    MinecraftAFKBot.getInstance().getCurrentBot().getFishingModule().swapWithBestFishingRod();
                    dropCount++;
                    break;
                }
            }

            source.sendTranslatedMessages("command-droprod-item-count", dropCount);
            return 0;
        };
    }

    @Override
    public String getSyntax(String label) {
        return MessageFormat.format("/{0} [Filter]", label);
    }

    public enum Filter {
        ALL,
        SELECTED,
        ALL_BUT_SELECTED
    }
}