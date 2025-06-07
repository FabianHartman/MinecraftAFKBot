package fabian.hartman.MinecraftAFKBot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.event.Event;

@Getter
@AllArgsConstructor
public class PosLookChangeEvent extends Event {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private int teleportId;
}
