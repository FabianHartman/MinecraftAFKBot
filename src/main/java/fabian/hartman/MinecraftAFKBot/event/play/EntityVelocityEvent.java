package fabian.hartman.MinecraftAFKBot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.event.Event;

@Getter
@AllArgsConstructor
public class EntityVelocityEvent extends Event {
    private short x;
    private short y;
    private short z;
    private int eid;
}