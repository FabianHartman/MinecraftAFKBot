package fabian.hartman.MinecraftAFKBot.modules.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.modules.command.BrigardierCommand;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutionType;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class CommandHelp extends BrigardierCommand {
    public CommandHelp() {
        super("help", "Displays this help message");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.then(argument("command", StringArgumentType.word())
                .executes(context -> {
                    CommandExecutor source = context.getSource();
                    String commandStr = context.getArgument("command", String.class);
                    if (commandStr.startsWith("/"))
                        commandStr = commandStr.substring(1);
                    Optional<BrigardierCommand> optCommand = MinecraftAFKBot.getInstance().getCurrentBot().getCommandRegistry().getCommand(commandStr);
                    if (optCommand.isPresent()) {
                        source.sendMessage(optCommand.get().getSyntax(commandStr));
                    } else {
                        source.sendTranslatedMessages("command-help-invalid-command");
                    }
                    return 0;
                })).executes(context -> {
            CommandExecutor source = context.getSource();
            List<BrigardierCommand> commands = MinecraftAFKBot.getInstance().getCurrentBot().getCommandRegistry().getCommands();
            commands.forEach(command -> {
                if (source.getType() == CommandExecutionType.OTHER_PLAYER)
                    source.sendTranslatedMessages("command-help-other-player", command.getLabel(), command.getDescription());
                else if (source.getType() == CommandExecutionType.OTHER_PLAYER_PRIVATE)
                    source.sendTranslatedMessages("command-help-other-player-private", command.getLabel(), command.getDescription());
                else if (source.getType() == CommandExecutionType.CONSOLE)
                    source.sendTranslatedMessages("command-help-console", command.getLabel(), command.getDescription());
            });
            return 0;
        });
    }

    @Override
    public String getSyntax(String label) {
        return MessageFormat.format("/{0} [Command]", label);
    }
}