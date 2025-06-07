package fabian.hartman.MinecraftAFKBot.network.mojangapi;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Realm {
    private long id;
    private String name;
    private String owner;
    private String motd;
}