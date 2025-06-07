package fabian.hartman.MinecraftAFKBot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.bot.Slot;
import fabian.hartman.MinecraftAFKBot.event.Event;

import java.util.List;

@Getter
@AllArgsConstructor
public class UpdateWindowItemsEvent extends Event {
    private int windowId;
    private List<Slot> slots;
}