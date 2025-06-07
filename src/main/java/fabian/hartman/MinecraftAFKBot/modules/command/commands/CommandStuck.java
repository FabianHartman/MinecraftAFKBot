package fabian.hartman.MinecraftAFKBot.modules.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.modules.command.BrigardierCommand;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;

public class CommandStuck extends BrigardierCommand {
    public CommandStuck() {
        super("stuck", "Cast out the fishing rod again", "recast", "reeject", "refish", "recatch");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.executes(context -> {
            CommandExecutor source = context.getSource();
            MinecraftAFKBot.getInstance().getCurrentBot().getFishingModule().stuck();
            source.sendTranslatedMessages("command-stuck-executed");
            return 0;
        });
    }
}
