package fabian.hartman.MinecraftAFKBot.modules.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.bot.Inventory;
import fabian.hartman.MinecraftAFKBot.bot.Slot;
import fabian.hartman.MinecraftAFKBot.modules.command.BrigardierCommand;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutClickWindow;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandClickInv extends BrigardierCommand {
    public CommandClickInv() {
        super("clickinv", "Let the bot click a slot in an opened inventory", "invclick");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.then(argument("slot", IntegerArgumentType.integer(1))
                        .then(argument("button", StringArgumentType.word())
                                .executes(getExecutor()))
                        .executes(getExecutor()))
                .executes(context -> {
                    context.getSource().sendMessage(getSyntax(context));
                    return 0;
                });
    }

    private Command<CommandExecutor> getExecutor() {
        return context -> {
            CommandExecutor source = context.getSource();

            Optional<Integer> openedWindow = MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().getOpenedInventories().keySet().stream().max(Comparator.comparingInt(integer -> integer));
            if (!openedWindow.isPresent() || openedWindow.get() <= 0) {
                source.sendTranslatedMessages("command-clickinv-no-inv");
                return 0;
            }

            String buttonStr = null;
            try {
                buttonStr = context.getArgument("button", String.class);
            } catch (IllegalArgumentException ignore) {}
            byte button = buttonStr == null || !buttonStr.equals("right") ? (byte) 0 : 1;

            short slot = (short) (context.getArgument("slot", Integer.class) - 1);

            Inventory inventory = MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().getOpenedInventories().get(openedWindow.get());
            if (!inventory.getContent().containsKey((int) slot)) {
                source.sendTranslatedMessages("command-clickinv-invalid-slot");
                return 0;
            }

            Map<Short, Slot> remainingSlots = new HashMap<>();
            remainingSlots.put(slot, Slot.EMPTY);
            MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(
                    new PacketOutClickWindow(openedWindow.get(),
                            slot,
                            button,
                            inventory.getActionCounter(),
                            (short) 0,
                            inventory.getContent().get((int) slot),
                            remainingSlots
                    )
            );
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 0;
        };
    }

    @Override
    public String getSyntax(String label) {
        return MessageFormat.format("/{0} <Slot> [left|right]", label);
    }
}
