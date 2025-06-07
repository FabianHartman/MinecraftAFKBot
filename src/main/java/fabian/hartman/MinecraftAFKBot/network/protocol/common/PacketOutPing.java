package fabian.hartman.MinecraftAFKBot.network.protocol.common;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

@Getter
@AllArgsConstructor
public class PacketOutPing extends Packet {
    private int id;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeInt(id);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        // Only outgoing packet
    }
}