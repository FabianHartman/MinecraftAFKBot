package fabian.hartman.MinecraftAFKBot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.event.Event;

import java.util.List;

@Getter
@AllArgsConstructor
public class DestroyEntitiesEvent extends Event {
    private List<Integer> entityIds;
}
