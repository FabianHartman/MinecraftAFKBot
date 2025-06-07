package fabian.hartman.MinecraftAFKBot.modules.command.executor;

public interface CommandExecutor {
    CommandExecutionType getType();

    void sendMessage(String message);
}