package fabian.hartman.MinecraftAFKBot.modules.command.brigardier.node;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;

public class LiteralNode extends Node {
    public LiteralNode(String name) {
        super(name);
    }

    @Override
    public ArgumentBuilder<CommandExecutor, ?> createArgumentBuilder() {
        return LiteralArgumentBuilder.literal(name);
    }
}