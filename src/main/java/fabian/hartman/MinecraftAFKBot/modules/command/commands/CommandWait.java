package fabian.hartman.MinecraftAFKBot.modules.command.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.modules.command.BrigardierCommand;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;

import java.text.MessageFormat;

public class CommandWait extends BrigardierCommand {
    public CommandWait() {
        super("wait", "Let the bot wait some time (in seconds)", "sleep");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.then(argument("time", IntegerArgumentType.integer(0))
                        .executes(context -> {
                            int time = context.getArgument("time", Integer.class);

                            try {
                                Thread.sleep(time * 1000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            context.getSource().sendTranslatedMessages("command-wait-waited", time);
                            return 0;
                        }))
                .executes(context -> {
                    context.getSource().sendMessage(getSyntax(context));
                    return 0;
                });
    }

    @Override
    public String getSyntax(String label) {
        return MessageFormat.format("/{0} <Time in seconds>", label);
    }
}