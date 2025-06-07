package fabian.hartman.MinecraftAFKBot.event.play;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import fabian.hartman.MinecraftAFKBot.event.Event;

@Getter
@RequiredArgsConstructor
public class OpenWindowEvent extends Event {
    private final int windowId;
    private final int windowType;
    private final String title;
}