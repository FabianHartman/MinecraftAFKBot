package fabian.hartman.MinecraftAFKBot.modules.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.modules.command.BrigardierCommand;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;

import java.text.MessageFormat;

public class CommandLevel extends BrigardierCommand {
    public CommandLevel() {
        super("level", "Shows the bot''s current levels", "level?");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.executes(context -> {
            context.getSource().sendTranslatedMessages("command-level", MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().getLevels());
            return 0;
        });
    }
}
