package fabian.hartman.MinecraftAFKBot.network.protocol.common;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@Getter
@AllArgsConstructor
public class PacketOutKeepAlive extends Packet {

    private long id;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        if(protocolId <= ProtocolConstants.MC_1_12_1) {
            writeVarInt(Long.valueOf(getId()).intValue(), out);
        } else {
            out.writeLong(id);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        // Only outgoing packet
    }
}