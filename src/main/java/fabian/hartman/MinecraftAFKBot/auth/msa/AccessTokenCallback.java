package fabian.hartman.MinecraftAFKBot.auth.msa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class AccessTokenCallback {
    private final String accessToken;
    private final String refreshToken;
}