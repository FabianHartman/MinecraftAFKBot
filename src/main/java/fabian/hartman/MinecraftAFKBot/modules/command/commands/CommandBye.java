package fabian.hartman.MinecraftAFKBot.modules.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.modules.command.BrigardierCommand;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;

public class CommandBye extends BrigardierCommand {
    public CommandBye() {
        super("bye", "Stops the bot", "stop", "shutdown");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.executes(context -> {
            CommandExecutor source = context.getSource();

            source.sendTranslatedMessages("command-bye");

            MinecraftAFKBot.getInstance().getCurrentBot().setPreventReconnect(true);
            MinecraftAFKBot.getInstance().getCurrentBot().setRunning(false);
            return 0;
        });
    }
}