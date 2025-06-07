package fabian.hartman.MinecraftAFKBot.network.protocol.configuration;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.configuration.KnownPacksRequestedEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Getter
@NoArgsConstructor
public class PacketInKnownPacks extends Packet {
    private final List<KnownPack> knownPacks = new LinkedList<>();

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        int count = readVarInt(in);
        for (int i = 0; i < count; i++) {
            String name = readString(in);
            String id = readString(in);
            String version = readString(in);
            knownPacks.add(new KnownPack(name, id, version));
        }
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new KnownPacksRequestedEvent(knownPacks));
    }

    @Data
    public static class KnownPack {
        private final String namespace;
        private final String id;
        private final String version;
    }
}