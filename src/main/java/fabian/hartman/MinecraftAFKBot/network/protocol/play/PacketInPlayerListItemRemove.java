package fabian.hartman.MinecraftAFKBot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.play.UpdatePlayerListEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@Getter
public class PacketInPlayerListItemRemove extends Packet {
    private final Set<UUID> players = new HashSet<>();

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        int count = readVarInt(in);
        for (int i = 0; i < count; i++) {
            players.add(readUUID(in));
        }
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new UpdatePlayerListEvent(UpdatePlayerListEvent.Action.REMOVE, players));
    }
}