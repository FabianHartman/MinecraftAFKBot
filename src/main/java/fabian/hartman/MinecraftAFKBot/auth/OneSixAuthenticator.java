package fabian.hartman.MinecraftAFKBot.auth;

import net.minecraft.OneSixParamStorage;
import fabian.hartman.MinecraftAFKBot.utils.UUIDUtils;

import java.util.Optional;

public class OneSixAuthenticator implements IAuthenticator {
    @Override
    public Optional<AuthData> authenticate() {
        System.out.println("Trying to authenticate using OneSix arguments...");
        OneSixParamStorage oneSix = OneSixParamStorage.getInstance();
        if (oneSix != null) {
            String uuid = UUIDUtils.withDashes(oneSix.getUuid());
            return Optional.of(new AuthData(oneSix.getAccessToken(), uuid, oneSix.getUsername()));
        }
        return Optional.empty();
    }
}
