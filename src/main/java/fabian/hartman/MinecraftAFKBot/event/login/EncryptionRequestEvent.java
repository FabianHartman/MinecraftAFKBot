package fabian.hartman.MinecraftAFKBot.event.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.event.Event;

import java.security.PublicKey;

@Getter
@AllArgsConstructor
public class EncryptionRequestEvent extends Event {
    private String serverId;
    private PublicKey publicKey;
    private byte[] verifyToken;
    private boolean shouldAuthenticate;
}
