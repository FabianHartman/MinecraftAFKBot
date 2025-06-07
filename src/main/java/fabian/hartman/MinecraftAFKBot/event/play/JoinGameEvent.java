package fabian.hartman.MinecraftAFKBot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.event.Event;

@Getter
@AllArgsConstructor
public class JoinGameEvent extends Event {
    private int eid;
    private int gamemode;
    private String[] worldIdentifier;
    private String dimension;
    private String spawnWorld;
    private long hashedSeed;
    private int difficulty;
    private int maxPlayers;
    private int viewDistance;
    private int simulationDistance;
    private String levelType;
    private boolean reducedDebugInfo;
    private boolean enableRespawnScreen;
    private boolean debug;
    private boolean flat;
}