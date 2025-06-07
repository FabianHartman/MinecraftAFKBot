package fabian.hartman.MinecraftAFKBot.event.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import fabian.hartman.MinecraftAFKBot.event.Event;
import fabian.hartman.MinecraftAFKBot.network.protocol.configuration.PacketInKnownPacks;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class KnownPacksRequestedEvent extends Event {
    private final List<PacketInKnownPacks.KnownPack> knownPacks;
}