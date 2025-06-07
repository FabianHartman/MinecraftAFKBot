package fabian.hartman.MinecraftAFKBot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.play.DestroyEntitiesEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class PacketInDestroyEntities extends Packet {
    private List<Integer> entityIds;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        int count = readVarInt(in);
        this.entityIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            getEntityIds().add(readVarInt(in));
        }

        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new DestroyEntitiesEvent(getEntityIds()));
    }
}