package fabian.hartman.MinecraftAFKBot.event.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.event.Event;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class LoginSuccessEvent extends Event {
    private UUID uuid;
    private String userName;
}