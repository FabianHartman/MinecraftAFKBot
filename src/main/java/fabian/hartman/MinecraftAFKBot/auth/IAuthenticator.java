package fabian.hartman.MinecraftAFKBot.auth;

import java.util.Optional;

public interface IAuthenticator {
    Optional<AuthData> authenticate();
}