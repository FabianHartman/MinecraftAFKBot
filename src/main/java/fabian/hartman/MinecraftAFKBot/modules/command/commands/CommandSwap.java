package fabian.hartman.MinecraftAFKBot.modules.command.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.bot.Player;
import fabian.hartman.MinecraftAFKBot.modules.command.BrigardierCommand;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;

import java.text.MessageFormat;

public class CommandSwap extends BrigardierCommand {
    public CommandSwap() {
        super("swap", "Swaps two items in the inventory", "swapitem");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.then(argument("slot", IntegerArgumentType.integer())
                        .then(argument("hotbar", IntegerArgumentType.integer(1, 9))
                                .executes(context -> {
                                    Player player = MinecraftAFKBot.getInstance().getCurrentBot().getPlayer();

                                    int slot1 = context.getArgument("slot", Integer.class);
                                    int slot2 = context.getArgument("hotbar", Integer.class);
                                    player.swapToHotBar(slot1, slot2);
                                    return 0;
                                }))
                        .executes(context -> {
                            context.getSource().sendMessage(getSyntax(context));
                            return 0;
                        }))
                .executes(context -> {
                    context.getSource().sendMessage(getSyntax(context));
                    return 0;
                });
    }

    @Override
    public String getSyntax(String label) {
        return MessageFormat.format("/swap <Slot> <Hotbar slot>", label);
    }
}