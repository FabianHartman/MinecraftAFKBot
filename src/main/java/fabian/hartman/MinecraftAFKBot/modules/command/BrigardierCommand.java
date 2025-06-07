package fabian.hartman.MinecraftAFKBot.modules.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

public abstract class BrigardierCommand {
    @Getter private final String label;
    @Getter private final List<String> aliases;
    @Getter private final String description;

    public BrigardierCommand(String label, String description, String... aliases) {
        this.label = label.toLowerCase().trim();
        this.description = description;
        for (int i = 0; i < aliases.length; i++)
            aliases[i] = aliases[i].toLowerCase().trim();
        this.aliases = Arrays.asList(aliases);
    }

    public abstract void register(LiteralArgumentBuilder<CommandExecutor> builder);

    public String getSyntax(String label) {
        return MessageFormat.format("/{0}", label);
    }
}