package fabian.hartman.MinecraftAFKBot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.event.Event;

@Getter
@AllArgsConstructor
public class ConfirmTransactionEvent extends Event {
    private byte windowId;
    private short action;
    private boolean accepted;
}