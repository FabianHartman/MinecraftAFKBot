package fabian.hartman.MinecraftAFKBot.modules.command.brigardier.node;

import com.mojang.brigadier.builder.ArgumentBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;

@AllArgsConstructor
@Getter
public abstract class Node {
    protected final String name;

    public abstract ArgumentBuilder<CommandExecutor, ?> createArgumentBuilder();
}