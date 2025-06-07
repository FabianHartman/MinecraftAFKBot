package fabian.hartman.MinecraftAFKBot.modules.command.executor;

import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;

public class ConsoleCommandExecutor implements CommandExecutor {
    public CommandExecutionType getType() {
        return CommandExecutionType.CONSOLE;
    }

    @Override
    public void sendMessage(String message) {
        MinecraftAFKBot.getLog().info(message);
    }
}
