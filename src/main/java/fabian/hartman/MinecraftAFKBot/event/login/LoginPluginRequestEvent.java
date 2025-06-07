package fabian.hartman.MinecraftAFKBot.event.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.event.Event;

@Getter
@AllArgsConstructor
public class LoginPluginRequestEvent extends Event {
    private int msgId;
    private String channel;
    private byte[] data;
}