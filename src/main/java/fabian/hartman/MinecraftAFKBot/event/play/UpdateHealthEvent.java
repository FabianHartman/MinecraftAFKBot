package fabian.hartman.MinecraftAFKBot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.event.Event;

@Getter
@AllArgsConstructor
public class UpdateHealthEvent extends Event {
    private int eid;
    private float health;
    private int food;
    private float saturation;
}