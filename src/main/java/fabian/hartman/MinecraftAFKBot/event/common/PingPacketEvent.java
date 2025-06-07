package fabian.hartman.MinecraftAFKBot.event.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.event.Event;

@Getter
@AllArgsConstructor
public class PingPacketEvent extends Event {
    private int id;
}