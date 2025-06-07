package fabian.hartman.MinecraftAFKBot.modules.command;

import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutionType;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;

import java.util.Arrays;
import java.util.List;

public abstract class Command {
    @Getter private final String label;
    @Getter private final List<String> aliases;
    @Getter private final String description;

    public Command(String label, String description, String... aliases) {
        this.label = label.toLowerCase().trim();
        this.description = description;
        for (int i = 0; i < aliases.length; i++)
            aliases[i] = aliases[i].toLowerCase().trim();
        this.aliases = Arrays.asList(aliases);
    }

    public abstract void onCommand(String label, String[] args, CommandExecutor executor);

    public void sendMessage(String message, CommandExecutor commandExecutor) {
        if (commandExecutor.getType() == CommandExecutionType.CONSOLE)
            MinecraftAFKBot.getLog().info(message);
        else
            MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().sendMessage(message, commandExecutor);
    }
}