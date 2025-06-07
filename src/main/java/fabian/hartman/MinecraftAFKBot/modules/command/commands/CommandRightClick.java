package fabian.hartman.MinecraftAFKBot.modules.command.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.modules.command.BrigardierCommand;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;

import java.text.MessageFormat;

public class CommandRightClick extends BrigardierCommand {
    public CommandRightClick() {
        super("rightclick", "Let the bot perform a rightclick", "use");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.then(argument("slot", IntegerArgumentType.integer(0))
                        .executes(context -> {
                            CommandExecutor source = context.getSource();
                            int slot = context.getArgument("slot", Integer.class) - 1;
                            if (slot < 0 || slot > 8) {
                                source.sendTranslatedMessages("command-rightclick-invalid-slot", slot + 1);
                                return 0;
                            }
                            MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().setHeldSlot(slot);
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().use();
                            return 0;
                        }))
                .executes(context -> {
                    MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().use();
                    return 0;
                });
    }

    @Override
    public String getSyntax(String label) {
        return MessageFormat.format("/{0} [Hotbar slot]", label);
    }
}