package fabian.hartman.MinecraftAFKBot.auth;

import net.minecraft.OneSixParamStorage;

import java.util.Optional;

public class Authenticator {
    public Optional<AuthData> authenticate() {
        OneSixParamStorage oneSix = OneSixParamStorage.getInstance();

        if (oneSix != null) {
            System.out.println("Changing authentication to use OneSix");
            return new OneSixAuthenticator().authenticate();
        }

        IAuthenticator authenticator = new MicrosoftAuthenticator();

        return authenticator.authenticate();
    }
}
