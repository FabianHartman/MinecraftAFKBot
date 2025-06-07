package fabian.hartman.MinecraftAFKBot.event.play;

import com.mojang.brigadier.CommandDispatcher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.event.Event;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;

@Getter
@AllArgsConstructor
public class CommandsRegisteredEvent extends Event {
    private CommandDispatcher<CommandExecutor> commandDispatcher;
}
