package fabian.hartman.MinecraftAFKBot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.event.Event;
import fabian.hartman.MinecraftAFKBot.network.entity.EntityDataValue;

import java.util.List;

@AllArgsConstructor
@Getter
public class EntityDataEvent extends Event {
    private int entityId;
    private List<EntityDataValue<?>> data;
}