package fabian.hartman.MinecraftAFKBot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.event.Event;

@Getter
@AllArgsConstructor
public class PingChangeEvent extends Event {
    private int ping;
}