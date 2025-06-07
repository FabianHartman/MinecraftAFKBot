package fabian.hartman.MinecraftAFKBot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.event.Event;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChatEvent extends Event {
    private String text;
    private UUID sender;
}