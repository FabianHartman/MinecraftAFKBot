package fabian.hartman.MinecraftAFKBot.network.protocol.common;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PacketOutResourcePackResponse extends Packet {
    private UUID uuid;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        if (protocolId >= ProtocolConstants.MC_1_20_3)
            writeUUID(uuid, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        // Only outgoing packet
    }
}