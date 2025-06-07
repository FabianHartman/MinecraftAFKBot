package fabian.hartman.MinecraftAFKBot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.event.Event;

@Getter
@AllArgsConstructor
public class SpawnEntityEvent extends Event {
    private int id;
    private int type;
    private double x;
    private double y;
    private double z;
    private byte yaw;
    private byte pitch;
    private int objectData;
    private short xVelocity;
    private short yVelocity;
    private short zVelocity;
}