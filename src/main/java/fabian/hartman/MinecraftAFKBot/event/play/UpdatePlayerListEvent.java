package fabian.hartman.MinecraftAFKBot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.event.Event;

import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UpdatePlayerListEvent extends Event {
    private Action action;
    private Set<UUID> players;

    public static enum Action {
        REPLACE,
        ADD,
        REMOVE
    }
}