package fabian.hartman.MinecraftAFKBot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.event.Event;

@AllArgsConstructor
@Getter
public class EntityMoveEvent extends Event {
    private int entityId;
    private short dX;
    private short dY;
    private short dZ;
    private Byte yaw;
    private Byte pitch;
    private boolean onGround;
}